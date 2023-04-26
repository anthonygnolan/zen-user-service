package com.coderdojo.zen.user.dto;

import com.coderdojo.zen.user.model.Category;

public class UserDTO {

    private final String name;
    private final String description;
    private final Category category;

    public UserDTO(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

}
