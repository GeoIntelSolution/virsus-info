package com.example.virus.repository;

import com.example.virus.vo.Event;
import java.util.List;

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

@EnableMongoRepositories
public interface MongoDB {
    void persistEvents(List<Event> events);
    List<Event> fetchEventByTime();
    String findLastRecentDate();
    void setLastTime(String lastTime);
}
