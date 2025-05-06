package com.authenthication.auth_session.Dto;




public class ProductsDto {
    private Integer id;
    private String name;
    private Integer stock;
    private Double price;
    private String imageUrl;
    private String review;
    private Integer createdById;


    public ProductsDto(Integer id, String name, Integer stock, Double price, String imageUrl, String review, Integer createdById) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.imageUrl = imageUrl;
        this.review = review;
        this.createdById = createdById;
    }

    public ProductsDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Integer getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Integer createdById) {
        this.createdById = createdById;
    }

    @Override
    public String toString() {
        return "ProductsDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", stock=" + stock +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                ", review='" + review + '\'' +
                ", createdById=" + createdById + '\'' +
                '}';
}

}


