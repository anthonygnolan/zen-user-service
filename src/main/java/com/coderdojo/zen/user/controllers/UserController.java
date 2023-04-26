package com.coderdojo.zen.user.controllers;

import com.coderdojo.zen.user.dto.UserDTO;
import com.coderdojo.zen.user.model.User;
import com.coderdojo.zen.user.exceptions.UserModelAssembler;
import com.coderdojo.zen.user.exceptions.UserNotFoundException;
import com.coderdojo.zen.user.model.Category;
import com.coderdojo.zen.user.repositories.UserRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
/**
 * Returns an Image object that can then be painted on the screen.
 * The url argument must specify an absolute. The name
 * argument is a specifier that is relative to the url argument.
 * <p>
 * This method always returns immediately, whether the
 * image exists. When this applet attempts to draw the image on
 * the screen, the data will be loaded. The graphics primitives
 * that draw the image will incrementally paint on the screen.
 *
 */
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserRepository userRepository;

    /**
     * The public name of a hero that is common knowledge
     */
    private final UserModelAssembler userAssembler;

    UserController(UserRepository userRepository, UserModelAssembler assembler) {

        this.userRepository = userRepository;
        this.userAssembler = assembler;
    }

    /**
     * Hero is the main entity we'll be using to . . .
     * Please see the class for true identity
     * @author Captain America
     * @param category the amount of incoming damage
     * @return EntityModel
     */
    @GetMapping("/users")
    public CollectionModel<EntityModel<User>> getUsers(@RequestParam(required = false, name = "category")Category category) {

        List<EntityModel<User>> users;
        if (category != null) {
            users = userRepository.findByCategory(category).stream() //
                    .map(userAssembler::toModel) //
                    .toList();
        } else {
            users = userRepository.findAll().stream() //
                    .map(userAssembler::toModel) //
                    .toList();
        }

        return CollectionModel.of(users, linkTo(methodOn(UserController.class).getUsers(null)).withSelfRel());
    }

    @PostMapping("/users")
    ResponseEntity<EntityModel<User>> createUser(@RequestBody UserDTO userDTO) {

        EntityModel<User> entityModel = userAssembler.toModel(userRepository.save(
                new User(null, userDTO.getName(), userDTO.getDescription(), userDTO.getCategory())
        ));

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }
    /**
     * <p>This is a simple description of the method. . .
     * <a href="http://www.supermanisthegreatest.com">Superman!</a>
     * </p>
     * @param id the amount of incoming damage
     * @return the amount of health hero has after attack
     * @see <a href="http://www.link_to_jira/HERO-402">HERO-402</a>
     * @since 1.0
     */
    @GetMapping("/users/{id}")
    public EntityModel<User> getUser(@PathVariable Long id) {

        User user = userRepository.findById(id) //
                .orElseThrow(() -> new UserNotFoundException(id));

        return userAssembler.toModel(user);
    }

    @PutMapping("/users/{id}")
    ResponseEntity<Object> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setName(userDTO.getName());
        user.setDescription(userDTO.getDescription());
        user.setCategory(userDTO.getCategory());

        return ResponseEntity.ok(userAssembler.toModel(userRepository.save(user)));
    }

    @DeleteMapping("/users/{id}")
    ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        userRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}