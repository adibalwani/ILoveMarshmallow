package com.zappos.ilovemarshmallow;

public class SingleItem {
    private String imageUrl;
    private String productName;
    private String price;
    private String brandName;
    private String asin;
    private float productRating;

    public SingleItem(String imageUrl, String productName, String price,
                      String brandName, String asin, float productRating) {
        this.imageUrl = imageUrl;
        this.productName = productName;
        this.price = price;
        this.brandName = brandName;
        this.asin = asin;
        this.productRating = productRating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public float getProductRating() {
        return productRating;
    }

    public void setProductRating(float productRating) {
        this.productRating = productRating;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }
}
