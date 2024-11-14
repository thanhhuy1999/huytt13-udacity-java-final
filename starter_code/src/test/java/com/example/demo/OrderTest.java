package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class OrderTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    private User user;
    private Cart cart;
    private List<UserOrder> userOrders;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("huytt13");

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setTotal(new BigDecimal("100.00"));
        cart.setItems(cart.getItems() == null ? new ArrayList<>() : cart.getItems());

        UserOrder order = UserOrder.createFromCart(cart);
        userOrders = Arrays.asList(order);

        user.setCart(cart);
    }

    @Test
    public void testSubmitOrder_UserFound() {
        when(userRepository.findByUsername("huytt13")).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit("huytt13");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody().getUser());

        verify(orderRepository, times(1)).save(any(UserOrder.class));
    }

    @Test
    public void testSubmitOrder_UserNotFound() {
        when(userRepository.findByUsername("notExistUser")).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit("notExistUser");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        verify(orderRepository, never()).save(any(UserOrder.class));
    }

    @Test
    public void testGetOrdersForUser_UserFound() {
        when(userRepository.findByUsername("huytt13")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(userOrders);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("huytt13");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userOrders, response.getBody());

        verify(orderRepository, times(1)).findByUser(user);
    }

    @Test
    public void testGetOrdersForUser_UserNotFound() {
        when(userRepository.findByUsername("notExistUser")).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("notExistUser");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        verify(orderRepository, never()).findByUser(any(User.class));
    }
}
