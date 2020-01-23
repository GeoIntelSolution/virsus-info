package com.example.virus.vo.feature;


import com.google.gson.annotations.SerializedName;

public abstract class Geometry<T>{
    private String type;
    @SerializedName(value = "coordinates")
    private T positions;
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public T getPositions() {
        return positions;
    }

    public void setPositions(T positions) {
        this.positions = positions;
    }

    public abstract BBOX getBBOX();

}
