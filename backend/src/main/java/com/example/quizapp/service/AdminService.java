package com.example.quizapp.service;

import com.example.quizapp.dto.AdminLoginRequestDto;
import com.example.quizapp.dto.AdminLoginResponseDto;
import com.example.quizapp.entity.AdminUser;
import com.example.quizapp.repository.AdminUserRepository;
import com.example.quizapp.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 管理者認証関連のビジネスロジックを処理するサービスクラス
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final AdminUserRepository adminUserRepository;
    private final JwtUtil jwtUtil;
    
    // 循環依存を避けるため、PasswordEncoderを直接インスタンス化
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 管理者ログイン処理
     * @param loginRequest ログイン情報
     * @return Optional<AdminLoginResponseDto> ログイン成功時はJWTトークンを含むレスポンス
     */
    public Optional<AdminLoginResponseDto> login(AdminLoginRequestDto loginRequest) {
        // ユーザー名でユーザーを検索
        Optional<AdminUser> adminUserOpt = adminUserRepository.findByUsername(loginRequest.getUsername());
        
        if (adminUserOpt.isEmpty()) {
            return Optional.empty();
        }
        
        AdminUser adminUser = adminUserOpt.get();
        
        // パスワードを検証
        if (!passwordEncoder.matches(loginRequest.getPassword(), adminUser.getPasswordHash())) {
            return Optional.empty();
        }
        
        // JWTトークンを生成
        String token = jwtUtil.generateToken(adminUser.getUsername());
        
        return Optional.of(new AdminLoginResponseDto(token, adminUser.getUsername()));
    }

    /**
     * 管理者ユーザーを作成（初期データ投入用）
     * @param username ユーザー名
     * @param password パスワード
     * @return AdminUser 作成された管理者ユーザー
     */
    public AdminUser createAdminUser(String username, String password) {
        // 既に同じユーザー名が存在するかチェック
        if (adminUserRepository.existsByUsername(username)) {
            throw new RuntimeException("ユーザー名が既に存在します: " + username);
        }
        
        // パスワードをハッシュ化
        String hashedPassword = passwordEncoder.encode(password);
        
        AdminUser adminUser = new AdminUser(username, hashedPassword);
        return adminUserRepository.save(adminUser);
    }

    /**
     * JWTトークンからユーザー名を取得
     * @param token JWTトークン
     * @return Optional<String> ユーザー名
     */
    public Optional<String> getUsernameFromToken(String token) {
        try {
            if (jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
                return Optional.of(jwtUtil.getUsernameFromToken(token));
            }
        } catch (Exception e) {
            // トークンが無効な場合
        }
        return Optional.empty();
    }

    /**
     * 管理者ユーザーが存在するかチェック
     * @param username ユーザー名
     * @return boolean 存在する場合true
     */
    @Transactional(readOnly = true)
    public boolean adminUserExists(String username) {
        return adminUserRepository.existsByUsername(username);
    }
}
