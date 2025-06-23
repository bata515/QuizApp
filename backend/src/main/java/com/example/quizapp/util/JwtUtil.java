package com.example.quizapp.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT（JSON Web Token）の生成・検証を行うユーティリティクラス
 * 管理者認証で使用するJWTトークンの操作を担当
 */
@Component
public class JwtUtil {

    // application.propertiesからJWTの秘密鍵を取得
    @Value("${jwt.secret}")
    private String secret;

    // application.propertiesからJWTの有効期限（ミリ秒）を取得
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * JWT署名用の秘密鍵を生成
     * @return SecretKey 署名用の秘密鍵
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * ユーザー名からJWTトークンを生成
     * @param username ユーザー名
     * @return String 生成されたJWTトークン
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(username)           // トークンの主体（ユーザー名）を設定
                .setIssuedAt(now)              // トークン発行日時を設定
                .setExpiration(expiryDate)     // トークン有効期限を設定
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // HS256アルゴリズムで署名
                .compact();                    // トークン文字列を生成
    }

    /**
     * JWTトークンからユーザー名を取得
     * @param token JWTトークン
     * @return String ユーザー名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * JWTトークンの有効性を検証
     * @param token JWTトークン
     * @return boolean トークンが有効な場合true、無効な場合false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // トークンが無効、署名が不正、形式が不正な場合
            return false;
        }
    }

    /**
     * JWTトークンの有効期限切れをチェック
     * @param token JWTトークン
     * @return boolean 期限切れの場合true、有効な場合false
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            // トークンが解析できない場合は期限切れとして扱う
            return true;
        }
    }
}
