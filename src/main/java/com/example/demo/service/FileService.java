package com.example.demo.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface FileService {
    String uploadFile(MultipartFile file) throws IOException;

    String url(MultipartFile file) throws IOException;

    String generateDownloadUrl(String fileName);

    String deleteFile(String file)throws IOException;

}
