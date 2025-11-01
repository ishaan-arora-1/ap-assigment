package vehicles.concrete;

import vehicles.abstracts.AirVehicle;

import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import exceptions.InsufficientFuelException;

import vehicles.interfaces.CargoCarrier;
import vehicles.interfaces.FuelConsumable;
import vehicles.interfaces.Maintainable;
import vehicles.interfaces.PassengerCarrier;

public class Airplane extends AirVehicle implements FuelConsumable, PassengerCarrier, CargoCarrier, Maintainable {
    private double fuelLevel;
    private final int passengerCapacity = 200;
    private int currentPassengers;
    private final double cargoCapacity = 10000; 
    private double currentCargo;
    private boolean maintenanceNeeded;
    private double mileageAtLastService;

    public Airplane(String id, String model, double maxSpeed, double maxAltitude) throws InvalidOperationException {
        super(id, model, maxSpeed, maxAltitude);
        this.mileageAtLastService = 0.0;
    }

    @Override
    public double calculateFuelEfficiency() {
        return 5.0;
    }

    // interfaces, one by one

    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) throw new InvalidOperationException("Refuel amount must be positive.");
        this.fuelLevel = this.fuelLevel + amount;
        System.out.printf("Airplane %s refueled with %.1f liters. Current fuel: %.1f L.%n", getId(), amount, this.fuelLevel);
    }

    @Override
    public double getFuelLevel(){ 
        return fuelLevel; 
    }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        double fuelNeeded = distance/calculateFuelEfficiency();
        if (fuelNeeded > this.fuelLevel){
            throw new InsufficientFuelException("Not enough fuel.");
        }
        this.fuelLevel -= fuelNeeded;
        return fuelNeeded;
    }
    
    @Override
    public void boardPassengers(int count) throws OverloadException {
        if (this.currentPassengers+count > this.passengerCapacity) throw new OverloadException("Passenger capacity exceeded.");
        this.currentPassengers += count;
    }

    @Override
    public void disembarkPassengers(int count) throws InvalidOperationException {
        if (count>this.currentPassengers) throw new InvalidOperationException("Cannot disembark more passengers than on board.");
        this.currentPassengers-=count;
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
        if (this.currentCargo+weight > this.cargoCapacity){
            throw new OverloadException("Cargo capacity exceeded.");
        }   
        this.currentCargo += weight;
    }

    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight > this.currentCargo){
            throw new InvalidOperationException("Cannot unload more cargo than loaded.");
        }
        this.currentCargo -= weight;
    }

    @Override
    public double getCargoCapacity(){return cargoCapacity;}

    @Override
    public double getCurrentCargo(){return currentCargo;}

    @Override
    public void scheduleMaintenance(){this.maintenanceNeeded=true;}

    @Override
    public boolean needsMaintenance() {
        // Checks mileage *since last service*
        return (getCurrentMileage() - mileageAtLastService) > 10000 || maintenanceNeeded;
    }

    @Override
    public void performMaintenance(){
        this.maintenanceNeeded=false;
        this.mileageAtLastService = getCurrentMileage();
        System.out.println("Maintenance performed on Airplane " + getId());
    }

    @Override
    public void setCurrentPassengers(int count) throws OverloadException {
        if (count < 0 || count > this.passengerCapacity) {
            throw new OverloadException("Invalid initial passenger count.");
        }
        this.currentPassengers = count;
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
    public void move(double distance) throws InvalidOperationException {
        if (distance <= 0) throw new InvalidOperationException("Distance must be positive");
        try {
            consumeFuel(distance);
            updateMileage(distance);
            System.out.printf("Airplane %s is flying at %.1f ft for %.1f km.%n", getId(), getMaxAltitude(), distance);
        } catch (InsufficientFuelException e) {
            System.out.println("Error moving Airplane " + getId() + ": " + e.getMessage());
        }
    }
}