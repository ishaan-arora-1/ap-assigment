package vehicles.concrete;

import exceptions.InsufficientFuelException;
import exceptions.InvalidOperationException;
import exceptions.OverloadException;

import vehicles.abstracts.LandVehicle;

import vehicles.interfaces.CargoCarrier;
import vehicles.interfaces.FuelConsumable;
import vehicles.interfaces.Maintainable;
import vehicles.interfaces.PassengerCarrier;

public class Bus extends LandVehicle implements FuelConsumable,PassengerCarrier,CargoCarrier,Maintainable{
    private double fuelLevel;
    private final int passengerCapacity = 50;
    private int currentPassengers;
    private final double cargoCapacity = 500;
    private double currentCargo;
    private boolean maintenanceNeeded;

    public Bus(String id, String model, double maxSpeed) throws InvalidOperationException {
        super(id, model, maxSpeed, 6);
    }
    
    @Override
    public void move(double distance) throws InvalidOperationException {
        if (distance <= 0) throw new InvalidOperationException("Distance must be positive");
        try {
            consumeFuel(distance);
            updateMileage(distance);
            System.out.printf("Bus %s is transporting passengers and cargo for %.1f km.%n", getId(), distance);
        } catch (InsufficientFuelException e) {
            System.out.println("Error moving Bus " + getId() + ": " + e.getMessage());
        }
    }

    @Override
    public double calculateFuelEfficiency() {
        return 10.0;
    }
    
    // all interfaces one by one
    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount<= 0)throw new InvalidOperationException("Refuel amount must be positive");
        this.fuelLevel= this.fuelLevel + amount;
    }

    @Override
    public double getFuelLevel() { return fuelLevel; }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        double fuelNeeded = distance / calculateFuelEfficiency();
        if (fuelNeeded > this.fuelLevel) throw new InsufficientFuelException("Not enough fuel");
        this.fuelLevel = this.fuelLevel - fuelNeeded;
        return fuelNeeded;
    }

    @Override
    public void boardPassengers(int count) throws OverloadException {
        if (this.currentPassengers+count > this.passengerCapacity) throw new OverloadException("Passenger capacity exceeded");
        this.currentPassengers= this.currentPassengers +count;
    }

    @Override
    public void disembarkPassengers(int count) throws InvalidOperationException {
        if (count > this.currentPassengers) throw new InvalidOperationException("Cannot disembark more passengers than on board");
        this.currentPassengers = this.currentPassengers - count;
    }

    @Override
    public int getPassengerCapacity(){ 
        return passengerCapacity;
    }

    @Override
    public int getCurrentPassengers(){
        return currentPassengers; 
    }

    @Override
    public void loadCargo(double weight) throws OverloadException {
        if (this.currentCargo + weight > this.cargoCapacity){
            throw new OverloadException("Cargo capacity exceeded.");
        }
        this.currentCargo = this.currentCargo + weight;
    }

    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight > this.currentCargo) throw new InvalidOperationException("Cannot unload more cargo than loaded.");
        this.currentCargo = this.currentCargo - weight;
    }
    @Override
    public double getCargoCapacity() {
        return cargoCapacity;
    }
    @Override
    public double getCurrentCargo() {return currentCargo;}

    @Override
    public void scheduleMaintenance() {this.maintenanceNeeded = true;}
    @Override
    public boolean needsMaintenance() {return getCurrentMileage() > 10000 || maintenanceNeeded;}
    @Override
    public void performMaintenance() {
        this.maintenanceNeeded = false;
        System.out.println("Maintenance performed on Bus " + getId());
    }
}