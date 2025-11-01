package fleet;

import exceptions.InvalidOperationException;
import vehicles.abstracts.Vehicle;
import vehicles.concrete.*;
import vehicles.interfaces.CargoCarrier;
import vehicles.interfaces.FuelConsumable;
import vehicles.interfaces.Maintainable;
import vehicles.interfaces.PassengerCarrier;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator; 
import java.util.HashMap;
import java.util.HashSet; 
import java.util.List;
import java.util.Map;
import java.util.Set; 
import java.util.stream.Collectors;

/**
use of ArrayList for dynamic storage.
use of HashSet for finding distinct models.
use of Comparators with collection.sort().
use of Collections.max and min.
 */
public class FleetManager {
    // 1. Use of Collections (ArrayList)
    // This ArrayList is the primary collection for dynamic vehicle storage.
    private List<Vehicle> fleet;

    public FleetManager() {
        this.fleet = new ArrayList<>(); // i have used array list as told in the assignment - task 1
    }

    // Checks for duplicate ID before adding
    public void addVehicle(Vehicle v) throws InvalidOperationException {
        for (Vehicle vehicle : fleet) {
            if (vehicle.getId().equalsIgnoreCase(v.getId())) {
                throw new InvalidOperationException("Vehicle with ID " + v.getId() + " already exists.");
            }
        }
        fleet.add(v);
        System.out.println("Vehicle " + v.getId() + " added to the fleet.");
    }

    public void removeVehicle(String id) throws InvalidOperationException {
        boolean removed = fleet.removeIf(v -> v.getId().equalsIgnoreCase(id));
        if (!removed) {
            throw new InvalidOperationException("Vehicle with ID " + id + " not found.");
        }
        System.out.println("Vehicle " + id + " removed.");
    }

    // Moves all vehicles, continues even if some fail
    public void startAllJourneys(double distance) {
        System.out.println("Starting all journeys of " + distance + " km");
        for (Vehicle v : fleet) {
            try {
                v.move(distance);
            } catch (InvalidOperationException e) {
                System.out.println("Could not start journey for " + v.getId() + ": " + e.getMessage());
            }
        }
    }

    public void maintainAll() {
        System.out.println("Performing maintenance on all vehicles needing it");
        for (Vehicle v : fleet) {
            if (v instanceof Maintainable) {
                Maintainable m = (Maintainable) v;
                if (m.needsMaintenance()) {
                    m.performMaintenance();
                }
            }
        }
    }

    public void refuelAll(double amount) {
        System.out.println("Refueling all applicable vehicles");
        for (Vehicle v : fleet) {
            if (v instanceof FuelConsumable) {
                try {
                    ((FuelConsumable) v).refuel(amount);
                } catch (InvalidOperationException e) {
                    System.out.println("Could not refuel " + v.getId() + ": " + e.getMessage());
                }
            }
        }
    }

    public List<Vehicle> getVehiclesNeedingMaintenance() {
        return fleet.stream()
                .filter(v -> v instanceof Maintainable && ((Maintainable) v).needsMaintenance())
                .collect(Collectors.toList());
    }

    // Returns all vehicles matching the given type name
    public List<Vehicle> searchByType(String type) {
        List<Vehicle> foundVehicles = new ArrayList<>();
        for (Vehicle v : fleet) {
            if (v.getClass().getSimpleName().equalsIgnoreCase(type)) {
                foundVehicles.add(v);
            }
        }
        return foundVehicles;
    }

    // Sorts by fuel efficiency, highest first
    public void sortFleetByEfficiency() {
        Collections.sort(fleet);
        System.out.println("Fleet sorted by fuel efficiency (highest first).");
    }

    // Sorts by speed, fastest first
    public void sortFleetByMaxSpeed() {
        fleet.sort(Comparator.comparingDouble(Vehicle::getMaxSpeed).reversed());
        System.out.println("Fleet sorted by max speed (fastest first).");
    }

    // Sorts alphabetically by model name
    public void sortFleetByModelName() {
        fleet.sort(Comparator.comparing(Vehicle::getModel, String.CASE_INSENSITIVE_ORDER));
        System.out.println("Fleet sorted by model name (A-Z).");
    }

    // Gets unique model names using HashSet
    private Set<String> getDistinctModels() {
        Set<String> models = new HashSet<>();
        for (Vehicle v : fleet) {
            models.add(v.getModel());
        }
        return models;
    }

    // Finds vehicle with highest max speed
    private Vehicle getFastestVehicle() {
        if (fleet.isEmpty()) return null;
        return Collections.max(fleet, Comparator.comparingDouble(Vehicle::getMaxSpeed));
    }

