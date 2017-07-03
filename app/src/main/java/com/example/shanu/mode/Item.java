package com.example.shanu.mode;


public class Item {
    private String name;
    private double quantity;
    private double price;

    public Item() {
    }

    public Item(String name, double quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public double getprice() {
        return price;
    }

    public void setprice(double price) {
        this.price = price;
    }

    public double getquantity() {
        return quantity;
    }

    public void setquantity(double quantity) {
        this.quantity = quantity;
    }
}