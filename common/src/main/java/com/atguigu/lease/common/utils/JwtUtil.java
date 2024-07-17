package com.atguigu.lease.common.utils;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * ClassName: JwtUtil
 * Package: com.atguigu.lease.common.utils
 */
public class JwtUtil {
    private static SecretKey secretKey = Keys.hmacShaKeyFor("iswakrypuxqjtgjmbaghvfwrmaitucyx".getBytes());
    public static String createToken(Long userId, String username) {
        // 這裡設定的是 Payload，Header 通常不需要設定，但也可以自己設定
        String jwt = Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .setSubject("LOGIN_USER")
                .claim("userId", userId)
                .claim("username", username)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
        return jwt;
    }

    // 解析這個 Token 是否合法，發生異常就代表非法
    public static Claims parseToken(String token) {
        try {
            JwtParser jwtParser = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build();
            // 如果 parseClaimsJws 拋出 Exception 代表非法
            Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
            Claims body = claimsJws.getBody();
            return body;
        } catch (ExpiredJwtException e) {
            throw new LeaseException(ResultCodeEnum.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new LeaseException(ResultCodeEnum.TOKEN_INVALID);
        }
    }

    public static void main(String[] args) {
        System.out.println(JwtUtil.createToken(2L, "user"));
    }
}
