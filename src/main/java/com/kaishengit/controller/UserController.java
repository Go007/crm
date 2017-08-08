package com.kaishengit.controller;

import com.kaishengit.dto.DataTablesResult;
import com.kaishengit.exception.NotFoundException;
import com.kaishengit.pojo.User;
import com.kaishengit.pojo.UserLog;
import com.kaishengit.service.UserService;
import com.kaishengit.util.ShiroUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Inject
    private UserService userService;
    @Value("${user.salt}")
    private String passwordSalt;
    /**
     * 修改密码
     */
    @RequestMapping(value = "/password",method = RequestMethod.GET)
    public String editPassword() {
        return "setting/password";
    }

    @RequestMapping(value = "/password",method = RequestMethod.POST)
    @ResponseBody
    public String editPassword(String password) {
        userService.changePassword(password);
        return "success";
    }

    /**
     * 验证原始密码是否正确(Ajax调用)
     * @return
     */
    @RequestMapping(value = "/validate/password",method = RequestMethod.GET)
    @ResponseBody
    public String validateOldPassword(@RequestHeader("X-Requested-With") String xRequestedWith, String oldpassword) {
        /**
         * 在页面中直接暴露访问隐秘数据的接口是很危险的，为了防止恶意访问，
         * 加上了X-Requested-With的头从而确定发起的是Ajax请求，因为jQuery的验证插件在remote进行Ajax调用时，
         * 会自动在请求头中加上X-Requested-With:XMLHttpRequest的请求头
         */
        if("XMLHttpRequest".equals(xRequestedWith)) {
            User user = ShiroUtil.getCurrentUser();
            if(user.getPassword().equals(DigestUtils.md5Hex(oldpassword + passwordSalt))) {
                return "true";
            }
            return "false";
        } else {
            throw new NotFoundException();
        }
    }


    /**
     * 显示当前登录用户的登录日志
     * @return
     */
    @RequestMapping(value = "/log",method = RequestMethod.GET)
    public String showUserLog() {
        return "setting/loglist";
    }

    /**
     * 使用DataTables显示数据
     * @return
     */
    @RequestMapping(value = "/log/load",method = RequestMethod.GET)
    @ResponseBody
    public DataTablesResult userLogLoad(HttpServletRequest request) {
        String draw = request.getParameter("draw");
        String start = request.getParameter("start");
        String length = request.getParameter("length");

        List<UserLog> userLogList = userService.findCurrentUserLog(start,length);
        Long count = userService.findCurrentUserLogCount();

        return new DataTablesResult<>(draw,userLogList,count,count);
    }

}
