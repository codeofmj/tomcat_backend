package com.smhrd.board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smhrd.board.entity.Board;
import com.smhrd.board.service.BoardService;
import org.springframework.web.bind.annotation.RequestParam;


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
	
	//게시글 작성요청
	@PostMapping("/register")
	public String register(@RequestParam String b_title, @RequestParam String b_content, @RequestParam String b_writer) {
		
		System.out.println(b_title+"/"+b_content+"/"+b_writer);
		
		//service.register(vo);
		return "success";
	}
	
	//게시글 특정 글 조회요청
	@GetMapping("/b_idx")
	public Board getMethodName(@RequestParam("b_idx") Long b_idx) {
		System.out.println("게시글 번호>> "+b_idx);
		return null;
	}
	
	
}
