package com.example.virus.vo.feature;


public class BBOX {
    public double minx;
    public double miny;
    public   double maxx;
    public double maxy;

    public BBOX(double minx, double miny, double maxx, double maxy) {
        this.minx = minx;
        this.miny = miny;
        this.maxx = maxx;
        this.maxy = maxy;
    }

    public BBOX() {
        this.minx= Double.MAX_VALUE;
        this.miny= Double.MAX_VALUE;
        this.maxx= Double.MIN_VALUE;
        this.maxy= Double.MIN_VALUE;
    }

    @Override
    public String toString() {
        return "BBOX{" +
                "minx=" + minx +
                ", miny=" + miny +
                ", maxx=" + maxx +
                ", maxy=" + maxy +
                '}';
    }

    void merge(BBOX bbox){
            if(this.minx<bbox.minx){
                bbox.minx=this.minx;
            }

            if(this.miny<bbox.miny){
                bbox.miny=this.miny;
            }

            if(this.maxy>bbox.maxy)
            {
                bbox.maxy=this.maxy;
            }

            if(this.maxx>bbox.maxx){
                bbox.maxx=this.maxx;
            }
    }
}
