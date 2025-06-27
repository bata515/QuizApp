package com.example.quizapp.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //TODO CSRFを後々実装予定
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login","/users/create","/register","/error").permitAll()  // ログインページは認証なしでアクセス可能に
                        .anyRequest().authenticated()  // 他のページは認証が必要
                )
                .formLogin(form -> form
                        .loginPage("/login")  // カスタムログインページを指定
                        .permitAll()  // ログインページへのアクセスは許可
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")  // ログアウトURL
                        .permitAll()
                );
        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean // このメソッドの返り値をSpringコンテナにBeanとして登録する
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCryptPasswordEncoderをインスタンス化して返す
    }

}
