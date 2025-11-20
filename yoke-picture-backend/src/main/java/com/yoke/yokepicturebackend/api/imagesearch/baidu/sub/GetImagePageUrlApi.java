package com.yoke.yokepicturebackend.api.imagesearch.baidu.sub;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.yoke.yokepicturebackend.exception.BusinessException;
import com.yoke.yokepicturebackend.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取以图搜图页面的 url(step1)
 */
@Slf4j
public class GetImagePageUrlApi {
    /**
     * 获取以图搜图页面的地址
     *
     * @param imageUrl
     * @return
     */
    public static String getImagePageUrl(String imageUrl) {
//        image: https%3A%2F%2Fwww.codefather.cn%2Flogo.png
//        tn: pc
//        from: pc
//        image_source: PC_UPLOAD_URL
//        sdkParams:
        // 1. 准备请求参数
        Map<String, Object> formData = new HashMap<>();
        formData.put("image", imageUrl);
        formData.put("tn", "pc");
        formData.put("from", "pc");
        formData.put("image_source", "PC_UPLOAD_URL");
        // 获取当前时间戳
        long uptime = System.currentTimeMillis();
        // 请求地址
        String url = "https://graph.baidu.com/upload?uptime=" + uptime;
        String ascToken="1740112329987_1740129598443_3B9ZmDCth9V/6vmk4C2eOlJGTElk4o5s0jU5Bc4OUB90eNRBq3U+GE7lYPcCsDuVprSe11JcU05M9OFsVDF2D1xjp1Aju/97OzCSYyD194TA4+iIMPs8nM7ooQmWS069CNRo3BqPLqf0GNEnTjSwzas4RP9iquoyRS+VavjUsFCscBD+EoQHAEmLWl8Lul0Ke9RPSZ8fDAZ5EG/uDnbmCDx5wmO3N5xzIBlHeCzWNQ55EFW0yqwC/LXoGqKz64XA+1DsAZebIeSy81xC6JCbuh+RCse5kNJr4+wm0FZxzvyRiuIXDFow49gv3Dlw5/3WmBHo4VH8qvkxCHCbH/MfsW1rZ7I9leW5Bo0Qng4JBOAJKBsURNXlh3eHCPIc01IlM06qVniRiB2IXTKg7/K6ZjOa/1f7r9vV601RKbMtsBJnqXe4Ce+iUlOuIeFNRDNs";
        log.info(url);
        try {
            // 2.发送请求
            HttpResponse httpResponse = HttpRequest.post(url)
                    .form(formData)
                    .header("Acs-Token", ascToken)
                    .timeout(5000)
                    .execute();
            if (httpResponse.getStatus() != HttpStatus.HTTP_OK) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
            }
            // 解析响应
            String body = httpResponse.body();
            Map<String, Object> result = JSONUtil.toBean(body, Map.class);
            // 3.处理响应结果
            if (result == null || !Integer.valueOf(0).equals(result.get("status"))) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
            }
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            // 对URL 进行解码
            String rawUrl = (String) data.get("url");
            String searchResultUrl = URLUtil.decode(rawUrl, StandardCharsets.UTF_8);
            if (StrUtil.isBlank(searchResultUrl)) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "未返回有效的结果地址");
            }
            return searchResultUrl;
        } catch (Exception e) {
            log.error("调用百度以图搜图接口失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜索失败");
        }
    }

    public static void main(String[] args) {
        // todo 测试
        String imageUrl = "https://www.codefather.cn/logo.png";
        String searchResult = getImagePageUrl(imageUrl);
        System.out.println("以图搜图成功，结果Url:" + searchResult);
    }

}