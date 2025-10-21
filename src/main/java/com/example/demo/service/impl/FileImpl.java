package com.example.demo.service.impl;


import com.example.demo.service.FileService;
import com.example.demo.util.FileUtil;
import com.example.demo.vo.file.FileUrlVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;


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
    public FileUrlVO url(MultipartFile file) {
        return fileUtil.url(file);
    }

    @Override
    public String uploadFileUrl(String url, MultipartFile file) throws IOException {
        return fileUtil.uploadFileUrl(url,file);
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
