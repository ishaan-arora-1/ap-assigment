package fleet;

import exceptions.InvalidOperationException;
import vehicles.abstracts.Vehicle;
import vehicles.concrete.*;
import vehicles.interfaces.FuelConsumable;
import vehicles.interfaces.Maintainable;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator; // Added for A2
import java.util.HashMap;
import java.util.HashSet; // Added for A2
import java.util.List;
import java.util.Map;
import java.util.Set; // Added for A2
import java.util.stream.Collectors;

/**
 * Manages the fleet of vehicles.
 * For Assignment 2, this class is updated to demonstrate:
 * - Use of ArrayList for dynamic storage.
 * - Use of HashSet (in generateReport) for finding distinct models.
 * - Use of Comparators (lambda expressions) for sorting by different criteria.
 * - Use of Collections.max/min for data analysis (fastest/slowest).
 */
public class FleetManager {
    // 1. Use of Collections (ArrayList)
    // This ArrayList is the primary collection for dynamic vehicle storage[cite: 24, 35].
    private List<Vehicle> fleet;

    public FleetManager() {
        this.fleet = new ArrayList<>(); // i have used array list as told in the assignment - task 1
    }

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

    public List<Vehicle> searchByType(String type) {
        List<Vehicle> foundVehicles = new ArrayList<>();
        for (Vehicle v : fleet) {
            if (v.getClass().getSimpleName().equalsIgnoreCase(type)) {
                foundVehicles.add(v);
            }
        }
        return foundVehicles;
    }

    // --- NEW & UPDATED METHODS FOR ASSIGNMENT 2 ---

    // 2. Sorting and Ordering (Comparable) [cite: 75]
    // This method (from A1) uses the "Comparable" interface implemented in Vehicle.
    public void sortFleetByEfficiency() {
        Collections.sort(fleet);
        System.out.println("Fleet sorted by fuel efficiency (highest first).");
    }

    // 2. Sorting and Ordering (Comparator) 
    // New method for A2. Uses a lambda expression as a Comparator to sort by speed.
    public void sortFleetByMaxSpeed() {
        // Sorts from highest speed to lowest
        fleet.sort(Comparator.comparingDouble(Vehicle::getMaxSpeed).reversed());
        System.out.println("Fleet sorted by max speed (fastest first).");
    }

    // 2. Sorting and Ordering (Comparator) 
    // New method for A2. Uses a Comparator to sort by model name alphabetically.
    public void sortFleetByModelName() { // sorting done instead of tree set as allowed in the assigment - task 2
        // Sorts alphabetically (A-Z) by model name
        fleet.sort(Comparator.comparing(Vehicle::getModel, String.CASE_INSENSITIVE_ORDER));
        System.out.println("Fleet sorted by model name (A-Z).");
    }

    // 3. Data Analysis & Use of Collections (HashSet) [cite: 25, 36]
    // New helper method for A2. Demonstrates HashSet to find unique model names.
    private Set<String> getDistinctModels() { // i have used hashset as told in the assignment - task 2
        Set<String> models = new HashSet<>();
        for (Vehicle v : fleet) {
            models.add(v.getModel());
        }
        return models;
    }

    // 4. Data Analysis (Collections.max/min) [cite: 27]
    // New helper method for A2. Finds the fastest vehicle.
    private Vehicle getFastestVehicle() {
        if (fleet.isEmpty()) return null;
        // Uses Collections.max with a Comparator to find the vehicle with the highest maxSpeed.
        return Collections.max(fleet, Comparator.comparingDouble(Vehicle::getMaxSpeed)); // used collection max as told in the assignment - task 3
    }

    // 4. Data Analysis (Collections.max/min) [cite: 27]
    // New helper method for A2. Finds the slowest vehicle.
    private Vehicle getSlowestVehicle() {
        if (fleet.isEmpty()) return null;
        // Uses Collections.min with a Comparator to find the vehicle with the lowest maxSpeed.
        return Collections.min(fleet, Comparator.comparingDouble(Vehicle::getMaxSpeed));
    }

    // 5. Reporting (Updated for A2) [cite: 39]
    // Updated to include distinct models, fastest, and slowest vehicles.
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

        // --- NEW REPORTING SECTION FOR A2 ---
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
        // --- END OF NEW A2 SECTION ---

        return report.toString();
    }

    // --- PERSISTENCE METHODS (From A1, satisfy A2 requirements) [cite: 29, 38] ---

    public void saveToFile(String filename) throws IOException {
        // Using try-with-resources as required [cite: 44]
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
                    // Handles malformed files gracefully [cite: 30, 81]
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

    private String vehicleToCsv(Vehicle v) {
        String commonData = String.join(",",
            v.getClass().getSimpleName(),
            v.getId(),
            v.getModel(),
            String.valueOf(v.getMaxSpeed()),
            String.valueOf(v.getCurrentMileage())
        );

        if (v instanceof Car) {
            Car c = (Car) v;
            return commonData + "," + c.getFuelLevel() + "," + c.getCurrentPassengers();
        }
        if (v instanceof Truck) {
            Truck t = (Truck) v;
            return commonData + "," + t.getFuelLevel() + "," + t.getCurrentCargo();
        }
        if (v instanceof Bus) {
            Bus b = (Bus) v;
            return commonData + "," + b.getFuelLevel() + "," + b.getCurrentPassengers() + "," + b.getCurrentCargo();
        }
        if (v instanceof Airplane) {
            Airplane a = (Airplane) v;
            return commonData + "," + a.getMaxAltitude() + "," + a.getFuelLevel() + "," + a.getCurrentPassengers() + "," + a.getCurrentCargo();
        }
        if (v instanceof CargoShip) {
            CargoShip cs = (CargoShip) v;
            return commonData + "," + cs.hasSail() + "," + cs.getCurrentCargo();
        }
        return commonData;
    }

    private Vehicle createVehicleFromCsv(String line) throws Exception {
        String[] data = line.split(",");
        String type = data[0].trim();
        String id = data[1].trim();
        String model = data[2].trim();
        double maxSpeed = Double.parseDouble(data[3].trim());
        double mileage = Double.parseDouble(data[4].trim()); // <-- LOAD MILEAGE
    
        Vehicle v = null; // Create vehicle first
    
        switch (type) {
            case "Car":
                v = new Car(id, model, maxSpeed);
                // You would also load fuel, passengers, etc. here
                // Example: ((Car) v).refuel(Double.parseDouble(data[5].trim()));
                break;
            case "Truck":
                v = new Truck(id, model, maxSpeed);
                // Example: ((Truck) v).refuel(Double.parseDouble(data[5].trim()));
                break;
            case "Bus":
                v = new Bus(id, model, maxSpeed);
                break;
            case "Airplane":
                double maxAltitude = Double.parseDouble(data[5].trim());
                v = new Airplane(id, model, maxSpeed, maxAltitude);
                break;
            case "CargoShip":
                boolean hasSail = Boolean.parseBoolean(data[5].trim());
                v = new CargoShip(id, model, maxSpeed, hasSail);
                break;
            default:
                throw new IllegalArgumentException("Unknown vehicle type in CSV: " + type);
        }
        
        if (v != null) {
            v.setMileage(mileage); // <-- SET THE MILEAGE
        }
        
        return v;
    }
}