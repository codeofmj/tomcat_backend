package com.smhrd.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smhrd.board.entity.Board;
import com.smhrd.board.repository.BoardRepository;

@Service
public class BoardService {

	@Autowired
	private BoardRepository repository;
	
	//게시글 전체보기 기능
	public List<Board> getList(){
		return repository.findAll();
	}
	
	//게시글 입력
	public void register(Board vo) {
		repository.save(vo);
	}
}
