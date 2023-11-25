package org.zhvtsv.odata;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public class QuickLookProduct {
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Assets")
    private Asset[] assets;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Asset[] getAssets() {
        return assets;
    }

    public void setAssets(Asset[] assets) {
        this.assets = assets;
    }

    @Override
    public String toString() {
        return "QuickLookProduct{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", assets=" + Arrays.toString(assets) +
                '}';
    }
}
