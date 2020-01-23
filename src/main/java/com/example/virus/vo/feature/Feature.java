package com.example.virus.vo.feature;

import java.util.Map;

public class Feature {
    private String type;
    private Geometry geometry;
    private Map<String, Object> properties;

    public Feature() {
        this.type="Feature";
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
