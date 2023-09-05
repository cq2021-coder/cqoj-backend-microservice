package com.cq.user.controller;

import com.cq.common.response.CommonResponse;
import com.cq.user.service.FileService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 文件控制器
 *
 * @author 程崎
 * @since 2023/08/03
 */
@RestController
@RequestMapping("/file")
@Api(tags = "file")
public class FileController {

    @Resource
    private FileService fileService;


    @PostMapping("/upload")
    public CommonResponse<String> upload(@RequestPart MultipartFile file) {
        return CommonResponse.success(fileService.fileUpload(file));
    }

    @PostMapping("/tmp")
    public CommonResponse<String> getTempAccess(String key) {
        return CommonResponse.success(fileService.getTmpAccess(key));
    }
}
