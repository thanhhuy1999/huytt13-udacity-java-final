package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserTest {

    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        Common.DependencyInjection(userController, "userRepository", userRepository);
        Common.DependencyInjection(userController, "cartRepository", cartRepository);
        Common.DependencyInjection(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void testCreateUser_Success() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("huytt13");
        request.setPassword("thanhhuy123");
        request.setConfirmPassword("thanhhuy123");

        when(bCryptPasswordEncoder.encode("thanhhuy123")).thenReturn("hashedPassword");

        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals("huytt13", user.getUsername());
        assertEquals("hashedPassword", user.getPassword());
    }

    @Test
    public void testCreateUser_PasswordMismatch() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("huytt13");
        request.setPassword("password1");
        request.setConfirmPassword("password2");

        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    public void testCreateUser_PasswordTooShort() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("huytt13");
        request.setPassword("short");
        request.setConfirmPassword("short");

        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    public void testFindById_UserExists() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("huytt13");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.findById(userId);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User foundUser = response.getBody();
        assertNotNull(foundUser);
        assertEquals("huytt13", foundUser.getUsername());
    }

    @Test
    public void testFindById_UserNotExists() {
        Long userId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.findById(userId);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    public void testFindByUserName_UserExists() {
        String username = "huytt13";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(user);

        ResponseEntity<User> response = userController.findByUserName(username);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User foundUser = response.getBody();
        assertNotNull(foundUser);
        assertEquals(username, foundUser.getUsername());
    }

    @Test
    public void testFindByUserName_UserNotExists() {
        String username = "unknown";

        when(userRepository.findByUsername(username)).thenReturn(null);

        ResponseEntity<User> response = userController.findByUserName(username);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}

