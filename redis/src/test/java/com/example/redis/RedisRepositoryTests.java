package com.example.redis;

import com.example.redis.repository.Item;
import com.example.redis.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisRepositoryTests {
    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void createTest() {
        Item item = Item.builder()
                .name("keyboard")
                .description("Very Expensive Keyboard")
                .price(100000)
                .build();

        itemRepository.save(item);
    }

    @Test
    public void readOneTest() {
        Item item = itemRepository.findById(1L).orElseThrow();
        System.out.println(item.getDescription());
    }

    @Test
    public void updateTest() {
        Item item = itemRepository.findById(1L).orElseThrow();
        item.setDescription("On Sale!!!");
        itemRepository.save(item);


        item = itemRepository.findById(1L).orElseThrow();
        System.out.println(item.getDescription());
    }

    @Test
    public void deleteTest() {
        itemRepository.deleteById(1L);
    }
}
