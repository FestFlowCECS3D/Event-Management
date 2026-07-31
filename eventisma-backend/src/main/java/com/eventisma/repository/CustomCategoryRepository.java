package com.eventisma.repository;

import com.eventisma.model.CustomCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomCategoryRepository extends JpaRepository<CustomCategory, String> {
}
