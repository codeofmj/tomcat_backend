package com.smhrd.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smhrd.board.entity.Board;

//예전 Mapper랑 같은 역할을 하는 클래스
@Repository
public interface BoardRepository extends JpaRepository<Board, Long>{ // 테이블명, PK 데이터 타입
	//유저 -> Controller -> Service -> Repository -> Hibernate -> DB접속
}
