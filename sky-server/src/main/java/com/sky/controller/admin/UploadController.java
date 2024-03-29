package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.properties.AliOssProperties;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common/upload")
@Slf4j
@Api(tags = "文件上传接口")
public class UploadController {

    @Autowired
    AliOssUtil aliOssUtil;

    @PostMapping
    public Result<String> upload(@RequestBody MultipartFile file) {
        try {
            String[] split = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
            if(split.length == 0){
                return Result.error("文件格式错误");
            }
            String filename = UUID.randomUUID().toString() + "." + split[split.length - 1];
            String result = aliOssUtil.upload(file.getBytes(), filename);
            return Result.success(result);
        } catch (IOException e) {
            log.info("文件上传失败：{}", e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
