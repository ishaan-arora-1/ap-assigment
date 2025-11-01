package fleet;
import exceptions.InvalidOperationException;
import vehicles.abstracts.Vehicle;
import vehicles.concrete.*;
import vehicles.interfaces.FuelConsumable;
import vehicles.interfaces.Maintainable;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FleetManager {
    private List<Vehicle> fleet;

    public FleetManager(){
        this.fleet = new ArrayList<>();
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
        boolean removed = fleet.removeIf(v->v.getId().equalsIgnoreCase(id));
        if(!removed){
            throw new InvalidOperationException("Vehicle with ID "+id+" not found.");
        }
        System.out.println("Vehicle "+id+" removed.");
    }

    public void startAllJourneys(double distance){
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
        for (Vehicle v : fleet){
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
        for(Vehicle v : fleet) {
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
            // We compare the simple class name (e.g., "Car")
            if (v.getClass().getSimpleName().equalsIgnoreCase(type)) {
                foundVehicles.add(v);
            }
        }
        return foundVehicles;
    }

    public void sortFleetByEfficiency() {
        Collections.sort(fleet);
    }

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

        report.append("\nSummary\n");
        report.append("Total Fleet Mileage: " + totalMileage + " km\n");
        report.append("Average Fuel Efficiency: " + averageEfficiency + " km/l\n");
        report.append("Vehicle Counts by Type:\n");
        for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
            report.append("  - ").append(entry.getKey()).append("s: ").append(entry.getValue()).append("\n");
        }

        return report.toString();
    }

    public void saveToFile(String filename) throws IOException {
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

        switch (type) {
            case "Car":
                return new Car(id, model, maxSpeed);
            case "Truck":
                return new Truck(id, model, maxSpeed);
            case "Bus":
                return new Bus(id, model, maxSpeed);
            case "Airplane":
                double maxAltitude = Double.parseDouble(data[5].trim());
                return new Airplane(id, model, maxSpeed, maxAltitude);
            case "CargoShip":
                boolean hasSail = Boolean.parseBoolean(data[5].trim());
                return new CargoShip(id, model, maxSpeed, hasSail);
            default:
                throw new IllegalArgumentException("Unknown vehicle type in CSV: " + type);
        }
    }
}