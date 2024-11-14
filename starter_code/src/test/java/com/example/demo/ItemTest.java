package com.example.demo;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(MockitoJUnitRunner.class)
public class ItemTest {
    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        itemController = new ItemController();
        Common.DependencyInjection(itemController, "itemRepository", itemRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    @Test
    public void testGetItems_Success() throws Exception {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setPrice(BigDecimal.valueOf(10.0));

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setPrice(BigDecimal.valueOf(20.0));

        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

        mockMvc.perform(get("/api/item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Item 2"));

        verify(itemRepository, times(1)).findAll();
    }

    @Test
    public void testGetItemById_Success() throws Exception {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setPrice(BigDecimal.valueOf(10.0));

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        mockMvc.perform(get("/api/item/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item 1"))
                .andExpect(jsonPath("$.price").value(10.0));

        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetItemById_NotFound() throws Exception {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/item/1"))
                .andExpect(status().isNotFound());

        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetItemsByName_Success() throws Exception {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setPrice(BigDecimal.valueOf(10.0));

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 1");
        item2.setPrice(BigDecimal.valueOf(20.0));

        when(itemRepository.findByName("Item 1")).thenReturn(Arrays.asList(item1, item2));

        mockMvc.perform(get("/api/item/name/Item 1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Item 1"));

        verify(itemRepository, times(1)).findByName("Item 1");
    }

    @Test
    public void testGetItemsByName_NotFound() throws Exception {
        when(itemRepository.findByName("NonExistentItem")).thenReturn(null);

        mockMvc.perform(get("/api/item/name/NonExistentItem"))
                .andExpect(status().isNotFound());

        verify(itemRepository, times(1)).findByName("NonExistentItem");
    }
}
