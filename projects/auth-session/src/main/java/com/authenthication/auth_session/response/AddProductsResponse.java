package com.authenthication.auth_session.response;




public class AddProductsResponse {
    private Integer id;
    private String name;
    private Integer stock;
    private Integer createdById;
    private String message;
    private Boolean status;

    public AddProductsResponse(String message, Boolean status) {
        this.message = message;
        this.status = status;
    }

    public AddProductsResponse(Integer id, String name, Integer stock, Integer createdById, String message, Boolean status) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.createdById = createdById;
        this.message = message;
        this.status = status;
    }

    public AddProductsResponse() {
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

    public Integer getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Integer createdById) {
        this.createdById = createdById;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AddProductsResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", stock=" + stock +
                ", createdById=" + createdById +
                ", message='" + message + '\'' +
                ", status=" + status +
                '}';
    }
}


