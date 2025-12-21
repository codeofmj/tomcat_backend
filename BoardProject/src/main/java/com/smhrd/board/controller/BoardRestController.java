package com.smhrd.board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smhrd.board.entity.Board;
import com.smhrd.board.service.BoardService;

@RestController
public class BoardRestController {

	@Autowired
	private BoardService service;
   
	//게시글 조회요청
	@CrossOrigin(origins = "http://127.0.0.1:5500", allowedHeaders = "*")
	@GetMapping("/list")
	public List<Board> list(){
		System.out.println(service.getList());
		return service.getList();
	}
	
	//게시글 작성요청
	@PostMapping("/register")
	public String register(Board vo) {
		service.register(vo);
		return "success";
	}
	
}