    // Finds vehicle with lowest max speed
    private Vehicle getSlowestVehicle() {
        if (fleet.isEmpty()) return null;
        return Collections.min(fleet, Comparator.comparingDouble(Vehicle::getMaxSpeed));
    }

    // Generates comprehensive fleet status report
    public String generateReport() {
        if (fleet.isEmpty()) {
            return "The fleet is currently empty.";
        }

        StringBuilder report = new StringBuilder();
        report.append("\nFleet Status Report\n");
        report.append("Total Vehicles: ").append(fleet.size()).append("\n\n");

        Map<String, Integer> typeCounts = new HashMap<>();
        double totalMileage = 0;
        double totalEfficiency = 0;
        int fuelVehicleCount = 0;

        for (Vehicle v : fleet) {
            String typeName = v.getClass().getSimpleName();
            typeCounts.put(typeName, typeCounts.getOrDefault(typeName, 0) + 1);
            
            totalMileage += v.getCurrentMileage();
            
            double efficiency = v.calculateFuelEfficiency();
            if (efficiency > 0) {
                totalEfficiency += efficiency;
                fuelVehicleCount++;
            }
            
            String maintenanceStatus;
            if (v instanceof Maintainable && ((Maintainable) v).needsMaintenance()) {
                maintenanceStatus = "Yes";
            } else {
                maintenanceStatus = "No";
            }
            
            report.append("  - ID: " + v.getId());
            report.append(", Type: " + typeName);
            report.append(", Model: " + v.getModel());
            report.append(", Mileage: " + v.getCurrentMileage() + " km");
            report.append(", Needs Maintenance: " + maintenanceStatus + "\n");
        }

        double averageEfficiency = 0;
        if (fuelVehicleCount > 0) {
            averageEfficiency = totalEfficiency / fuelVehicleCount;
        }

        Set<String> distinctModels = getDistinctModels();
        Vehicle fastest = getFastestVehicle();
        Vehicle slowest = getSlowestVehicle();

        report.append("\nSummary\n");
        report.append("Total Fleet Mileage: " + totalMileage + " km\n");
        report.append("Average Fuel Efficiency: " + averageEfficiency + " km/l\n");
        
        report.append("Distinct Vehicle Models: " + distinctModels.size() + " (" + distinctModels + ")\n");
        if (fastest != null) {
            report.append("Fastest Vehicle: " + fastest.getId() + " (" + fastest.getModel() + ") at " + fastest.getMaxSpeed() + " km/h\n");
        }
        if (slowest != null) {
            report.append("Slowest Vehicle: " + slowest.getId() + " (" + slowest.getModel() + ") at " + slowest.getMaxSpeed() + " km/h\n");
        }
        
        report.append("Vehicle Counts by Type:\n");
        for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
            report.append("  - ").append(entry.getKey()).append("s: ").append(entry.getValue()).append("\n");
        }

