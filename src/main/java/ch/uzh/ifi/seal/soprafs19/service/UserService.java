package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //Dashboard
    public Iterable<User> getUsers(String token) {
        if (this.userRepository.existsByToken(token)) {
            if (this.userRepository.findByToken(token).getStatus().equals(UserStatus.ONLINE)) {
                return this.userRepository.findAll();
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect Token.");
    }

    //Register
    public User createUser(User newUser) {
        if (!this.userRepository.existsByUsername(newUser.getUsername())) {
            newUser.setToken(UUID.randomUUID().toString());
            newUser.setStatus(UserStatus.OFFLINE);
            newUser.setCreationDate();
            newUser.setPassword(String.valueOf(newUser.getPassword()));
            userRepository.save(newUser);
            log.debug("Created Information for User: {}", newUser);
            return newUser;
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken.");
    }

    //Login
    public User loginUser(User logUser) {
        if (this.userRepository.existsByUsername(logUser.getUsername())) {
            User regUser = this.userRepository.findByUsername(logUser.getUsername());
            if (logUser.getPassword().contentEquals(regUser.getPassword())) {
                regUser.setStatus(UserStatus.ONLINE);
                regUser.setToken(UUID.randomUUID().toString());
                log.debug("Logged in User: {}", regUser);
                return regUser;
            }
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password is incorrect.");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist.");
    }

    //Game, Logout
    public User logoutUser(User logoUser, String token) {
        if (this.userRepository.existsById(logoUser.getId())) {
            Optional<User> opUser = this.userRepository.findById(logoUser.getId());
            if (opUser.isPresent()) {
                User onlineUser = opUser.get();
                if (onlineUser.getToken().equals(token)) {
                    onlineUser.setStatus(UserStatus.OFFLINE);
                    onlineUser.setToken(UUID.randomUUID().toString());
                    return onlineUser;
                }
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect token.");
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
    }

    //Profile
    public User getProfile(Long userId, String token) {
        if (this.userRepository.existsByToken(token)) {
            if (this.userRepository.findByToken(token).getStatus().equals(UserStatus.ONLINE)) {
                if (this.userRepository.existsById(userId)) {
                    Optional<User> profUser = this.userRepository.findById(userId);
                    if (profUser.isPresent()) {
                        return profUser.get();
                    }
                }
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect Token.");




    }

    public User updateUser(User upUser, Long userId, String token) {
        if (this.userRepository.existsByToken(token)) {
            if (this.userRepository.findByToken(token).getStatus().equals(UserStatus.ONLINE)) {
                if (this.userRepository.existsById(userId)) {
                    Optional<User> opUser = this.userRepository.findById(userId);
                    if (opUser.isPresent()) {
                        User toDoUser = opUser.get();
                        if (upUser.getBirthdayDate() != null) {
                            toDoUser.setBirthdayDate(upUser.getBirthdayDate());
                        }
                        if (!(upUser.getUsername().equals(toDoUser.getUsername()) || upUser.getUsername().equals(""))) {
                            if (!this.userRepository.existsByUsername(upUser.getUsername())) {
                                toDoUser.setUsername(upUser.getUsername());
                            } else {
                                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken.");
                            }
                        }
                        return toDoUser;
                    }
                }
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect Token.");
    }
}