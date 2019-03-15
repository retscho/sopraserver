package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
public class UserServiceTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void createUser() {
        this.setUp();

        //create User
        Assert.assertNull(userRepository.findByUsername("testUsername"));
        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");

        User createdUser = userService.createUser(testUser);

        Assert.assertNotNull(createdUser.getToken());
        Assert.assertEquals(createdUser.getStatus(), UserStatus.OFFLINE);
        Assert.assertEquals(createdUser, userRepository.findByToken(createdUser.getToken()));

        //Tear Down
        userRepository.deleteAll();
    }


    @Test
    public void loginUser() {
        this.setUp();

        //createUser
        Assert.assertNull(userRepository.findByUsername("testUsername"));
        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        userService.createUser(testUser);

        //login User
        Assert.assertNotNull(userRepository.findByUsername("testUsername"));
        User logUser = new User();
        logUser.setPassword("testPassword");
        logUser.setUsername("testUsername");

        User loggedInUser = userService.loginUser(logUser);

        Assert.assertNotNull(loggedInUser.getToken());
        Assert.assertEquals(loggedInUser.getStatus(), UserStatus.ONLINE);
        Assert.assertEquals(loggedInUser, userRepository.findByToken(loggedInUser.getToken()));

        //Tear Down
        userRepository.deleteAll();
    }


    @Test(expected = ResponseStatusException.class)
    public void loginUserIncorrectPassword() {
        this.setUp();

        //create User
        Assert.assertNull(userRepository.findByUsername("testUsername"));
        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        userService.createUser(testUser);

        //login User
        Assert.assertNotNull(userRepository.findByUsername("testUsername"));
        User logUser = new User();
        logUser.setPassword("incorrectPassword");
        logUser.setUsername("testUsername");
        userService.loginUser(logUser);
    }

    @Test
    public void changeUsername() {
        this.setUp();

        //create User
        Assert.assertNull(userRepository.findByUsername("testUsername"));
        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        userService.createUser(testUser);

        //login User
        Assert.assertNotNull(userRepository.findByUsername("testUsername"));
        User logUser = new User();
        logUser.setPassword("testPassword");
        logUser.setUsername("testUsername");
        User loggedInUser = userService.loginUser(logUser);

        //update User
        Assert.assertNull(userRepository.findByUsername("newUsername"));
        User newUser = new User();
        newUser.setUsername("newUsername");

        User updatedUser = userService.updateUser(newUser, loggedInUser.getId(), loggedInUser.getToken());

        Assert.assertNotNull(updatedUser.getToken());
        Assert.assertEquals(updatedUser.getStatus(), UserStatus.ONLINE);
        Assert.assertEquals(updatedUser, userRepository.findByToken(updatedUser.getToken()));
        Assert.assertEquals(updatedUser.getUsername(), newUser.getUsername());
        Assert.assertEquals(updatedUser.getBirthdayDate(), loggedInUser.getBirthdayDate());

        //Tear Down
        userRepository.deleteAll();
    }

    @Test(expected = ResponseStatusException.class)
    public void changeUsernameAlreadyExists() {
        this.setUp();

        //create User
        Assert.assertNull(userRepository.findByUsername("testUsername"));
        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        userService.createUser(testUser);

        //create another User
        Assert.assertNull(userRepository.findByUsername("testUsername2"));
        User testUser2 = new User();
        testUser2.setPassword("testPassword2");
        testUser2.setUsername("testUsername2");
        userService.createUser(testUser2);

        //login User
        Assert.assertNotNull(userRepository.findByUsername("testUsername"));
        User logUser = new User();
        logUser.setPassword("testPassword");
        logUser.setUsername("testUsername");
        User loggedInUser = userService.loginUser(logUser);

        //update User
        User newUser = new User();
        newUser.setUsername("testUsername2");
        userService.updateUser(newUser, loggedInUser.getId(), loggedInUser.getToken());

        //Tear Down
        userRepository.deleteAll();
    }

    @Test
    public void changeBirthday() {
        this.setUp();

        //create User
        Assert.assertNull(userRepository.findByUsername("testUsername"));
        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        userService.createUser(testUser);

        //login User
        Assert.assertNotNull(userRepository.findByUsername("testUsername"));
        User logUser = new User();
        logUser.setPassword("testPassword");
        logUser.setUsername("testUsername");
        User loggedInUser = userService.loginUser(logUser);

        //update User
        User newUser = new User();
        newUser.setUsername("");
        newUser.setBirthdayDate(new Date());

        User updatedUser = userService.updateUser(newUser, loggedInUser.getId(), loggedInUser.getToken());

        Assert.assertNotNull(updatedUser.getToken());
        Assert.assertEquals(updatedUser.getStatus(), UserStatus.ONLINE);
        Assert.assertEquals(updatedUser, userRepository.findByToken(updatedUser.getToken()));
        Assert.assertEquals(updatedUser.getBirthdayDate(), newUser.getBirthdayDate());
        Assert.assertEquals(updatedUser.getUsername(), loggedInUser.getUsername());

        //Tear Down
        userRepository.deleteAll();
    }

    @Test
    public void logoutUser() {
        this.setUp();

        //create User
        Assert.assertNull(userRepository.findByUsername("testUsername"));
        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");
        userService.createUser(testUser);

        //login User
        Assert.assertNotNull(userRepository.findByUsername("testUsername"));
        User logUser = new User();
        logUser.setPassword("testPassword");
        logUser.setUsername("testUsername");
        User loggedInUser = userService.loginUser(logUser);

        //logout User
        Assert.assertEquals(loggedInUser.getStatus(), UserStatus.ONLINE);

        User loggedOutUser = userService.logoutUser(loggedInUser, loggedInUser.getToken());

        Assert.assertNotNull(loggedOutUser.getToken());
        Assert.assertEquals(loggedOutUser.getStatus(), UserStatus.OFFLINE);
        Assert.assertEquals(loggedOutUser, userRepository.findByToken(loggedOutUser.getToken()));
        Assert.assertNotEquals(loggedOutUser.getToken(), loggedInUser.getToken());

        //Tear Down
        userRepository.deleteAll();
    }

}