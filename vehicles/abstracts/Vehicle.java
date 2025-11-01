package vehicles.abstracts;

import exceptions.InvalidOperationException;

public abstract class Vehicle implements Comparable<Vehicle> {

    private String id;
    private String model;
    protected double maxSpeed;
    private double currentMileage;

    public Vehicle(String id, String model, double maxSpeed) throws InvalidOperationException {
        // as mentioned to check id not null
        if(id==null || id.isEmpty()){
            throw new InvalidOperationException("Id cannot be null or empty");
        }

        this.id = id;
        this.model = model;
        this.maxSpeed = maxSpeed;
        this.currentMileage = 0.0;
        // its been told to init to 0.0
    }


    public abstract void move(double distance) throws InvalidOperationException;
    // cant put < 0 here, so ill put in all submethods

    public abstract double calculateFuelEfficiency();
    public abstract double estimateJourneyTime(double distance);
    
    
    public void displayInfo(){
        System.out.println("Vehicle ID: " + id);
        System.out.println("Model: " + model);
        System.out.println("Max Speed: " + maxSpeed);
        System.out.println("Current Mileage: " + currentMileage);
    }

    public double getCurrentMileage(){
        return currentMileage;
    }
    
    public String getId(){
        return id;
    }

    public String getModel(){
        return model;
    }

    public double getMaxSpeed(){
        return maxSpeed;
    }

    protected void updateMileage(double distance){
        this.currentMileage += distance;
    }

    @Override
    public int compareTo(Vehicle other){
        return Double.compare(other.calculateFuelEfficiency(), this.calculateFuelEfficiency());
    }
}