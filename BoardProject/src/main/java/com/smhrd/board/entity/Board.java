package com.smhrd.board.entity;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity //DB 해당 클래스 테이블 설계
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Board {

	@Id //PK
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 1씩 자동으로 증가
	private Long b_idx; // 게시글 고유번호 -> JPA int -> Long 설정
	
	private String b_title; 
	
	@Column(length = 2000) // 기본값: 255
	private String b_content;
	
	@Column(updatable = false) // 수정할 때 작성자는 변경 X
	private String b_writer;
	
	@Column(length = 255)
	private String b_file_path;
	
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	@Column(insertable = false, updatable = false, columnDefinition = "datetime default now()") // 날짜는 입력X, 수정X, 자동으로 저장되게 설정  
	private LocalDateTime b_datetime;
	
	@Column(insertable = false, updatable = false, columnDefinition = "bigint default 0")
	private Long b_count; // 조회수 int -> Long 설정
	
}