package vehicles.concrete;

import exceptions.InsufficientFuelException;
import exceptions.InvalidOperationException;
import exceptions.OverloadException;

import vehicles.abstracts.LandVehicle;

import vehicles.interfaces.FuelConsumable;
import vehicles.interfaces.Maintainable;
import vehicles.interfaces.PassengerCarrier;

public class Car extends LandVehicle implements FuelConsumable, PassengerCarrier, Maintainable {
    private double fuelLevel;
    private final int passengerCapacity = 5;
    private int currentPassengers;
    private boolean maintenanceNeeded;
    private double mileageAtLastService;

    public Car(String id, String model, double maxSpeed) throws InvalidOperationException {
        super(id, model, maxSpeed, 4);
        this.fuelLevel = 0;
        this.currentPassengers = 0;
        this.maintenanceNeeded = false;
        this.mileageAtLastService = 0.0;
    }

    @Override
    public void move(double distance) throws InvalidOperationException {
        if (distance <= 0) {
            throw new InvalidOperationException("Distance must be positive");
        }
        try {
            consumeFuel(distance);
            updateMileage(distance);
            System.out.printf("Car %s is driving on the road for %.1f km.%n", getId(), distance);
        } catch (InsufficientFuelException e) {
            System.out.println("Error moving Car " + getId() + ": " + e.getMessage());
        }
    }

    @Override
    public double calculateFuelEfficiency() {
        return 15.0;
    }

    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) throw new InvalidOperationException("Refuel amount must be positive.");
        this.fuelLevel += amount;
        System.out.printf("Car %s refueled with %.1f liters. Current fuel: %.1f L.%n", getId(), amount, this.fuelLevel);
    }

    @Override
    public double getFuelLevel() {
        return fuelLevel;
    }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        double fuelNeeded = distance / calculateFuelEfficiency();
        if (fuelNeeded > this.fuelLevel) {
            throw new InsufficientFuelException("Not enough fuel for the journey.");
        }
        this.fuelLevel -= fuelNeeded;
        return fuelNeeded;
    }
    
    @Override
    public void boardPassengers(int count) throws OverloadException {
        if (this.currentPassengers + count > this.passengerCapacity) {
            throw new OverloadException("Passenger capacity exceeded.");
        }
        this.currentPassengers += count;
    }

    @Override
    public void disembarkPassengers(int count) throws InvalidOperationException {
        if (count > this.currentPassengers) {
            throw new InvalidOperationException("Cannot disembark more passengers than are on board.");
        }
        this.currentPassengers -= count;
    }
    
    @Override
    public int getPassengerCapacity() { return passengerCapacity; }
    
    @Override
    public int getCurrentPassengers() { return currentPassengers; }

    @Override
    public void scheduleMaintenance() {
        this.maintenanceNeeded = true;
    }

    @Override
    public boolean needsMaintenance() {
        // Checks mileage *since last service*
        return (getCurrentMileage() - mileageAtLastService) > 10000 || maintenanceNeeded;
    }

    @Override
    public void performMaintenance() {
        this.maintenanceNeeded = false;
        this.mileageAtLastService = getCurrentMileage();
        System.out.println("Maintenance performed on Car " + getId() + ".");
    }

    @Override
    public void setCurrentPassengers(int count) throws OverloadException {
        if (count < 0 || count > this.passengerCapacity) {
            throw new OverloadException("Invalid initial passenger count.");
        }
        this.currentPassengers = count;
    }

    @Override
    public void setFuelLevel(double amount) {
        this.fuelLevel = amount; // Used for loading from file
    }

    @Override
    public void setMileageAtLastService(double mileage) {
        this.mileageAtLastService = mileage;
    }

    @Override
    public void setMaintenanceNeeded(boolean needed) {
        this.maintenanceNeeded = needed;
    }
}