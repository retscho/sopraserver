package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {

    private final UserService service;

    UserController(UserService service) {
        this.service = service;
    }

    //users
    @GetMapping("/users")
    Iterable<User> all(@RequestHeader("Token") String token) {
        return this.service.getUsers(token);
    }

    @PostMapping("/users")
    User createUser(@RequestBody User newUser) { return this.service.createUser(newUser); }

    //login
    @CrossOrigin
    @PutMapping("/login")
    User loginUser(@RequestBody User logUser){ return this.service.loginUser(logUser); }

    //logout
    @CrossOrigin
    @PutMapping("/logout")
    User logoutUser(@RequestBody User logoUser, @RequestHeader("Token") String token) {
        return this.service.logoutUser(logoUser, token);
    }

    //users/{id}
    @GetMapping("/users/{id}")
    User userProfile(@PathVariable Long id, @RequestHeader("Token") String token) {
        return this.service.getProfile(id, token);
    }

    @CrossOrigin
    @PutMapping("/users/{id}")
    User upUser(@RequestBody User upUser, @PathVariable Long id, @RequestHeader("Token") String token) {
        return this.service.updateUser(upUser, id, token);
    }
}