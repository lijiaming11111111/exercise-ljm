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

    /**
     * 文件上传
     *
     * @param file 要上传的文件
     * @return 上传后文件相关标识或路径等信息
     * @throws IOException 处理文件上传时可能抛出的IO异常
     */
    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        return fileUtil.uploadFile(file);
    }

    /**
     * 生成文件可用于PUT上传的预签名URL
     *
     * @param file 相关文件
     * @return 预签名URL
     */

    @Override
    public String url(MultipartFile file) {
        return fileUtil.url(file);
    }

    /**
     * 生成文件下载的URL
     *
     * @param fileName 要下载的文件名
     * @return 下载URL
     */
    @Override
    public String generateDownloadUrl(String fileName) {
        return fileUtil.generateDownloadUrl(fileName);
    }

    /**
     * 删除文件
     *
     * @param file 要删除的文件标识
     * @return 删除结果相关信息
     * @throws IOException 处理文件删除时可能抛出的IO异常
     */
    @Override
    public String deleteFile(String file)throws IOException{
        return fileUtil.deleteFile(file);
    }
}
