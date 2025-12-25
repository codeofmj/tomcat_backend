package com.smhrd.board.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.smhrd.board.dto.BoardCreateRequest;
import com.smhrd.board.entity.Board;
import com.smhrd.board.repository.BoardRepository;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

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
}
