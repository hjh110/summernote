package com.newer.summernote.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
public class UploadController {


    @RequestMapping(value = "/customerRecord_upload", method = RequestMethod.POST)
    public Map<String, Object> uploadHandle(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进来");
        Map<String, Object> map = new HashMap<String, Object>(0);
        map.clear();


        MultipartFile multipartFile = null;

        // 转型为MultipartHttpRequest：
        if (request instanceof MultipartHttpServletRequest) {

            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            // 获取对应file对象
            Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
            Iterator<String> fileIterator = multipartRequest.getFileNames();
            while (fileIterator.hasNext()) {
                String fileKey = fileIterator.next();
                // 获取对应文件
                multipartFile = fileMap.get(fileKey);
                if (multipartFile.getSize() != 0L) {
                    // 日期目录
                    String imageDir = "admin/upload/customerRecord/" + new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    //String imageDir="C:\\Users\\javahjh\\IdeaProjects\\hejunhao\\src\\main\\resources\\static/upload/"+ new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    //不能存在项目下，因为他不会实时刷新

                    String realPath = request.getSession().getServletContext().getRealPath(imageDir);
                    //String realPath =imageDir;
                    System.out.println("request.getServletContext():" + request.getServletContext());
                    System.out.println(realPath);
                    System.out.println(request.getServletContext().getRealPath(imageDir));
                    File dir = new File(realPath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    // 获取上传文件原始名称
                    String fileName = multipartFile.getOriginalFilename();
                    // 获取文件的扩展名
                    String ext = fileName.substring(fileName.lastIndexOf("."));
                    String tmpExt = ext.toLowerCase(); // 验证图片类型
                    if (!tmpExt.contains(".jpg") && !tmpExt.contains(".gif") && !tmpExt.contains(".png") && !tmpExt.contains(".jpeg")) {
                        map.put("msg", "只能上传jpg、gif、png类型的图片。");
                        map.put("data", "");
                        return map;
                    }
                    // 重新给文件命名
                    String banName = String.valueOf(System.currentTimeMillis() + (int) (Math.random() * 100 + 1));
                    String newName = banName + ext;
                    // 封装为文件对象
                    File targetFile = new File(realPath, newName);
                    try {
                        // 上传文件
                        multipartFile.transferTo(targetFile);
                        //返回给前端页面的图片

                        String path = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "//" + imageDir + "//" + newName;
                        System.out.println("返回给前端页面的图片:" + path);
                        map.put("data", path);
                        map.put("msg", "上传成功");
                    } catch (Exception e) {
                        map.put("data", "");
                        map.put("msg", "上传失败");
                    }
                }
            }
        }
        System.out.println("是文件" + request.getParameter("id"));

        return map;
    }
}