        return report.toString();
    }

    // --- PERSISTENCE METHODS ---

    public void saveToFile(String filename) throws IOException {
        // Using try-with-resources as required
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Vehicle v : fleet) {
                String csvLine = vehicleToCsv(v);
                writer.write(csvLine);
                writer.newLine();
            }
            System.out.println("Fleet saved successfully to " + filename);
        } catch (IOException e) {
            System.err.println("Error: Could not save fleet to file: " + e.getMessage());
            throw e;
        }
    }

    // Loads fleet from CSV, skips invalid lines
    public void loadFromFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            fleet.clear();
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    Vehicle v = createVehicleFromCsv(line);
                    fleet.add(v);
                } catch (Exception e) {
                    System.err.println("Warning: Skipping malformed line in CSV: " + line);
                }
            }
            System.out.println("Fleet loaded successfully from " + filename);
        } catch (FileNotFoundException e) {
            System.err.println("Error: The file '" + filename + "' was not found.");
        } catch (IOException e) {
            System.err.println("Error: Could not read fleet from file: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Converts a Vehicle object into a CSV string.
     * Order:
     * 0-Type, 1-ID, 2-Model, 3-MaxSpeed, 4-Mileage, 5-Efficiency,
     * 6-FuelLevel, 7-MaintenanceNeeded, 8-MileageAtLastService,
     * 9+ (Vehicle-specific data)
     */
    private String vehicleToCsv(Vehicle v) {
        // Common data for all vehicles
        String commonData = String.join(",",
            v.getClass().getSimpleName(),
            v.getId(),
            v.getModel(),
            String.valueOf(v.getMaxSpeed()),
            String.valueOf(v.getCurrentMileage()),
            String.valueOf(v.calculateFuelEfficiency()), 
            (v instanceof FuelConsumable) ? String.valueOf(((FuelConsumable) v).getFuelLevel()) : "0.0",
            (v instanceof Maintainable) ? String.valueOf(getMaintenanceFlag((Maintainable) v)) : "false",
            (v instanceof Maintainable) ? String.valueOf(getMaintenanceMileage((Maintainable) v)) : "0.0"
        );

        // Add type-specific data
        String specificData = "";
        if (v instanceof Car) {
            specificData = "," + ((Car) v).getCurrentPassengers();
        } else if (v instanceof Truck) {
            specificData = "," + ((Truck) v).getCurrentCargo();
        } else if (v instanceof Bus) {
            specificData = "," + ((Bus) v).getCurrentPassengers() + "," + ((Bus) v).getCurrentCargo();
        } else if (v instanceof Airplane) {
            specificData = "," + ((Airplane) v).getMaxAltitude() + "," + ((Airplane) v).getCurrentPassengers() + "," + ((Airplane) v).getCurrentCargo();
        } else if (v instanceof CargoShip) {
            specificData = "," + ((CargoShip) v).hasSail() + "," + ((CargoShip) v).getCurrentCargo();
        }
        
        return commonData + specificData;
    }
    
    // Helper methods to read maintenance fields for CSV saving
    private double getMaintenanceMileage(Maintainable m) {
        try {
            java.lang.reflect.Field field = m.getClass().getDeclaredField("mileageAtLastService");
            field.setAccessible(true);
            return field.getDouble(m);
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    private boolean getMaintenanceFlag(Maintainable m) {
        try {
            java.lang.reflect.Field field = m.getClass().getDeclaredField("maintenanceNeeded");
            field.setAccessible(true);
            return field.getBoolean(m);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Creates a Vehicle object by parsing a line of CSV text.
     */
    private Vehicle createVehicleFromCsv(String line) throws Exception {
        String[] data = line.split(",");
        
        // Read common base data (indices up to 8 are the same for all types)
        String type = data[0].trim();
        String id = data[1].trim();
        String model = data[2].trim();
        double maxSpeed = Double.parseDouble(data[3].trim());
        double mileage = Double.parseDouble(data[4].trim());
        // data[5] is efficiency, which is calculated, so we just read and skip it
        double fuelLevel = Double.parseDouble(data[6].trim());
        boolean maintenanceNeeded = Boolean.parseBoolean(data[7].trim());
        double mileageAtLastService = Double.parseDouble(data[8].trim());

        Vehicle v = null;
        int dataIndex = 9; // Start index for type-specific data

        // Create the specific vehicle object and read its type-specific data
        switch (type) {
            case "Car":
                v = new Car(id, model, maxSpeed);
                ((Car) v).setCurrentPassengers(Integer.parseInt(data[dataIndex++].trim()));
                break;
            case "Truck":
                v = new Truck(id, model, maxSpeed);
                ((Truck) v).setCurrentCargo(Double.parseDouble(data[dataIndex++].trim()));
                break;
            case "Bus":
                v = new Bus(id, model, maxSpeed);
                ((Bus) v).setCurrentPassengers(Integer.parseInt(data[dataIndex++].trim()));
                ((Bus) v).setCurrentCargo(Double.parseDouble(data[dataIndex++].trim()));
                break;
            case "Airplane":
                double maxAltitude = Double.parseDouble(data[dataIndex++].trim());
                v = new Airplane(id, model, maxSpeed, maxAltitude);
                ((Airplane) v).setCurrentPassengers(Integer.parseInt(data[dataIndex++].trim()));
                ((Airplane) v).setCurrentCargo(Double.parseDouble(data[dataIndex++].trim()));
                break;
            case "CargoShip":
                boolean hasSail = Boolean.parseBoolean(data[dataIndex++].trim());
                v = new CargoShip(id, model, maxSpeed, hasSail);
                ((CargoShip) v).setCurrentCargo(Double.parseDouble(data[dataIndex++].trim()));
                break;
            default:
                throw new IllegalArgumentException("Unknown vehicle type in CSV: " + type);
        }

        if (v != null) {
            v.setMileage(mileage);
            if (v instanceof FuelConsumable) {
                ((FuelConsumable) v).setFuelLevel(fuelLevel);
            }
            if (v instanceof Maintainable) {
                ((Maintainable) v).setMaintenanceNeeded(maintenanceNeeded);
                ((Maintainable) v).setMileageAtLastService(mileageAtLastService);
            }
        }
        return v;
    }
}