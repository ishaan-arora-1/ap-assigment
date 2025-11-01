package vehicles.concrete;

import exceptions.InsufficientFuelException;
import exceptions.InvalidOperationException;
import exceptions.OverloadException;

import vehicles.abstracts.LandVehicle;

import vehicles.interfaces.CargoCarrier;
import vehicles.interfaces.FuelConsumable;
import vehicles.interfaces.Maintainable;

public class Truck extends LandVehicle implements FuelConsumable, CargoCarrier, Maintainable {
    private double fuelLevel;
    private final double cargoCapacity = 5000;
    private double currentCargo;
    private boolean maintenanceNeeded;
    private double mileageAtLastService;

    public Truck(String id, String model, double maxSpeed) throws InvalidOperationException {
        super(id, model, maxSpeed, 6);
        this.fuelLevel = 0;
        this.currentCargo = 0;
        this.maintenanceNeeded = false;
        this.mileageAtLastService = 0.0;
    }

    @Override
    public void move(double distance) throws InvalidOperationException {
        if (distance <= 0) {
            throw new InvalidOperationException("Distance must be positive.");
        }
        try {
            consumeFuel(distance);
            updateMileage(distance);
            System.out.println("Truck " + getId() + " is hauling cargo for " + distance + " km.");
        } catch (InsufficientFuelException e) {
            System.out.println("Error moving Truck " + getId() + ": " + e.getMessage());
        }
    }

    @Override
    public double calculateFuelEfficiency() {
        double baseEfficiency = 8.0; 
        if (this.currentCargo > (this.cargoCapacity * 0.5)) { // As per A1 PDF
            return baseEfficiency * 0.9; // 10% reduction
        }
        return baseEfficiency;
    }

    @Override
    public void refuel(double amount) throws InvalidOperationException {
         if (amount <= 0) throw new InvalidOperationException("Refuel amount must be positive.");
        this.fuelLevel += amount;
        System.out.printf("Truck %s refueled with %.1f liters. Current fuel: %.1f L.%n", getId(), amount, this.fuelLevel);
    }
    
    @Override
    public double getFuelLevel() { return fuelLevel; }

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
    public void loadCargo(double weight) throws OverloadException {
        if (this.currentCargo + weight > this.cargoCapacity) {
            throw new OverloadException("Cargo capacity exceeded.");
        }
        this.currentCargo += weight;
    }

    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight > this.currentCargo) {
            throw new InvalidOperationException("Cannot unload more cargo than is loaded.");
        }
        this.currentCargo -= weight;
    }

    @Override
    public double getCargoCapacity() { return cargoCapacity; }
    
    @Override
    public double getCurrentCargo() { return currentCargo; }
    
    @Override
    public void scheduleMaintenance() { this.maintenanceNeeded = true; }

    @Override
    public boolean needsMaintenance() {
        // Checks mileage *since last service*
        return (getCurrentMileage() - mileageAtLastService) > 10000 || maintenanceNeeded;
    }

    @Override
    public void performMaintenance() {
        this.maintenanceNeeded = false;
        this.mileageAtLastService = getCurrentMileage();
        System.out.println("Maintenance performed on Truck " + getId() + ".");
    }

    @Override
    public void setCurrentCargo(double weight) throws OverloadException {
        if (weight < 0 || weight > this.cargoCapacity) {
            throw new OverloadException("Invalid initial cargo weight.");
        }
        this.currentCargo = weight;
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