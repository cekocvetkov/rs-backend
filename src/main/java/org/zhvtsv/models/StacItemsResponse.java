package org.zhvtsv.models;

import java.util.List;

public class StacItemsResponse {
    List<StacItem> stacItems;

    public List<StacItem> getStacItems() {
        return stacItems;
    }

    public void setStacItems(List<StacItem> stacItems) {
        this.stacItems = stacItems;
    }

    @Override
    public String toString() {
        return "StacItemsResponse{" +
                "stacItems=" + stacItems +
                '}';
    }
}
