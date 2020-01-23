package com.example.virus.repository;

import com.example.virus.vo.Event;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class MongoDBImplTest {
    @Test
    public void testReadMongo() {
        MongoDB mongoDB = new MongoDBImpl();
        java.util.List lists =new ArrayList();
        for (int i = 0; i < 10; i++) {
            Event event =new Event();
            event.setId(2);
            lists.add(event);
        }

        mongoDB.persistEvents(lists);
    }
}