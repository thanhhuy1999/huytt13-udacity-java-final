package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class CartTest {

    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        cartController = new CartController();
        Common.DependencyInjection(cartController, "userRepository", userRepository);
        Common.DependencyInjection(cartController, "cartRepository", cartRepository);
        Common.DependencyInjection(cartController, "itemRepository", itemRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
    }

    @Test
    public void testAddToCart_Success() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        user.setId(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        user.setCart(cart);

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setPrice(BigDecimal.valueOf(10.00));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        mockMvc.perform(post("/api/cart/addToCart")
                        .contentType("application/json")
                        .content("{\"username\":\"testUser\", \"itemId\":1, \"quantity\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L)) // Kiểm tra ID của giỏ hàng
                .andExpect(jsonPath("$.items.size()").value(2)); // Kiểm tra số lượng items trong giỏ hàng

        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    public void testAddToCart_UserNotFound() throws Exception {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("nonExistentUser");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername("nonExistentUser")).thenReturn(null);

        mockMvc.perform(post("/api/cart/addToCart")
                        .contentType("application/json")
                        .content("{\"username\":\"nonExistentUser\", \"itemId\":1, \"quantity\":2}"))
                .andExpect(status().isNotFound());

        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testAddToCart_ItemNotFound() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        user.setId(1L);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/cart/addToCart")
                        .contentType("application/json")
                        .content("{\"username\":\"testUser\", \"itemId\":1, \"quantity\":2}"))
                .andExpect(status().isNotFound());

        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testRemoveFromCart_Success() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        user.setId(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        user.setCart(cart);

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setPrice(BigDecimal.valueOf(10.00));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        mockMvc.perform(post("/api/cart/removeFromCart")
                        .contentType("application/json")
                        .content("{\"username\":\"testUser\", \"itemId\":1, \"quantity\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.items.size()").value(0));

        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    public void testRemoveFromCart_UserNotFound() throws Exception {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("nonExistentUser");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepository.findByUsername("nonExistentUser")).thenReturn(null);

        mockMvc.perform(post("/api/cart/removeFromCart")
                        .contentType("application/json")
                        .content("{\"username\":\"nonExistentUser\", \"itemId\":1, \"quantity\":1}"))
                .andExpect(status().isNotFound());

        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testRemoveFromCart_ItemNotFound() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        user.setId(1L);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/cart/removeFromCart")
                        .contentType("application/json")
                        .content("{\"username\":\"testUser\", \"itemId\":1, \"quantity\":1}"))
                .andExpect(status().isNotFound());

        verify(cartRepository, never()).save(any(Cart.class));
    }
}
