package com.smhrd.board.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardCreateRequest {
	private String b_title;
	private String b_writer;
	private String b_content;
	private MultipartFile b_file;
}
