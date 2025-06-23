package com.example.quizapp.service;

import com.example.quizapp.dto.CategoryDto;
import com.example.quizapp.entity.Category;
import com.example.quizapp.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * カテゴリー関連のビジネスロジックを処理するサービスクラス
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 全てのカテゴリーを取得
     * @return List<CategoryDto> カテゴリーのリスト
     */
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * IDでカテゴリーを取得
     * @param id カテゴリーID
     * @return Optional<CategoryDto> カテゴリー情報
     */
    @Transactional(readOnly = true)
    public Optional<CategoryDto> getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(this::convertToDto);
    }

    /**
     * 新しいカテゴリーを作成
     * @param categoryDto カテゴリー情報
     * @return CategoryDto 作成されたカテゴリー
     */
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = new Category(categoryDto.getName());
        Category savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }

    /**
     * カテゴリーを更新
     * @param id カテゴリーID
     * @param categoryDto 更新するカテゴリー情報
     * @return Optional<CategoryDto> 更新されたカテゴリー
     */
    public Optional<CategoryDto> updateCategory(Long id, CategoryDto categoryDto) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setName(categoryDto.getName());
                    Category savedCategory = categoryRepository.save(category);
                    return convertToDto(savedCategory);
                });
    }

    /**
     * カテゴリーを削除
     * @param id カテゴリーID
     * @return boolean 削除成功の場合true
     */
    public boolean deleteCategory(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * CategoryエンティティをCategoryDtoに変換
     * @param category Categoryエンティティ
     * @return CategoryDto 変換されたDTO
     */
    private CategoryDto convertToDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
