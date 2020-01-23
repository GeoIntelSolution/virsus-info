package com.example.virus.vo.feature;


import java.util.ArrayList;

public class MultiLineString extends Geometry<PolygonArr> {
    public MultiLineString() {
        this.setType("MultiLineString");
    }

    @Override
    public BBOX getBBOX() {
        if(this.getPositions()==null){
            return null;
        }
        BBOX bbox = new BBOX();
        ArrayList<LngLatLikeArr> positions = this.getPositions();
        positions.forEach(lineString -> {
            lineString.forEach(lngLatlike->{
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

        return bbox;
    }
}
