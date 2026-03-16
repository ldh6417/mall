package com.kh.mallapi.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;

@Component
@Log4j2
@RequiredArgsConstructor
public class CustomFileUtil {

	@Value("${com.kh.upload.path}")
	private String uploadPath;

	@PostConstruct
	// CustomFileUtil 객채로드되면서, init 자동으로 실행해줘.
	public void init() {
		File tempFolder = new File(uploadPath);
		if (tempFolder.exists() == false) {
			tempFolder.mkdir();
		}
		uploadPath = tempFolder.getAbsolutePath();

		log.info("tempFolder.getAbsolutePath();" + uploadPath);
		if (tempFolder.exists() == false) {
			tempFolder.mkdir();
		}

		// 경로 설정 및 로그 출력 c:\SpringBootProject\workspace\ mallapi \ upload 절대경로
		uploadPath = tempFolder.getAbsolutePath();
		log.info("-------------------------------------");
		log.info("Upload Path initialized: " + uploadPath);
		log.info("-------------------------------------");
	}

	public List<String> saveFiles(List<MultipartFile> files) throws RuntimeException {
		// 절대 중복되지않는 파일명을 만들어서 저장하는 리스트
		List<String> uploadNames = new ArrayList<>();

		// size() == 0 대신 isEmpty() 권장
		if (files == null || files.isEmpty()) {
			uploadNames.add("default.jpg");
			return uploadNames;
		}

		for (MultipartFile multipartFile : files) {
			String savedName = UUID.randomUUID().toString() + "_" + multipartFile.getOriginalFilename();
			Path savePath = Paths.get(uploadPath, savedName);

			try {
				Files.copy(multipartFile.getInputStream(), savePath);
				// 파일의 타입: kdj.jpg => jpg 파일 타입이다.
				String contentType = multipartFile.getContentType();
				// 썸네일 생성
				// 타입을 체크 검토 필터링
				if (contentType != null && contentType.endsWith("image")) {
					// 썸네일 파일명 생성
					Path thumbnailPath = Paths.get(uploadPath, "s_" + savedName);
					// 원본파일을 가로폭(400),세로폭(400)변경 thumbnailPath.toFile()저장
					Thumbnails.of(savePath.toFile()).size(400, 400).toFile(thumbnailPath.toFile());
				}
			} catch (IOException e) {
				uploadNames.add(savedName);
				throw new RuntimeException("File save error: " + e.getMessage());
			}
		}
		return uploadNames;
	}

	// 브라우저에게 화면을 보여주는 기능 당담함수
	public ResponseEntity<Resource> getFile(String fileName) {
		//
		Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
		// 보낼파일이 존재하는지 체크
		if (!resource.exists()) {
			resource = new FileSystemResource(uploadPath + File.separator + "default.jpg");
		}
		// 웹 브라우저에 보낼 header
		HttpHeaders headers = new HttpHeaders();
		try {
			// Files.probeContentType()은 파일 경로를 분석하여 MIME 타입을 자동 감지 jpg → image/jpeg, png →
			// image/png pdf → application/pdf 이 정보를 HTTP 응답 헤더에 Content-Type으로 추가한다
			headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}

		return ResponseEntity.ok().headers(headers).body(resource);
	}

	public void deleteFiles(List<String> fileNames) {
		if (fileNames == null || fileNames.isEmpty()) {
			return;
		}

		fileNames.forEach(fileName -> {
			// 썸네일이 있는지 확인하고 삭제
			String thumbnailFileName = "s_" + fileName;
			// 썸네일이미지 경로 확인
			Path thumbnailPath = Paths.get(uploadPath, thumbnailFileName);
			// 원본 이미지 경로
			Path filePath = Paths.get(uploadPath, fileName);
			try {
				Files.deleteIfExists(filePath);
				Files.deleteIfExists(thumbnailPath);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		});
	}

}
