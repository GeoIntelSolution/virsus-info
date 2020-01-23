package com.example.virus.vo.feature;

import java.util.ArrayList;

public class LngLatLike extends ArrayList<Double> {

    public LngLatLike() {
    }

    public LngLatLike(double x, double y) {
        this.add(x);
        this.add(y);
    }

    public LngLatLike(double x, double y,double z) {
        this.add(x);
        this.add(y);
        this.add(z);
    }

    public LngLatLike(double x, double y,double z,double m) {
        this.add(x);
        this.add(y);
        this.add(z);
        this.add(m);
    }

    public double getX(){
        return this.get(0);
    }

    public double getY(){
        return this.get(1);
    }


}
