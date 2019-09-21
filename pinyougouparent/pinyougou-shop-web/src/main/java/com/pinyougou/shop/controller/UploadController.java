package com.pinyougou.shop.controller;

import entities.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import utils.FastDFSClient;

@RestController
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/upload")
    public Message upload(MultipartFile file){
        try {
            //初始化fastDFS工具类实例
            FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
            //获取文件全名
            String originalFilename = file.getOriginalFilename();
            //获取文件后缀名
            String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            //上传文件, 获取文件在服务器上的存储地址
            String saveAddr = client.uploadFile(file.getBytes(), suffix);
            String visitAddr = FILE_SERVER_URL + saveAddr;
            return new Message(true, visitAddr);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message(false,"上传失败");
        }
    }
}
