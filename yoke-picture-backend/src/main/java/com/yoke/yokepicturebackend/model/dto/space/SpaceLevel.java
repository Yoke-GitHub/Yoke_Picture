package com.yoke.yokepicturebackend.model.dto.space;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 空间级别
 */
@Data
@AllArgsConstructor
public class SpaceLevel {

    /**
     * 值
     */
    private int value;

    /**
     * 中文
     */
    private String text;

    /**
     * 最大上传数量
     */
    private long maxCount;

    /**
     * 最大上传容量
     */
    private long maxSize;
}

