package org.zhvtsv.models;

import java.util.Arrays;

public class ExtentRequest {
    private double [] extent;

    public double[] getExtent() {
        return extent;
    }

    public void setExtent(double[] extent) {
        this.extent = extent;
    }

    @Override
    public String toString() {
        return "ExtentRequest{" +
                "extent=" + Arrays.toString(extent) +
                '}';
    }
}
