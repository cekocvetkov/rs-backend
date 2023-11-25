package org.zhvtsv.models;


import org.zhvtsv.odata.QuickLookProduct;

import java.util.List;

public class QuickLookProductResponse {
    private List<QuickLookProduct> quickLookProductList;

    public QuickLookProductResponse(List<QuickLookProduct> quickLookProductList) {
        this.quickLookProductList = quickLookProductList;
    }

    public List<QuickLookProduct> getQuickLookProductList() {
        return quickLookProductList;
    }

    public void setQuickLookProductList(List<QuickLookProduct> quickLookProductList) {
        this.quickLookProductList = quickLookProductList;
    }

    @Override
    public String toString() {
        return "QuickLookProductResponse{" +
                "quickLookProductList=" + quickLookProductList +
                '}';
    }
}
