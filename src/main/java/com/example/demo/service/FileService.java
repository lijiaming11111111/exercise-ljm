package com.example.demo.service;

import com.example.demo.vo.file.FileUrlVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public interface FileService {
    /**
     * 文件上传
     *
     * @param file 要上传的文件
     * @return 上传后文件相关标识或路径等信息
     * @throws IOException 处理文件上传时可能抛出的IO异常
     */
    String uploadFile(MultipartFile file) throws IOException;

    /**
     * 生成文件可用于PUT上传的预签名URL
     *
     * @param file 相关文件
     * @return 预签名URL
     * @throws IOException 处理URL生成时可能抛出的IO异常
     */
    FileUrlVO url(MultipartFile file) throws IOException;

    /**
     * 生成文件可用于PUT上传的预签名URL
     *
     * @param file 相关文件
     * @return 预签名URL
     * @throws IOException 处理URL生成时可能抛出的IO异常
     */
    String uploadFileUrl(String url, MultipartFile file) throws IOException;

    /**
     * 生成文件下载的URL
     *
     * @param fileName 要下载的文件名
     * @return 下载URL
     */
    String generateDownloadUrl(String fileName);

    /**
     * 删除文件
     *
     * @param file 要删除的文件标识
     * @return 删除结果相关信息
     * @throws IOException 处理文件删除时可能抛出的IO异常
     */
    String deleteFile(String file)throws IOException;

}
