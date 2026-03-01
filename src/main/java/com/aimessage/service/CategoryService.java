package com.aimessage.service;

import com.aimessage.entity.Category;
import com.aimessage.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @PostConstruct
    @Transactional
    public void initCategories() {
        createCategoryIfNotExists("funding", "AI融资与商业动态", "💰", 1, true);
        createCategoryIfNotExists("policy", "AI安全与政策监管", "🔒", 2, true);
        createCategoryIfNotExists("model", "模型发布与产品更新", "🚀", 3, true);
        createCategoryIfNotExists("agent", "AI Agent与智能体", "🤖", 4, true);
        createCategoryIfNotExists("china", "中国AI动态", "🇨🇳", 5, true);
        createCategoryIfNotExists("chip", "AI芯片与算力", "⚡", 6, false);
        createCategoryIfNotExists("robot", "具身智能与机器人", "🔧", 7, false);
        createCategoryIfNotExists("research", "AI学术研究", "📚", 8, false);
    }

    private void createCategoryIfNotExists(String name, String displayName, String icon, int sortOrder, boolean isImportant) {
        if (!categoryRepository.existsByName(name)) {
            Category category = new Category();
            category.setName(name);
            category.setDisplayName(displayName);
            category.setIcon(icon);
            category.setSortOrder(sortOrder);
            category.setIsImportant(isImportant);
            categoryRepository.save(category);
        }
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Category not found: " + name));
    }
}
