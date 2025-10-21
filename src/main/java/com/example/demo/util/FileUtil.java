package com.example.demo.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.demo.entity.File;
import com.example.demo.entity.Mail;
import com.example.demo.mapper.FileMapper;
import com.example.demo.result.Result;
import com.example.demo.vo.file.FileUrlVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;

import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;


import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class FileUtil {

    private final S3Client s3Client;

    @Autowired
    private S3Presigner s3Presigner;

    private final String bucketName;

    private final FileMapper fileMapper;

    private final RestTemplate restTemplate;

    public FileUtil(S3Client s3Client, @Value("${tebi.bucket-name}") String bucketName, FileMapper fileMapper, RestTemplate restTemplate) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.fileMapper = fileMapper;
        this.restTemplate = restTemplate;
    }

    /**
     * 上传文件到 TEBI 桶
     * @param file 待上传的文件
     * @return 上传后的文件路径（S3 中的 key）
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // 生成唯一的文件名，避免重复
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        // 构建上传请求
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)  // S3 中的文件标识（key）
                .contentType(file.getContentType())
                .build();

        // 执行上传
        s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        //数据库
        File sqlFile=new File();
        sqlFile.setId(IdWorker.getId());
        sqlFile.setFileName(file.getOriginalFilename());
        sqlFile.setObjectName(fileName);
        sqlFile.setBucketName(bucketName);
        sqlFile.setUploadTime(LocalDateTime.now());
        fileMapper.insert(sqlFile);
        return fileName;  // 返回文件在 S3 中的 key
    }

    /**
     * 生成文件下载的URL
     *
     * @param fileName 要下载的文件名
     * @return 下载URL
     */
    public String generateDownloadUrl(String fileName) {
        try {
            // 检查文件是否存在
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            s3Client.headObject(headRequest);
            // 生成预签名 GET 请求
            Map<String, String> responseHeaders = new HashMap<>();
            // attachment 表示附件下载，filename 可指定下载后的文件名
            responseHeaders.put("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .responseContentDisposition("attachment; filename=\"" + fileName + "\"")
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(
                    request -> request
                            .getObjectRequest(getObjectRequest)
                            .signatureDuration(Duration.ofDays(1)) // 设置 URL 有效期
            );

            // 获取预签名 URL 并返回
            URL presignedUrl = presignedRequest.url();
            return "url成功生成为:"+presignedUrl.toString();
        } catch (SdkException e) {
            // 文件不存在或 S3 访问错误
            return "文件不存在或无法访问";
        }
    }

    /**
     * 生成文件可用于PUT上传的预签名URL
     *
     * @param file 相关文件
     * @return 预签名URL
     */
    public FileUrlVO url(MultipartFile file)  {
        try {
            //获取原始文件名
            String originalName = file.getOriginalFilename();
            //获取文件扩展名
            String fileExt = originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : "";
            //文件唯一ID
            String uniqueFileName =  UUID.randomUUID() + fileExt;

            //指定存储桶
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFileName)
                    .contentType(file.getContentType())
                    .build();
            //生成url
            PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(
                    (builder) -> builder.putObjectRequest(putObjectRequest)
                            .signatureDuration(Duration.ofMinutes(30))
            );

            URL presignedUrl = presignedPutObjectRequest.url();
            //数据库
            File sqlFile=new File();
            sqlFile.setId(IdWorker.getId());
            sqlFile.setFileName(originalName);
            sqlFile.setObjectName(uniqueFileName);
            sqlFile.setBucketName(bucketName);
            sqlFile.setUploadTime(LocalDateTime.now());
            fileMapper.insert(sqlFile);
            fileMapper.selectById(sqlFile.getId());
            FileUrlVO fileUrlVO = new FileUrlVO();
            fileUrlVO.setUrl(presignedUrl);
            fileUrlVO.setId(sqlFile.getId());
            return fileUrlVO;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "生成URL失败：" + e.getMessage());
            return null;
        }
    }

    /**
     * 删除文件
     *
     * @param file 要删除的文件标识
     * @return 删除结果相关信息
     * @throws IOException 处理文件删除时可能抛出的IO异常
     */
    public String deleteFile(String file) throws IOException {
        try {
            DeleteObjectRequest deleteObjectRequest= DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(file)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            QueryWrapper<File> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("object_name", file); // 字段名需与数据库一致
            fileMapper.delete(queryWrapper);
            return "删除成功";
        }catch (S3Exception e) {
            return "删除失败";
        }
    }
    /**
     * 通过预签名 URL 上传文件
     * @param url 已生成的预签名 URL
     * @param file 要上传的文件
     * @return 上传结果（成功/失败信息）
     */
    public String uploadFileUrl(String url, MultipartFile file) {
        try {
            // 1. 检查文件是否为空
            if (file.isEmpty()) {
                return "上传失败：文件为空";
            }
            String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8);
            // 2. 设置请求头（根据 S3 协议，需指定文件 Content-Type）
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(file.getContentType()));
            headers.setContentLength(file.getSize());

            // 3. 构建请求体（文件字节流）
            HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

            // 4. 发送 PUT 请求到预签名 URL
            ResponseEntity<Void> response = restTemplate.exchange(
                    decodedUrl,
                    HttpMethod.PUT,
                    requestEntity,
                    Void.class
            );

            // 5. 检查响应状态（200/204 表示成功）
            if (response.getStatusCode().is2xxSuccessful()) {
                return "文件上传成功";
            } else {
                return "文件上传失败，状态码：" + response.getStatusCodeValue();
            }

        } catch (IOException e) {
            return "文件读取失败：" + e.getMessage();
        } catch (Exception e) {
            return "上传请求失败：" + e.getMessage();
        }
    }
}
