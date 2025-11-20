package com.yoke.yokepicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yoke.yokepicturebackend.model.dto.space.SpaceAddRequest;
import com.yoke.yokepicturebackend.model.dto.space.SpaceQueryRequest;
import com.yoke.yokepicturebackend.model.entity.Space;
import com.yoke.yokepicturebackend.model.entity.User;
import com.yoke.yokepicturebackend.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author H
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-01-18 06:15:59
*/
public interface SpaceService extends IService<Space> {
    /**
     * 空间校验
     * @param space the space
     * @param add 是否是添加操作校验
     */
    void validSpace(Space space, boolean add);

    /**
     * 添加空间
     * @param spaceAddRequest 空间添加请求
     * @param loginUser 登录用户
     * @return
     */
    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);


    /**
     * 获取空间包装类（单条）
     * @param space 空间
     * @param request 请求
     * @return
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 获取空间包装类（分页）
     * @param spacePage 空间分页
     * @param request 请求
     * @return
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 获取查询对象
     * @param spaceQueryRequest   空间查询请求
     * @return
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    /**
     * 根据空间级别填充数据
     * @param space 空间
     */
    void fillSpaceBySpaceLevel(Space space);

    /**
     * 检查空间权限
     * @param loginUser
     * @param space
     */
    void checkSpaceAuth(User loginUser, Space space);

}
