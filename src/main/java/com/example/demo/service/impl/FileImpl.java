package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.demo.entity.Mail;
import com.example.demo.service.FileService;
import com.example.demo.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileImpl implements FileService {

    private final FileUtil fileUtil;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        return fileUtil.uploadFile(file);
    }

    @Override
    public String url(MultipartFile file) {
        return fileUtil.url(file);
    }

    @Override
    public String generateDownloadUrl(String fileName) {
        return fileUtil.generateDownloadUrl(fileName);
    }
}
