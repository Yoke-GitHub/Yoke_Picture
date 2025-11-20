package com.yoke.yokepicturebackend.manager.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.yoke.yokepicturebackend.exception.BusinessException;
import com.yoke.yokepicturebackend.exception.ErrorCode;
import com.yoke.yokepicturebackend.exception.ThrowUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Url图片上传
 */
@Service
public class UrlPictureUpload extends PictureUploadTemplate {
    @Override
    protected void validPicture(Object inputSource) {
        String fileUrl = (String) inputSource;
        // 1.校验非空
        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), ErrorCode.PARAMS_ERROR, "url不能为空");
        // 2.校验url格式
        try {
            new URL(fileUrl);
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "url格式错误");
        }
        // 3.校验url协议
        ThrowUtils.throwIf(!fileUrl.startsWith("http://") && !fileUrl.startsWith("https://"), ErrorCode.PARAMS_ERROR, "仅支持http或https协议的文件地址");
        // 4.发送Head请求，判断文件是否存在
        HttpResponse httpResponse = null;
        try {
            httpResponse = HttpUtil.createRequest(Method.HEAD, fileUrl).execute();
            //未正常访问，无需进行其他判断 可能不支持head请求
            if (httpResponse.getStatus() != HttpStatus.SC_OK) {
                return;
            }
            // 5.校验文件类型
            String contentType = httpResponse.header("Content-Type");
            // 不为空才进行校验
            if (StrUtil.isNotBlank(contentType)) {
                final List<String> ALLOW_CONTENT_TYPE = Arrays.asList("image/jpeg","image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp");
                ThrowUtils.throwIf(!ALLOW_CONTENT_TYPE.contains(contentType), ErrorCode.PARAMS_ERROR, "文件格式错误");
            }
            // 6.校验文件大小
            String contentStr = httpResponse.header("content-length");
            if (StrUtil.isNotBlank(contentStr)) {
                try {
                    long contentLength = Long.parseLong(contentStr);
                    final long one_M = 1024 * 1024;
                    ThrowUtils.throwIf(contentLength > 2 * one_M, ErrorCode.PARAMS_ERROR, "文件大小不能超过2M");
                } catch (NumberFormatException e) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小解析失败");
                }
            }
        } finally {
            // 7.释放资源
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }

    @Override
    protected String getOriginFilename(Object inputSource) {
        String fileUrl = (String) inputSource;
        return FileUtil.mainName(fileUrl);
    }

    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        String fileUrl = (String) inputSource;
        //  下载文件到临时目录
        HttpUtil.downloadFile(fileUrl, file);
    }
}
