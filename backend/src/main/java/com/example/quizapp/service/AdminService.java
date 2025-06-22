package com.example.quizapp.service;

import com.example.quizapp.entity.Admin;
import com.example.quizapp.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Optional<Admin> findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }
    
    public Admin createAdmin(String username, String password) {
        if (adminRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("ユーザー名が既に存在します: " + username);
        }
        
        String encodedPassword = passwordEncoder.encode(password);
        Admin admin = new Admin(username, encodedPassword);
        return adminRepository.save(admin);
    }
    
    public boolean authenticate(String username, String password) {
        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            return passwordEncoder.matches(password, admin.getPassword());
        }
        return false;
    }
    
    public boolean existsByUsername(String username) {
        return adminRepository.existsByUsername(username);
    }
}
