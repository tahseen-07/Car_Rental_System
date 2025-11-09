// Path: src/com/carrental/model/Car.java

package com.carrental.model;

public class Car {
    private String id;
    private String brand;
    private String model;
    private String type;
    private double rentPerDay;
    private boolean available;
    private String image;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getRentPerDay() { return rentPerDay; }
    public void setRentPerDay(double rentPerDay) { this.rentPerDay = rentPerDay; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}