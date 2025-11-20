package com.yoke.yokepicturebackend.controller;

import com.yoke.yokepicturebackend.common.BaseResponse;
import com.yoke.yokepicturebackend.common.ResultUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MainController {

    /**
     * 健康检查
     */
    @RequestMapping("/health")
    public BaseResponse<String> health() {
        return ResultUtils.success("ok");
    }
}
