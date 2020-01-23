package com.example.virus.repository;

import com.example.virus.vo.Event;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
@EnableMongoRepositories
public class MongoDBImpl implements MongoDB {
    private static String lastTime ="";


    @Override
    public  void setLastTime(String lastTime) {
        MongoDBImpl.lastTime = lastTime;
    }

    @Resource(name = "mongoTemplate")
    public MongoTemplate mongoTemplate;

    @Override
    public void persistEvents(List<Event> events) {
//        mongoTemplate.insert(events);
        if(events==null||events.size()==0) return;
          lastTime=events.get(0).getUpdateTime();
          mongoTemplate.insert(events,"events");
    }

    @Override
    public List<Event> fetchEventByTime() {
        return null;
    }

    @Override
    public String findLastRecentDate() {
        return lastTime;
    }
}
