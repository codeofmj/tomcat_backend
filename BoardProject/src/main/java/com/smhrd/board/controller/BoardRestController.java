package com.smhrd.board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smhrd.board.dto.BoardCreateRequest;
import com.smhrd.board.entity.Board;
import com.smhrd.board.service.BoardService;


@RestController
@RequestMapping("/api/board")
@CrossOrigin(origins = "http://127.0.0.1:5500", allowedHeaders = "*")
//@CrossOrigin(origins = "http://10.2.1.6", allowedHeaders = "*")
public class BoardRestController {

	@Autowired
	private BoardService service;
	
	//게시글 조회요청
	@GetMapping("/list")
	public List<Board> list(){
		System.out.println(service.getList());
		return service.getList();
	}
	
	//게시글 작성요청(로컬)
	//consumes: 들어오는 데이터 타입을 정의할 때 설정
	//JSON 타입을 받고 싶다면? MediaType.APPLICATION_JSON_VALUE
	//단, 요청을 하는 곳에서는 반드시 Content-Type:application/json 명시 필요
	@PostMapping(value="/register", consumes="multipart/form-data")
	public String register(@ModelAttribute BoardCreateRequest req) throws Exception {

		try {
			
			service.register(req);
			return "success";
			
		}catch (Exception e) {

			e.printStackTrace();
			return "fail";
				
		}

	}
	
	//게시글 작성요청(NCP Object Storage 저장)
//	@PostMapping(value="/register", consumes="multipart/form-data")
//	public String register(@ModelAttribute BoardCreateRequest req) throws Exception {
//
//		//업로드 후 저장될 파일의 경로
//		String savedPath = null;
//		
//		//요청 DTO에서 파일 꺼내기
//		MultipartFile file = req.getB_file();
//		
//		//파일이 존재하고, 비어있지 않다면
//		if(file != null && !file.isEmpty()) {
//			
//			//1.파일명 충돌 방지를 위해 UUID에 원본파일명 조합
//			String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//			
//			//2.NCP Object Storage 저장을 위한 객체 생성
//			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//					.bucket(bucketName)
//					.key(fileName)
//					.contentType(file.getContentType())
//					.acl(ObjectCannedACL.PUBLIC_READ)
//					.build();
//			
//			s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
//			
//			//3.DB에 저장할 경로 문자열 생성
//			savedPath = "/uploads/" + fileName;
//		}
//		
//		// DB 저장용 Entity로 변환
//		Board board = new Board();
//		board.setB_title(req.getB_title());
//		board.setB_writer(req.getB_writer());
//		board.setB_content(req.getB_content());
//		board.setB_file_path(savedPath);
//		
//		// 실제 DB 저장
//		service.register(board);
//		
//
//		return "success";
//	}
		
	
}
