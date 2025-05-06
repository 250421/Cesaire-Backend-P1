package com.authenthication.auth_session.Entity;

import jakarta.persistence.*;


@Entity
@Table(name = "products")
public class Products {
    @Id
    @Column(name="user_id", length = 45)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userid;

   @Column(name="name", length = 255)
    private String name;

    @Column(name="stock", length = 255)
    private Integer stock;

    @Column(name="price", length = 255)
    private Double price;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "postion", length = 255)
    private String position;

    @Column(columnDefinition = "TEXT")
    private String review;

    // Relationship with User who created the product
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;


    public Products(int userid, String name, Integer stock, Double price, String imageUrl, String position, String review, User createdBy) {
        this.userid = userid;
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.imageUrl = imageUrl;
        this.position = position;
        this.review = review;
        this.createdBy = createdBy;
    }

    public Products() {
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }


    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
}


