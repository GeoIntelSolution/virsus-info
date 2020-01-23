package com.example.virus.vo.feature;


public class LineString  extends  Geometry<LngLatLikeArr>{
    public LineString() {
        this.setType("LineString");
    }

    @Override
    public BBOX getBBOX() {
        if (this.getPositions() == null) {
            return null;
        }
        LngLatLike firstNode = this.getPositions().get(0);
        BBOX bbox = new BBOX(firstNode.getX(), firstNode.getY(), firstNode.getX(), firstNode.getY());
        this.getPositions().forEach(lngLatlike -> {
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
        return bbox;
    }
}
