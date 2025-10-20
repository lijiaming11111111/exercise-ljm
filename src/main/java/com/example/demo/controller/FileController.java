package com.example.demo.controller;

import com.example.demo.result.Result;
import com.example.demo.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

@CrossOrigin
@Slf4j
@RequiredArgsConstructor
@Tag(name = "文件接口")
@Validated
@RestController
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    /**
     * fileName 上传的文件名
     * @param file 上传的文件
     * @return result
     */
    @PostMapping("/upload")
    @Operation(summary = "手动上传文件")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.success("请选择要上传的文件");
        }
        try {
            String fileKey = fileService.uploadFile(file);
            return Result.success("文件上传成功:" + fileKey);
        } catch (Exception e) {
            return Result.success("失败原因:" + e.getMessage());
        }
    }

    /**
     * fileName 用于生成预签名 URL 的文件名
     * @param file 相关文件
     * @return result
     */
    @PutMapping("/uploadUrl")
    @Operation(summary = "生成url")
    public Result<URL> uploadUrl(@RequestParam("file") MultipartFile file) throws IOException {
        return Result.success("url生成成功",fileService.url(file));
    }

    /**
     * fileName 用于生成预签名 URL 的文件名
     * @param file 相关文件
     * @return result
     */
    @PutMapping("/uploadUrlFile")
    @Operation(summary = "上传url文件")
    public Result<String> uploadUrlFile(String url,@RequestParam("file") MultipartFile file) throws IOException {
        return Result.success(fileService.uploadFileUrl(url,file));
    }

    /**
     * fileName 下载的文件名
     * @param fileName 要下载的文件名称
     * @return result
     */
    @GetMapping("/downloadUrl")
    @Operation(summary = "下载的url")
    public Result<String> downloadUrl(@RequestParam("name") String fileName) {
        return Result.success(fileService.generateDownloadUrl(fileName),null);
    }

    /**
     * fileName 删除的文件铭
     * @param file 删除的文件
     * @return result
     */
    @DeleteMapping("/deleteFile")
    @Operation(summary = "删除文件")
    public Result<String> deleteFile(@RequestParam("file") String file) throws IOException {
        return Result.success(fileService.deleteFile(file),null);
    }
}
