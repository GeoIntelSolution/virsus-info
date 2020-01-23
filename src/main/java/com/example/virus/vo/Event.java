package com.example.virus.vo;

import java.util.Date;

public class Event {
    int  Id;
    String name;
    String  updateTime;
    String description;
    String shapeId;
    String type;
    int quantity;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShapeId() {
        return shapeId;
    }

    public void setShapeId(String shapeId) {
        this.shapeId = shapeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return "Event{" +
                "Id=" + Id +
                ", name='" + name + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", description='" + description + '\'' +
                ", shapeId='" + shapeId + '\'' +
                ", type='" + type + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
