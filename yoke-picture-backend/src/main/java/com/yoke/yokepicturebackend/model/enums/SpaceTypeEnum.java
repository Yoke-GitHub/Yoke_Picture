package com.yoke.yokepicturebackend.model.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * @author H
 * @project_name yoke-picture-backend
 * @filename SpaceTypeEnum
 * @created_date 2025/9/23 1:21
 * @description 空间类型枚举类
 */
@Getter
public enum SpaceTypeEnum {

    PRIVATE("私有空间",0),
    TEAM("团队空间",1);

    private final String text;

    private final int value;

    SpaceTypeEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     *根据value 获取枚举
     * @param value
     * @return
     */
    public static SpaceTypeEnum getEnumByValue(Integer value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (SpaceTypeEnum spaceTypeEnum : SpaceTypeEnum.values()) {
            if (spaceTypeEnum.value == value) {
                return spaceTypeEnum;
            }
        }
        return null;
    }

}
