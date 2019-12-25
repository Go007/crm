package com.hong.controller;

import com.hong.dto.FlashMessage;
import com.hong.service.UserService;
import com.hong.util.ServletUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

 /*   @Value("${user.salt}")
    private String passwordSalt;*/
    @Inject
    private UserService userService;

    /**
     * 去登录页面
     * @return
     */
    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String index() {
        return "login";
    }

    @RequestMapping(value = "/",method = RequestMethod.POST)
    public String login(String username, String password,
                        RedirectAttributes redirectAttributes, HttpServletRequest request) {
        //获取认证主体，如果主体已存在，则将当前的主体退出
        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated()) {
            //当前用户已经登录,则先退出之前的账号（选做）
            subject.logout();
        }
        try {
            //登录，调用ShiroRealm类中的登录认证方法
          //  password = DigestUtils.md5Hex(password + passwordSalt);
            UsernamePasswordToken usernamePasswordToken =
                    new UsernamePasswordToken(username, password);
            //如果验证没有通过会抛异常,因此通过捕获有无异常判断登录是否通过
            subject.login(usernamePasswordToken);//会自动调用ShiroRelam的 doGetAuthenticationInfo()

            //获取登录的IP地址，并保存用户登录的日志
            userService.saveUserLogin(ServletUtil.getRemoteIp(request));
            return "redirect:/home";
        } catch (LockedAccountException ex) {
            redirectAttributes.addFlashAttribute("message",new FlashMessage(FlashMessage.STATE_ERROR,"账号已被禁用"));
        } catch (AuthenticationException exception) {
            redirectAttributes.addFlashAttribute("message",new FlashMessage(FlashMessage.STATE_ERROR,"账号或密码错误"));
        }
        //打回登录页面
        return "redirect:/";
    }

    /**
     * 安全退出
     * @return
     */
    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    public String logout(RedirectAttributes redirectAttributes) {
        SecurityUtils.getSubject().logout();

        redirectAttributes.addFlashAttribute("message",new FlashMessage("你已安全退出"));
        return "redirect:/";
    }

    @RequestMapping("/home")
    public String home() {
        return "home";
    }

    @RequestMapping("/403")
    public String error403() {
        return "error/403";
    }
}
