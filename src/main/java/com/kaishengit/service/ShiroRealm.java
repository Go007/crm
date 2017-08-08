package com.kaishengit.service;

import com.kaishengit.mapper.RoleMapper;
import com.kaishengit.mapper.UserMapper;
import com.kaishengit.pojo.Role;
import com.kaishengit.pojo.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
public class ShiroRealm extends AuthorizingRealm {

    @Inject
    private UserMapper userMapper;
    @Inject
    private RoleMapper roleMapper;

    /**
     * 验证用户是否具有某项权限
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        User user = (User) principalCollection.getPrimaryPrincipal();
        if(user != null) {
            //获取用户对应的角色列表
            List<Role> roleList = roleMapper.findByUserId(user.getId());
            //将用户的角色名称赋值给info
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            for(Role role : roleList) {
                info.addRole(role.getRolename());
            }
            return info;
        }
        return null;
    }

    /**
     * 验证用户的账号和密码是否正确
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername(); //获取用户表单中的账号
        User user = userMapper.findByUsername(username); //根据账号查找对应的对象
        if(user != null) {
            if(!user.getEnable()) {
                throw new LockedAccountException("账号已被禁用");
            }
            return new SimpleAuthenticationInfo(user,user.getPassword(),getName());
        } else {
            throw new UnknownAccountException("账号或密码错误");
        }

    }
}
