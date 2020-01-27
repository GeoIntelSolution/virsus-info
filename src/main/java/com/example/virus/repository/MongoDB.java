package com.example.virus.repository;

import com.example.virus.vo.Event;
import java.util.List;

import com.example.virus.vo.NewSummary;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

//@EnableMongoRepositories
public interface MongoDB {
    void persistEvents(List<Event> events);
    void persistEvents2(List<NewSummary> events);
    void persistCityEvents(List<Event> events);
    void persistCityEvents2(List<NewSummary> events);
    List<Event> fetchEventByTime();
    String findLastRecentDate();
    void setLastTime(String lastTime);
}
