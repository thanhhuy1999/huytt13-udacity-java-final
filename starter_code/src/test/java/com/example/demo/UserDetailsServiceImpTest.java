package com.example.demo;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.security.UserDetailsServiceImp;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;


import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserDetailsServiceImpTest {
    private final UserRepository userRepository = mock(UserRepository.class);

    private UserDetailsServiceImp userDetailsServiceImp;

    @Before
    public void setUp() {
        userDetailsServiceImp = new UserDetailsServiceImp(userRepository);
        Common.DependencyInjection(userDetailsServiceImp, "userRepository", userRepository);
    }

    @Test
    public void testLoadUserByUsername() {
        String username = "huytt13";
        User user = new User();
        user.setUsername(username);
        String password = "thanhhuy123";
        user.setPassword(password);
        user.setId(1L);

        when(userRepository.findByUsername(username)).thenReturn(user);

        UserDetails userDetails = userDetailsServiceImp.loadUserByUsername(username);
        assertNotNull(userDetails);

        assertEquals(password, userDetails.getPassword());
        assertEquals(username, userDetails.getUsername());
    }
}
