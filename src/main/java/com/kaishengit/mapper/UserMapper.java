package com.kaishengit.mapper;

import com.kaishengit.pojo.User;

import java.util.List;
import java.util.Map;

public interface UserMapper {

    void save(User user);

    /**
     * 保存用户的角色的对应关系
     * @param user 用户对象
     * @param roles 角色ID数组
     */
    void saveUserAndRole(User user, String[] roles);

    /**
     * 根据用户主键删除和角色的对应关系
     * @param id 用户的主键
     */
    void delUserAndRole(Integer id);

    User findByUsername(String username);

    void updateUser(User user);

    List<User> findByParam(Map<String, Object> params);

    Long count();

    Long countByParam(Map<String, Object> params);

    User findById(Integer id);

    List<User> findAll();

    User findUserWithRoleById(Integer id);
}
