package org.zhvtsv.models;

public class ProductRequest {
    private String productId;

    private String productName;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "ProductRequest{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                '}';
    }
}
