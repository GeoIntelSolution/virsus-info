package com.example.virus.vo.feature;

public class Point extends Geometry<LngLatLike>{
    public Point() {
        this.setType("Point");
    }


    @Override
    public BBOX getBBOX() {
        BBOX bbox = new BBOX();
        bbox.maxy= bbox.miny=this.getPositions().getY();
        bbox.maxx= bbox.minx=this.getPositions().getX();
        bbox.miny= bbox.miny=this.getPositions().getY();
        bbox.minx= bbox.minx=this.getPositions().getX();
        return bbox;
    }
}
