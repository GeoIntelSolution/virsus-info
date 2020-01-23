package com.example.virus.vo.feature;


import java.util.ArrayList;

public class MultiPolygon extends Geometry<ArrayList<PolygonArr>> {
    public MultiPolygon() {
        this.setType("MultiPolygon");
    }

    @Override
    public BBOX getBBOX() {
        if(this.getPositions()==null){
            return null;
        }
        BBOX bbox = new BBOX();
        this.getPositions().forEach(polygon->{
            polygon.forEach(ring->{
                ring.forEach(lngLatlike->{
                    if (lngLatlike.getX() < bbox.minx) {
                        bbox.minx = lngLatlike.getX();
                    } else if (lngLatlike.getX() > bbox.maxx) {
                        bbox.maxx = lngLatlike.getX();
                    }

                    if (lngLatlike.getY() < bbox.miny) {
                        bbox.miny = lngLatlike.getY();
                    } else if (lngLatlike.getY() > bbox.maxy) {
                        bbox.maxy = lngLatlike.getY();
                    }
                });
            });
        });
        return bbox;
    }
}
