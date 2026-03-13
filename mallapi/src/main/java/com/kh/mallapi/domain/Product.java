package com.kh.mallapi.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "tbl_product")
@Getter
@ToString(exclude = "imageList")
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Product {

	@Id // 프라이머리키 도구
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRODUCT_SEQ_GEN") // 시퀀스 도구
	private Long pno;
	private String pname;
	private int price;
	private String pdesc;
	private boolean delFlag;
	
	@ElementCollection // 조인시켜주는 도구
	@Builder.Default
	private List<ProductImage> imageList = new ArrayList<>();

	// setter => change
	public void changePrice(int price) {
		this.price = price;
	}

	public void changeDesc(String desc) {
		this.pdesc = desc;
	}

	public void changeName(String name) {
		this.pname = name;
	}

	public void changeDel(boolean delFlag) {
		this.delFlag = delFlag;
	}

	public void addImage(ProductImage image) {
		// 이미지 추가시 순서(ord) 자동 설정 (0, 1, 2, ...)
		image.setOrd(this.imageList.size());
		imageList.add(image);
	}

	public void addImageString(String fileName) {
		ProductImage productImage = ProductImage.builder().fileName(fileName).build();
		addImage(productImage);
	}

	public void clearList() {
		this.imageList.clear();
	}

}
