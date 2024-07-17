package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.common.utils.JwtUtil;
import com.atguigu.lease.model.entity.SystemUser;
import com.atguigu.lease.model.enums.BaseStatus;
import com.atguigu.lease.web.admin.mapper.SystemUserMapper;
import com.atguigu.lease.web.admin.service.LoginService;
import com.atguigu.lease.web.admin.vo.login.CaptchaVo;
import com.atguigu.lease.web.admin.vo.login.LoginVo;
import com.atguigu.lease.web.admin.vo.system.user.SystemUserInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wf.captcha.SpecCaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SystemUserMapper systemUserMapper;

    @Override
    public CaptchaVo getCaptcha() {
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        // 將驗證碼轉小寫，到時候前端輸入的也會轉小寫
        String code = specCaptcha.text().toLowerCase();
        String key = "admin:login:" + UUID.randomUUID();
        // 將驗證碼的key、value 放到 reids
        stringRedisTemplate.opsForValue().set(key, code, 60, TimeUnit.SECONDS);
        return new CaptchaVo(specCaptcha.toBase64(), key);
    }

    @Override
    public String login(LoginVo loginVo) {
        // 判斷用戶是否輸入驗證碼
        if (loginVo.getCaptchaCode() == null) {
            throw new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_NOT_FOUND);
        }

        // 判斷驗證碼是否過期
        String code = stringRedisTemplate.opsForValue().get(loginVo.getCaptchaKey());
        if (code == null) {
            throw new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_EXPIRED);
        }
        // 判斷用戶輸入驗證碼與 redis 中是否相同，記得用戶輸入的要先轉小寫
        if (!code.equals(loginVo.getCaptchaCode().toLowerCase())) {
            throw new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_ERROR);
        }

        // 判斷用戶輸入帳號
        LambdaQueryWrapper<SystemUser> systemUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        systemUserLambdaQueryWrapper.eq(SystemUser::getUsername, loginVo.getUsername());
        SystemUser systemUser = systemUserMapper.selectOne(systemUserLambdaQueryWrapper);
        if (systemUser == null) {
            throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }

        // 判斷此帳號是否有被禁用
        if (systemUser.getStatus() == BaseStatus.DISABLE) {
            throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_DISABLED_ERROR);
        }
        // 判斷用戶輸入密碼
        if (!systemUser.getPassword().equals(DigestUtils.md5DigestAsHex(loginVo.getPassword().getBytes()))) {
            throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_ERROR);
        }

        return JwtUtil.createToken(systemUser.getId(), systemUser.getUsername());
    }

    @Override
    public SystemUserInfoVo getLoginUserInfo(Long userId) {
        SystemUser systemUser = systemUserMapper.selectById(userId);
        SystemUserInfoVo systemUserInfoVo = new SystemUserInfoVo();
        systemUserInfoVo.setName(systemUser.getName());
        systemUserInfoVo.setAvatarUrl(systemUser.getAvatarUrl());
        return systemUserInfoVo;
    }
}
