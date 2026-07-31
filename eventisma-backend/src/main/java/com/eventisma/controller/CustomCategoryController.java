package com.eventisma.controller;

import com.eventisma.model.CustomCategory;
import com.eventisma.repository.CustomCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/custom-categories")
@CrossOrigin(origins = "*")
public class CustomCategoryController {

    @Autowired
    private CustomCategoryRepository customCategoryRepository;

    @GetMapping
    public ResponseEntity<List<CustomCategory>> getAllCustomCategories() {
        return ResponseEntity.ok(customCategoryRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<CustomCategory> createCustomCategory(@RequestBody CustomCategory customCategory) {
        customCategory.setId(null);
        CustomCategory saved = customCategoryRepository.save(customCategory);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomCategory> updateCustomCategory(@PathVariable String id, @RequestBody CustomCategory updated) {
        return customCategoryRepository.findById(id)
                .map(existing -> {
                    existing.setName(updated.getName());
                    CustomCategory saved = customCategoryRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomCategory(@PathVariable String id) {
        if (!customCategoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        customCategoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
