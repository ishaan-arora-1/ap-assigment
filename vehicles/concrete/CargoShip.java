package vehicles.concrete;

import exceptions.InsufficientFuelException;
import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import vehicles.abstracts.WaterVehicle;
import vehicles.interfaces.CargoCarrier;
import vehicles.interfaces.FuelConsumable;
import vehicles.interfaces.Maintainable;

public class CargoShip extends WaterVehicle implements CargoCarrier, Maintainable, FuelConsumable {
    private final double cargoCapacity = 50000; // kg
    private double currentCargo;
    private boolean maintenanceNeeded;
    private double mileageAtLastService;
    private double fuelLevel;

    public CargoShip(String id, String model, double maxSpeed, boolean hasSail) throws InvalidOperationException {
        super(id, model, maxSpeed, hasSail);
        this.mileageAtLastService = 0.0;
    }
    
    @Override
    public void move(double distance) throws InvalidOperationException {
        if (distance <= 0) throw new InvalidOperationException("Distance must be positive.");
        try {
            if (!hasSail()) {
                consumeFuel(distance);
            }
            updateMileage(distance);
            System.out.printf("CargoShip %s is sailing with cargo for %.1f km.%n", getId(), distance);
        } catch (InsufficientFuelException e) {
            System.out.println("Error moving CargoShip " + getId() + ": " + e.getMessage());
        }
    }

    @Override
    public double calculateFuelEfficiency() {
        return hasSail() ? 0 : 4.0;
    }
    
    @Override
    public void loadCargo(double weight) throws OverloadException {
        if (this.currentCargo + weight > this.cargoCapacity) throw new OverloadException("Cargo capacity exceeded.");
        this.currentCargo += weight;
    }

    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight > this.currentCargo) throw new InvalidOperationException("Cannot unload more cargo than loaded.");
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
        System.out.println("Maintenance performed on CargoShip " + getId() + ".");
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

    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (hasSail()) throw new InvalidOperationException("Sailing ships do not consume fuel.");
        if (amount <= 0) throw new InvalidOperationException("Refuel amount must be positive.");
        this.fuelLevel += amount;
    }
    @Override
    public double getFuelLevel() { return hasSail() ? 0 : fuelLevel; }
    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        if (hasSail()) return 0;
        double fuelNeeded = distance / calculateFuelEfficiency();
        if (fuelNeeded > this.fuelLevel) throw new InsufficientFuelException("Not enough fuel.");
        this.fuelLevel = this.fuelLevel - fuelNeeded;
        return fuelNeeded;
    }
}