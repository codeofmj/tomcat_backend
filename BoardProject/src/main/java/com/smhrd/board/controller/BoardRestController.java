package com.smhrd.board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	@GetMapping("/{b_idx}")
	public Board getDetail(@PathVariable("b_idx") Long b_idx) {
		Board vo = service.getDetail(b_idx);
		System.out.println("조회된 데이터: "+vo);
		
		return vo;
	}
	
	@GetMapping("/{b_idx}/download")
	public ResponseEntity<Resource> download(@PathVariable("b_idx") Long id) {
        try {
			return service.downloadAttachment(id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
    }
	
}
