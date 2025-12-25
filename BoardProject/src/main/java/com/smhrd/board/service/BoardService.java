package com.smhrd.board.service;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.smhrd.board.dto.BoardCreateRequest;
import com.smhrd.board.entity.Board;
import com.smhrd.board.repository.BoardRepository;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class BoardService {

	@Autowired
	private S3Client s3Client;

	@Value("${ncp.bucket-name}")
	private String bucketName;

	@Value("${ncp.end-point}")
	private String endPoint;

	@Value("${file.upload-dir}")
	private String uploadDir;

	@Autowired
	private BoardRepository repository;

	// 게시글 전체보기 기능
	public List<Board> getList() {
		return repository.findAll();
	}

//	// 게시글 입력(로컬저장)
//	public void register(BoardCreateRequest req) throws Exception {
//
//		// 업로드 후 저장될 파일의 경로
//		String savedPath = null;
//
//		// 요청 DTO에서 파일 꺼내기
//		MultipartFile file = req.getB_file();
//
//		// 파일이 존재하고, 비어있지 않다면
//		if (file != null && !file.isEmpty()) {
//
//			// 1.파일명 충돌 방지를 위해 UUID에 원본파일명 조합
//			String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//
//			// 2.서버에 저장할 폴더명
//			Path uploadFolder = Paths.get(uploadDir);
//
//			// 3.실제 저장될 파일 경로
//			Path targetPath = uploadFolder.resolve(fileName);
//
//			// 4.파일 저장
//			Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
//
//			// 5.DB에 저장할 경로 문자열 생성
//			savedPath = uploadFolder + fileName;
//			System.out.println("이미지저장경로: " + savedPath);
//		}
//
//		// DB 저장용 Entity로 변환
//		Board vo = new Board();
//		vo.setB_title(req.getB_title());
//		vo.setB_writer(req.getB_writer());
//		vo.setB_content(req.getB_content());
//		vo.setB_file_path(savedPath);
//
//		repository.save(vo);
//	}

	// 게시글 입력(NCP Object Storage)
	public void register(BoardCreateRequest req) throws Exception {

		// 업로드 후 저장될 파일의 경로
		String savedPath = null;

		// 요청 DTO에서 파일 꺼내기
		MultipartFile file = req.getB_file();

		// 파일이 존재하고, 비어있지 않다면
		if (file != null && !file.isEmpty()) {

			// 1.파일명 충돌 방지를 위해 UUID에 원본파일명 조합
			String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // 2.NCP Object Storage에 저장
            // - PutObjectRequest: Object Storage에 파일 업로드하기 위한 요청 객체 생성
            //   -> 버킷이름, 파일이름, 파일형식(MIME), 파일접근권한
            // - s3Client.putObject()
            //   -> 준비된 요청객체와 파일데이터를 Object Storage로 파일 전송
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            
			// 5.DB에 저장할 경로 문자열 생성
			savedPath = endPoint+"/"+bucketName+"/"+fileName;
			System.out.println(savedPath);
		}

		// DB 저장용 Entity로 변환
		Board vo = new Board();
		vo.setB_title(req.getB_title());
		vo.setB_writer(req.getB_writer());
		vo.setB_content(req.getB_content());
		vo.setB_file_path(savedPath);

		repository.save(vo);
	}
	
	//게시글 조회
	public Board getDetail(Long b_idx) {
		
		//findById(): 특정 데이터 조회
		//orElseThrow(): 값이 있으면 반환하고 없으면 예외처리는 던지는 메소드
		//()->new IllegalArgumentException: 람다식 표현법
		Optional<Board> optionalBoard = repository.findById(b_idx);
		
		if(optionalBoard.isEmpty()) {
			throw new IllegalArgumentException("게시글이 존재하지 않습니다. id="+b_idx);
		}
		
		return optionalBoard.get();
		
	}
	
	//게시글 첨부파일 다운로드 
	public ResponseEntity<Resource> downloadAttachment(Long b_idx) throws Exception{
	    Board board = repository.findById(b_idx)
	            .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

	        // 첨부파일 없으면 404
	    	String url = board.getB_file_path();
	        if (url == null || url.isBlank()) {
	            return ResponseEntity.notFound().build();
	        }
	        
	        
	        // Object Storage "key" 추출
	        // URL구조: endPoint + "/" + bucketName + "/" + key(파일명)
	        // -> "/bucketName/" 뒤가 바로 key
	        String marker = "/" + bucketName + "/";
	        int idx = url.indexOf(marker);
	        
	        // URL 형식이 다르다면 잘못된 요청으로 응답
	        if(idx <0) return ResponseEntity.badRequest().build();
	        
	        //실제 Object Storage key
	        String key = url.substring(idx + marker.length());
	        
	        //Object Storage에서 파일 가져오기
	        GetObjectRequest req = GetObjectRequest.builder()
	        		.bucket(bucketName)
	        		.key(key)
	        		.build();
	        
	        //Object Storage에서 파일 읽기
	        ResponseInputStream<GetObjectResponse> ncpObjStream = s3Client.getObject(req);
	        GetObjectResponse meta = ncpObjStream.response();
	        
	        
	        //파일 전체를 byte[]로 읽기
	        byte[] bytes = ncpObjStream.readAllBytes();
	        
	        //byte[]를 String이 응답으로 내려줄 수 있는 Resource로 감싸주는 작업
	        ByteArrayResource resource = new ByteArrayResource(bytes);
	        
	        // 다운로드 파일명: key의 마지막 파일명만 사용
	        // ex) board/uuid_file.png -> uuid_file.png 추출
	        String filename = key.contains("/") ? key.substring(key.lastIndexOf("/")+1):key;
	        System.out.println(key);
	        
	        // Content-Disposition 헤더 생성
	        // -> 이 응답을 브라우저가 어떻게 처리할지 지시하는 설명서
	        // attachment -> "무조건 다운로드"
	        // filename -> 다운로드될 파일명
	        ContentDisposition cd = ContentDisposition.attachment()
	        		.filename(filename, StandardCharsets.UTF_8)
	        		.build();
	        
	        // 최종 응답 생성
	        // 다운로드 강제 헤더 설명 -> 브라우저가 열지 못하게 강제 -> 파일 크기 명시 -> 실제 파일 데이터
	        return ResponseEntity.ok()
	        		.header(HttpHeaders.CONTENT_DISPOSITION, cd.toString())
	        		.contentType(MediaType.APPLICATION_OCTET_STREAM)
	        		.contentLength(bytes.length)
	        		.body(resource);
	}
	

	
	
	
}
