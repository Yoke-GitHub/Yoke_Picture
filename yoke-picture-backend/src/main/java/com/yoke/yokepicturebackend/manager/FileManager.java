package com.yoke.yokepicturebackend.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.yoke.yokepicturebackend.config.CosClientConfig;
import com.yoke.yokepicturebackend.exception.BusinessException;
import com.yoke.yokepicturebackend.exception.ErrorCode;
import com.yoke.yokepicturebackend.exception.ThrowUtils;
import com.yoke.yokepicturebackend.model.dto.file.UploadPictureResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 文件管理器
 * @Deprecated 已废弃，改为使用upload包中的模板方法
 */
@Slf4j
@Service
@Deprecated
public class FileManager {
    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    @Resource
    private CosManager cosManager;

    /**
     * 上传图片
     *
     * @param uploadPathPrefix 上传路径前缀
     * @return
     * @Param multipartFile 文件
     */
    public UploadPictureResult uploadPicture(MultipartFile multipartFile, String uploadPathPrefix) {
        //校验图片
        validPicture(multipartFile);
        //图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originalFilename = multipartFile.getOriginalFilename();
        //自己拼接文件上传路径，而不是使用原始文件名称，可以增强安全性
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("%s/%s", uploadPathPrefix, uploadFilename);
        //解析结果并返回
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(uploadPath, null);
            multipartFile.transferTo(file);
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            // 获取图片信息对象
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // 图片属性 宽 高 比例
            int pictureWidth = imageInfo.getWidth();
            int pictureHeight = imageInfo.getHeight();
            double pictureScale = NumberUtil.round(pictureWidth * 1.0 / pictureHeight, 2).doubleValue();
            // 封装返回结果
            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
            uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setPicWidth(pictureWidth);
            uploadPictureResult.setPicHeight(pictureHeight);
            uploadPictureResult.setPicScale(pictureScale);
            uploadPictureResult.setPicFormat(imageInfo.getFormat());
            System.out.println(uploadPictureResult.getPicFormat() + "格式");

            // 返回可访问地址
            return uploadPictureResult;
        } catch (Exception e) {
            log.error("图片上传到对象存储失败 ", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            //清理临时文件
            deleteTempFile(file);
        }
    }

    /**
     * 校验文件
     *
     * @Param multipartFile
     */
    public void validPicture(MultipartFile multipartFile) {
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件不能为空");
        //1.校验文件大小
        long fileSize = multipartFile.getSize();
        final long one_M = 1024 * 1024;
        ThrowUtils.throwIf(fileSize > one_M * 2, ErrorCode.PARAMS_ERROR, "文件大小不能超过2M");
        //2.校验文件类型
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        //允许上传的格式列表(或集合)
        final List<String> ALLOW_FORMAT_LIST = Arrays.asList("png", "jpg", "jpeg", "bmp", "gif", "webp");
        ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件格式不支持");
    }

    /**
     * 删除临时文件
     *
     * @param file
     */
    public void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        // 删除临时文件
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }

    //todo 新增方法

    /**
     * 通话url上传图片
     *
     * @param uploadPathPrefix 上传路径前缀
     * @return
     * @Param fileUrl 文件URL
     */
    public UploadPictureResult uploadPictureBUrl(String fileUrl, String uploadPathPrefix) {
        //校验图片
        //todo
        validPicture(fileUrl);
        //图片上传地址
        String uuid = RandomUtil.randomString(16);
        // todo
        String originalFilename = FileUtil.mainName(fileUrl);
//        String originalFilename = multipartFile.getOriginalFilename();
        //自己拼接文件上传路径，而不是使用原始文件名称，可以增强安全性
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("%s/%s", uploadPathPrefix, uploadFilename);
        //解析结果并返回
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(uploadPath, null);
//            multipartFile.transferTo(file);
            //todo 下载文件
            HttpUtil.downloadFile(fileUrl, file);
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            // 获取图片信息对象
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // 图片属性 宽 高 比例
            int pictureWidth = imageInfo.getWidth();
            int pictureHeight = imageInfo.getHeight();
            double pictureScale = NumberUtil.round(pictureWidth * 1.0 / pictureHeight, 2).doubleValue();
            // 封装返回结果
            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
            uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setPicWidth(pictureWidth);
            uploadPictureResult.setPicHeight(pictureHeight);
            uploadPictureResult.setPicScale(pictureScale);
            uploadPictureResult.setPicFormat(imageInfo.getFormat());
            System.out.println(uploadPictureResult.getPicFormat() + "格式");

            // 返回可访问地址
            return uploadPictureResult;
        } catch (Exception e) {
            log.error("图片上传到对象存储失败 ", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            //清理临时文件
            deleteTempFile(file);
        }
    }

    /**
     * url图片校验
     *
     * @param fileUrl
     */
    private void validPicture(String fileUrl) {
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
                final List<String> ALLOW_CONTENT_TYPE = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp");
                ThrowUtils.throwIf(!ALLOW_CONTENT_TYPE.contains(contentType), ErrorCode.PARAMS_ERROR, "文件格式错误");
            }
            // 6.校验文件大小
            String contentStr = httpResponse.header("content-length");
            if (StrUtil.isNotBlank(contentStr)) {
                try {
                    long contentLength = NumberUtil.parseLong(contentStr);
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
}
