package vehicles.abstracts;

import exceptions.InvalidOperationException;

public abstract class LandVehicle extends Vehicle {
    private int numWheels;

    public LandVehicle(String id, String model, double maxSpeed, int numWheels) throws InvalidOperationException {
        super(id, model, maxSpeed);
        this.numWheels = numWheels;
    }

    @Override
    public double estimateJourneyTime(double distance) {
        return (distance / this.maxSpeed) * 1.1; // Add 10% for traffic
    }

    public int getNumWheels() {
        return numWheels;
    }
}