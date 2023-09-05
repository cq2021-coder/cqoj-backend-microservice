package com.cq.user.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务
 *
 * @author 程崎
 * @since 2023/08/03
 */
public interface FileService {


    /**
     * 文件上传
     *
     * @param file 文件
     * @return {@link String}
     */
    String fileUpload(MultipartFile file);

    /**
     * 获得临时访问
     *
     * @param key 关键
     * @return {@link String}
     */
    String getTmpAccess(String key);
}
