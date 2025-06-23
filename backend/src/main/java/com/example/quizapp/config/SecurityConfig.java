package com.example.quizapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Securityの設定クラス
 * JWT認証とCORS設定を行う
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * セキュリティフィルターチェーンの設定
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF保護を無効化（REST APIのため）
            .csrf(csrf -> csrf.disable())
            
            // CORS設定を有効化
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // セッションを使用しない（JWT認証のため）
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // エンドポイントのアクセス制御
            .authorizeHttpRequests(authz -> authz
                // プレイヤー向けAPIは認証不要
                .requestMatchers("/api/categories/**").permitAll()
                .requestMatchers("/api/quizzes/**").permitAll()
                .requestMatchers("/api/result/**").permitAll()
                
                // 管理者ログインAPIは認証不要
                .requestMatchers("/api/admin/login").permitAll()
                
                // その他の管理者APIは認証必要
                .requestMatchers("/api/admin/**").authenticated()
                
                // 静的リソースは認証不要
                .requestMatchers("/", "/index.html", "/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                
                // その他のリクエストは認証不要（プレイヤー向けアプリのため）
                .anyRequest().permitAll()
            )
            
            // JWTフィルターを追加
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * パスワードエンコーダーのBean定義
     * @return PasswordEncoder BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CORS設定
     * フロントエンドからのリクエストを許可するための設定
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 許可するオリジン（開発環境用）
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // 許可するHTTPメソッド
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 許可するヘッダー
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // 認証情報の送信を許可
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
