package com.example.yaoha_000.map;


/**
 * Created by Haoyuan Chen on 2015/11/4.
 */
public class Coordinate {
    private double lat;
    private double lon;

    public Coordinate(double lon, double lat){
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat(){
        return this.lat;
    }
    public double getLon(){return this.lon;}
    public void setCoord(double lo, double la){ this.lat = la; this.lon = lo;}
    public String toString(){
        return this.getLon() + "/" + this.getLat();
    }
}
