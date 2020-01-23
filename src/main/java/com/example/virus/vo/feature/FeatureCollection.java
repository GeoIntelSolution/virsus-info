package com.example.virus.vo.feature;

import java.util.ArrayList;
import java.util.List;

public class FeatureCollection {
    private String type;
    private List<Double> bbox;
    private List<Feature> features ;


    public FeatureCollection(){
        this.type="FeatureCollection";
        this.features=new ArrayList<>();
    }
    public List<Double> getBbox() {
        return bbox;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void addFeature(Feature feature){
        //set the bbox
//        feature.getGeometry().getCoordinates()
        this.features.add(feature);
    }

}
