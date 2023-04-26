package com.coderdojo.zen.user;

import com.coderdojo.zen.user.model.User;
import com.coderdojo.zen.user.model.Category;
import com.coderdojo.zen.user.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserITTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(){
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    void givenUserObject_whenCreateUser_thenReturnSavedUser() throws Exception{

        // given - precondition or setup
        User user = new User(
            null,
            "Scratch 2",
            "This is a Scratch user.",
            Category.PROGRAMMING
        );

        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        // then - verify the result or output using assert statements
        response.andDo(print()).
                andExpect(status().isCreated())
                .andExpect(jsonPath("$.name",
                        is(user.getName())))
                .andExpect(jsonPath("$.description",
                        is(user.getDescription())))
                .andExpect(jsonPath("$.category",
                        is(user.getCategory().toString())));

    }

    // JUnit test for Get All users REST API
    @Test
    @Order(2)
    void givenListOfUsers_whenGetAllUsers_thenReturnUsersList() throws Exception{
        // given - precondition or setup
        List<User> listOfUsers = new ArrayList<>();
        listOfUsers.add(new User(1L,"MacBook Pro", "", Category.PROGRAMMING));
        listOfUsers.add(new User(2L,"iPhone", "", Category.PROGRAMMING));
        userRepository.saveAll(listOfUsers);
        // when -  action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/api/v1/users"));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()",
                        is(listOfUsers.size())));

    }

    @Test
    @Order(3)
    void givenListOfUsers_whenGetUsersByCategory_thenReturnUsersForCategory() throws Exception{
        // given - precondition or setup
        List<User> listOfUsers = new ArrayList<>();
        listOfUsers.add(new User(null,"One", "one", Category.SOFT_SKILLS));
        listOfUsers.add(new User(null,"Two", "two", Category.SOFT_SKILLS));
        userRepository.saveAll(listOfUsers);
        // when -  action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/api/v1/users")
                .param("category", "soft_skills"));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()",
                        is(listOfUsers.size())));

    }

    @Test
    @Order(4)
    void givenListOfUsers_whenGetUsersByInvalidCategory_thenReturnAllUsers() throws Exception{
        // given - precondition or setup
        List<User> listOfUsers = new ArrayList<>();
        listOfUsers.add(new User(null,"One", "one", Category.PROGRAMMING));
        listOfUsers.add(new User(null,"Two", "two", Category.PROGRAMMING));
        userRepository.saveAll(listOfUsers);
        // when -  action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/api/v1/users")
                .param("category", "fake"));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()",
                        is(listOfUsers.size())));

    }

    // positive scenario - valid user id
    // JUnit test for GET user by id REST API
    @Test
    @Order(5)
    void givenUserId_whenGetUserById_thenReturnUserObject() throws Exception{
        // given - precondition or setup
        User user = userRepository.save(
                new User(null,"JavaScript", "This is a JavaScript user", Category.PROGRAMMING)
        );
        // when -  action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/api/v1/users/{id}", user.getId()));
        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.description",
                        is(user.getDescription())))
                .andExpect(jsonPath("$.category",
                        is(user.getCategory().toString())));

    }

    // negative scenario - valid user id
    // JUnit test for GET user by id REST API
    @Test
    @Order(6)
    void givenInvalidUserId_whenGetUserById_thenReturnEmpty() throws Exception{

        // when -  action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/api/v1/users/{id}", 11L));

        // then - verify the output
        response.andExpect(status().isNotFound())
                .andDo(print());

    }

    // JUnit test for update user REST API - positive scenario
    @Test
    @Order(7)
    void givenUpdatedUser_whenUpdateUser_thenReturnUpdateUserObject() throws Exception{
        // given - precondition or setup
        User user = userRepository.save(
                new User(null,
                        "Python",
                        "This is a Javascript user",
                        Category.PROGRAMMING
                )
        );
        User updatedUser = new User(user.getId(),"Python", "This is a Python user", Category.PROGRAMMING);
        // when -  action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(put("/api/v1/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Python\",\"description\":\"This is a Python user\",\"category\":\"PROGRAMMING\"}"));


        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.description",
                        is(updatedUser.getDescription())))
                .andExpect(jsonPath("$.category",
                        is(updatedUser.getCategory().toString())));
    }

    // JUnit test for update user REST API - negative scenario
    @Test
    @Order(8)
    void givenUpdatedUser_whenUpdateUser_thenReturn404() throws Exception{
        // given - precondition or setup

        User user = new User(
                15L,
                "Swift",
                "This is a swift user",
                Category.PROGRAMMING
        );

        // when -  action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(put("/api/v1/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        // then - verify the output
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    // JUnit test for delete user REST API
    @Test
    @Order(9)
    void givenUserId_whenDeleteUser_thenReturn200() throws Exception{
        // given - precondition or setup
        User user = userRepository.save(
                new User(
                        null,
                        "Python",
                        "This is a Javascript user",
                        Category.PROGRAMMING
                )
        );

        // when -  action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(delete("/api/v1/users/{id}", user.getId()));

        // then - verify the output
        response.andExpect(status().isNoContent())
                .andDo(print());
    }
}