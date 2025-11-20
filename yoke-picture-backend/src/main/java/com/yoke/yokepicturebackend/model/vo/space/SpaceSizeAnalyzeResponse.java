package com.yoke.yokepicturebackend.model.vo.space;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author H
 * @project_name yoke-picture-backend
 * @filename SpaceSizeAnalyzeResponse
 * @created_date 2025/9/17 4:16
 * @description  空间图片大小分析响应
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceSizeAnalyzeResponse implements Serializable {

    /**
     * 图片大小范围
     */
    private String sizeRange;

    /**
     * 图片数量
     */
    private Long count;

    private static final long serialVersionUID = 1L;
}
