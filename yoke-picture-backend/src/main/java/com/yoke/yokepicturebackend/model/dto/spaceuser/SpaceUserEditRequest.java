package com.yoke.yokepicturebackend.model.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

/**
 * @author H
 * @project_name yoke-picture-backend
 * @filename SpaceUserEditRequest
 * @created_date 2025/9/23 2:02
 * @description 编辑空间成员请求
 */
@Data
public class SpaceUserEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 空间角色：viewer/editor/admin
     */
    private String spaceRole;

    private static final long serialVersionUID = 1L;
}
