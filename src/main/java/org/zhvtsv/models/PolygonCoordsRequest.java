package org.zhvtsv.models;

import java.util.Arrays;

public class PolygonCoordsRequest {
    private double [][] coords;

    public double[][] getCoords() {
        return coords;
    }

    public void setCoords(double[][] coords) {
        this.coords = coords;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(double[] coord : coords) {
            sb.append(Arrays.toString(coord));
        }
        return "PolygonCoordsRequest{" +
                "coords=" + sb +
                '}';
    }

}
