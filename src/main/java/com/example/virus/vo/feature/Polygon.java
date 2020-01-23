package com.example.virus.vo.feature;



public class Polygon extends Geometry<PolygonArr> {
    public Polygon() {
        this.setType("Polygon");
    }




    @Override
    public BBOX getBBOX() {
        if(this.getPositions()==null){
            return null;
        }
        BBOX bbox = new BBOX();
        PolygonArr positions = this.getPositions();
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
