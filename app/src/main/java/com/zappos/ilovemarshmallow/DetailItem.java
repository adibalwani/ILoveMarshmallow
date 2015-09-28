package com.zappos.ilovemarshmallow;

public class DetailItem {

    private String imageUrl;
    private String brandName;
    private String price;
    private String productName;
    private float productRating;
    private String productDescription;

    public DetailItem(String imageUrl, String brandName, String price,
                      String productName, float productRating, String productDescription) {
        this.imageUrl = imageUrl;
        this.brandName = brandName;
        this.price = price;
        this.productName = productName;
        this.productRating = productRating;
        this.productDescription = productDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public float getProductRating() {
        return productRating;
    }

    public void setProductRating(float productRating) {
        this.productRating = productRating;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
}
