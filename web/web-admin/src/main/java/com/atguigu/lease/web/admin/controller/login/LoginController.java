package com.atguigu.lease.web.admin.controller.login;


import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.common.utils.JwtUtil;
import com.atguigu.lease.web.admin.service.LoginService;
import com.atguigu.lease.web.admin.vo.login.CaptchaVo;
import com.atguigu.lease.web.admin.vo.login.LoginVo;
import com.atguigu.lease.web.admin.vo.system.user.SystemUserInfoVo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台管理系统登录管理")
@RestController
@RequestMapping("/admin")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Operation(summary = "獲取圖形驗證碼")
    @GetMapping("login/captcha")
    public Result<CaptchaVo> getCaptcha() {
        CaptchaVo captchaVo = loginService.getCaptcha();
        return Result.ok(captchaVo);
    }

    @Operation(summary = "登入")
    @PostMapping("login")
    public Result<String> login(@RequestBody LoginVo loginVo) {
        String jwt = loginService.login(loginVo);
        return Result.ok(jwt);
    }

    @Operation(summary = "獲取登入用戶個人信息")
    @GetMapping("info")
    public Result<SystemUserInfoVo> info() {
        Long userId = LoginUserHolder.getLoginUser().getUserId();
        SystemUserInfoVo systemUserInfoVo = loginService.getLoginUserInfo(userId);
        return Result.ok(systemUserInfoVo);
    }
}