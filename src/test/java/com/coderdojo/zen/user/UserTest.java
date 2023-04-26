package com.coderdojo.zen.user;

import com.coderdojo.zen.user.model.User;
import com.coderdojo.zen.user.model.Category;
import com.coderdojo.zen.user.exceptions.UserNotFoundException;
import com.coderdojo.zen.user.repositories.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;
import java.util.stream.StreamSupport;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserTests {

    @Autowired
    private UserRepository userRepository;

    // JUnit test for saveUser
    @Test
    @Order(1)
    @Rollback(value = false)
    void saveUserTest(){

        User user = new User(1L, "Scratch", "MacBook Pro", Category.PROGRAMMING);

        userRepository.save(user);

        Assertions.assertThat(user.getId()).isPositive();
    }

    @Test
    @Order(2)
    void getUserTest(){

        User user = userRepository.findById(1L).orElseThrow(() -> new UserNotFoundException(1L));

        Assertions.assertThat(user.getId()).isEqualTo(1L);

    }

    @Test
    @Order(3)
    void getListOfUsersTest(){

        Iterable<User> users = userRepository.findAll();

        Assertions.assertThat(StreamSupport.stream(users.spliterator(), false).count()).isPositive();

    }

    @Test
    @Order(4)
    @Rollback(value = false)
    void updateUserTest(){

        User user = userRepository.findById(1L).orElseThrow(() -> new UserNotFoundException(1L));

        user.setDescription("New Description");
        user.setCategory(Category.PROGRAMMING);

        User userUpdated =  userRepository.save(user);

        Assertions.assertThat(userUpdated.getDescription()).isEqualTo("New Description");
        Assertions.assertThat(userUpdated.getCategory()).isEqualTo(Category.PROGRAMMING);
    }

    @Test
    @Order(5)
    @Rollback(value = false)
    void deleteUserTest(){

        User user = userRepository.findById(1L).orElseThrow(() -> new UserNotFoundException(1L));

        userRepository.delete(user);

        //userRepository.deleteById(1L);

        User user1 = null;

        Optional<User> optionalUser = userRepository.findById(1L);

        if(optionalUser.isPresent()){
            user1 = optionalUser.get();
        }

        Assertions.assertThat(user1).isNull();
    }
}


