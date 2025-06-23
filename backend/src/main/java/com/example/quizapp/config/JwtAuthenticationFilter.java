package com.example.quizapp.config;

import com.example.quizapp.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT認証フィルター
 * リクエストヘッダーからJWTトークンを取得し、認証処理を行う
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /**
     * リクエストごとに実行されるフィルター処理
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param filterChain FilterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Authorizationヘッダーからトークンを取得
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Bearer トークンの形式をチェック
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // "Bearer " を除去
            
            // トークンが有効かチェックし、ユーザー名を取得
            if (jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
                username = jwtUtil.getUsernameFromToken(token);
            }
        }

        // ユーザー名が取得でき、まだ認証されていない場合
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 認証オブジェクトを作成
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
            
            // リクエストの詳細情報を設定
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            
            // SecurityContextに認証情報を設定
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 次のフィルターに処理を渡す
        filterChain.doFilter(request, response);
    }
}
