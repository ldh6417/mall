package com.kh.mallapi.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Getter 
@ToString 
@Builder 
@AllArgsConstructor 
@NoArgsConstructor 
@Embeddable //포링키 도구
public class ProductImage {
	// 참조키: pno 
	//이미지 파일명 
	private String fileName;
	//이미지 파일 순서
	private int ord;
	
	public void setOrd(int ord) {
		this.ord = ord;
	}
}
