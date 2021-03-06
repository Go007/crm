package com.hong.mapper;

import com.hong.pojo.Role;

import java.util.List;

public interface RoleMapper {

    Role findById(Integer id);

    List<Role> findAll();

    /**
     * 根据用户ID查找具有的角色列表
     * @param userId
     * @return
     */
    List<Role> findByUserId(Integer userId);

}
