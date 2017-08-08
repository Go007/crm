package com.kaishengit.service;

import com.google.common.collect.Maps;
import com.kaishengit.mapper.RoleMapper;
import com.kaishengit.mapper.UserLogMapper;
import com.kaishengit.mapper.UserMapper;
import com.kaishengit.pojo.Role;
import com.kaishengit.pojo.User;
import com.kaishengit.pojo.UserLog;
import com.kaishengit.util.ShiroUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;

@Named
@Transactional
public class UserService {
    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Inject
    private UserMapper userMapper;
    @Inject
    private UserLogMapper userLogMapper;
    @Inject
    private RoleMapper roleMapper;
    @Value("${user.salt}")
    private String passwordSalt;

    /**
     * 创建用户的登录日志
     * @param ip
     */
    public void saveUserLogin(String ip) {
        UserLog userLog = new UserLog();
        userLog.setLoginip(ip);
        userLog.setLogintime(DateTime.now().toString("yyyy-MM-dd HH:mm"));
        userLog.setUserid(ShiroUtil.getCurrentUserID());

        userLogMapper.save(userLog);
    }

    /**
     * 修改用户密码
     * @param password
     */
    public void changePassword(String password) {
        User user = ShiroUtil.getCurrentUser();
        user.setPassword(DigestUtils.md5Hex(password + passwordSalt));

        userMapper.updateUser(user);
    }

    /**
     * 获取当前登录用户的登录日志
     * @param start
     * @param length
     * @return
     */
    public List<UserLog> findCurrentUserLog(String start, String length) {
        Map<String,Object> param = Maps.newHashMap();
        param.put("userId",ShiroUtil.getCurrentUserID());
        param.put("start",start);
        param.put("length",length);
        return userLogMapper.findByParam(param);
    }

    /**
     * 获取当前登录用户的日志数量
     * @return
     */
    public Long findCurrentUserLogCount() {
        Map<String,Object> param = Maps.newHashMap();
        param.put("userId",ShiroUtil.getCurrentUserID());
        return userLogMapper.countByParam(param);
    }

    /**
     * 根据DataTables查询参数获取用户列表
     * @param params
     * @return
     */
    public List<User> findUserListByParam(Map<String, Object> params) {
        return userMapper.findByParam(params);
    }

    /**
     * 获取用户的总数量
     * @return
     */
    public Long findtUserCount() {
        return userMapper.count();
    }

    /**
     * 根据查询条件获取用户数量
     * @param params
     * @return
     */
    public Long findUserCountByParam(Map<String, Object> params) {
        return userMapper.countByParam(params);
    }

    /**
     * 获取所有的角色
     * @return
     */
    public List<Role> findAllRole() {
        return  roleMapper.findAll();
    }

    /**
     * 添加新用户
     * @param user
     * @param role
     */
    @Transactional
    public void saveUser(User user, String[] role) {
        user.setEnable(true);
        user.setPassword(DigestUtils.md5Hex(user.getPassword() + passwordSalt));

        //TODO 向微信公众平台注册账号

        userMapper.save(user);

        //保存用户的角色的关系
        //因为userID是设置在数据库端自增的,新增成功后需要查询id
        user = userMapper.findByUsername(user.getUsername());
        if (role != null) {
            userMapper.saveUserAndRole(user,role);
        }
        logger.info("{}添加了新用户{}", ShiroUtil.getCurrentUserName(),user.getUsername());
    }

    /**
     * 根据用户名查找用户
     * @param username
     * @return
     */
    public User findUserByUserName(String username) {
        return userMapper.findByUsername(username);
    }

    /**
     * 重置用户密码
     * @param id 用户ID
     */
    public void resetUserPassword(Integer id) {
        User user = userMapper.findById(id);
        if(user != null) {
            user.setPassword(DigestUtils.md5Hex("000000" + passwordSalt));
            userMapper.updateUser(user);
        }
    }

    /**
     * 根据用户ID查找用户
     * @param id
     * @return
     */
    public User findUserById(Integer id) {
        return userMapper.findById(id);
    }

    /**
     * 修改用户信息
     * @param user
     * @param role
     */
    public void editUser(User user, String[] role) {
        //删除原有的和角色之间的关系，
        userMapper.delUserAndRole(user.getId());
        //然后重建
        userMapper.saveUserAndRole(user,role);
        //修改用户
        userMapper.updateUser(user);
    }

    /**
     * 显示所有员工
     * @return
     */
    public List<User> finAllUser() {
        return userMapper.findAll();
    }

    /**
     * 根据用户ID查找具有的角色列表
     * @param userId
     * @return
     */
    public List<Role> findByUserId(Integer userId){return roleMapper.findByUserId(userId);}

    public User findUserWithRoleById(Integer id) {
        return userMapper.findUserWithRoleById(id);
    }
}
