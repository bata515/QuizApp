package com.example.quizapp.service;

import com.example.quizapp.entity.Category;
import com.example.quizapp.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }
    
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }
    
    public Category createCategory(String name, String description) {
        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("カテゴリー名が既に存在します: " + name);
        }
        Category category = new Category(name, description);
        return categoryRepository.save(category);
    }
    
    public Category updateCategory(Long id, String name, String description) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("カテゴリーが見つかりません: " + id));
        
        // 名前が変更される場合、重複チェック
        if (!category.getName().equals(name) && categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("カテゴリー名が既に存在します: " + name);
        }
        
        category.setName(name);
        category.setDescription(description);
        return categoryRepository.save(category);
    }
    
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("カテゴリーが見つかりません: " + id);
        }
        categoryRepository.deleteById(id);
    }
    
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
}
