package com.coderdojo.zen.user.repositories;

import com.coderdojo.zen.user.model.User;
import com.coderdojo.zen.user.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByCategory(Category category);
}