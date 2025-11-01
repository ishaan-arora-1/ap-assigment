import fleet.FleetManager;
import exceptions.InvalidOperationException;
import exceptions.OverloadException;
import vehicles.abstracts.Vehicle;
import vehicles.concrete.*;
import vehicles.interfaces.PassengerCarrier;
import vehicles.interfaces.CargoCarrier;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static FleetManager fleetManager = new FleetManager();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Running Initial Demo (Assignment 2 Version)");
        runDemo();
        
        System.out.println("\nLaunching Command-Line Interface");
        runCLI();
        
        scanner.close();
        System.out.println("Exiting application. Goodbye!");
    }

    private static void runDemo() {
        try {
            System.out.println("\nCreating and adding vehicles to the fleet...");
            fleetManager.addVehicle(new Car("C001", "Toyota Camry", 220));
            fleetManager.addVehicle(new Truck("T001", "Volvo FH16", 140));
            fleetManager.addVehicle(new Airplane("A001", "Boeing 747", 900, 41000));
            // ive added a duplicate model to test distinct model reporting
            fleetManager.addVehicle(new Car("C002", "Toyota Camry", 220));
            
            System.out.println("\nInitial fleet report:");
            System.out.println(fleetManager.generateReport());

            System.out.println("\nRefueling all vehicles with 1000 liters...");
            fleetManager.refuelAll(1000);

            System.out.println("\nSimulating a 100km journey...");
            fleetManager.startAllJourneys(100);

            System.out.println("\nFleet report after journey:");
            System.out.println(fleetManager.generateReport());

            System.out.println("\nSaving fleet to 'demo_fleet.csv'...");
            fleetManager.saveToFile("demo_fleet.csv");

        } catch (Exception e) {
            System.err.println("An error occurred during the demo: " + e.getMessage());
        }
    }

    private static void runCLI() {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1: addVehicle(); break;
                case 2: removeVehicle(); break;
                case 3: startJourney(); break;
                case 4: refuelAll(); break;
                case 5: fleetManager.maintainAll(); break;
                case 6: System.out.println(fleetManager.generateReport()); break;
                case 7: saveFleet(); break;
                case 8: loadFleet(); break;
                case 9: searchByType(); break;
                case 10: sortFleetSubMenu(); break; 
                case 11: listMaintenanceNeeds(); break; 
                case 12: running = false; break; 
                default: System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    // Updated menu for A2
    private static void printMenu() {
        System.out.println("\nFleet Management System Menu");
        System.out.println("1. Add Vehicle");
        System.out.println("2. Remove Vehicle");
        System.out.println("3. Start Journey for All Vehicles");
        System.out.println("4. Refuel All Vehicles");
        System.out.println("5. Perform Maintenance on All");
        System.out.println("6. Generate Fleet Report");
        System.out.println("7. Save Fleet to File");
        System.out.println("8. Load Fleet from File");
        System.out.println("9. Search by Type");
        System.out.println("10. Sort Fleet"); // NEW for A2
        System.out.println("11. List Vehicles Needing Maintenance"); // Re-numbered
        System.out.println("12. Exit"); // Re-numbered
        System.out.print("Choose an option: ");
    }
    
    // New sub-menu method for A2 
    private static void sortFleetSubMenu() {
        System.out.println("\n--- Sort Fleet By ---");
        System.out.println("1. Fuel Efficiency (Highest First)");
        System.out.println("2. Max Speed (Fastest First)");
        System.out.println("3. Model Name (A-Z)");
        System.out.println("4. Cancel");
        System.out.print("Choose sorting option: ");
        
        int choice = getUserChoice();
        switch (choice) {
            case 1:
                fleetManager.sortFleetByEfficiency();
                break;
            case 2:
                fleetManager.sortFleetByMaxSpeed();
                break;
            case 3:
                fleetManager.sortFleetByModelName();
                break;
            case 4:
                System.out.println("Sorting cancelled.");
                break;
            default:
                System.out.println("Invalid sort option.");
        }
        // Display the report after sorting to show the result
        if (choice >= 1 && choice <= 3) {
            System.out.println(fleetManager.generateReport());
        }
    }
    
    private static int getUserChoice() {
        while (true) {
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                return choice;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
                System.out.print("Choose an option: ");
            }
        }
    }
    
    private static void addVehicle() {
        try {
            System.out.print("Enter vehicle type (Car, Truck, Bus, Airplane, CargoShip): ");
            String type = scanner.nextLine();
            
            System.out.print("Enter ID: ");
            String id = scanner.nextLine();
            
            System.out.print("Enter Model: ");
            String model = scanner.nextLine();
            
            System.out.print("Enter Max Speed (km/h): ");
            double maxSpeed = scanner.nextDouble();
            scanner.nextLine(); // consume newline
            
            Vehicle v = null;
            
            switch(type.toLowerCase()) {
                case "car":
                    v = new Car(id, model, maxSpeed);
                    break;
                case "truck":
                    v = new Truck(id, model, maxSpeed);
                    break;
                case "bus":
                    v = new Bus(id, model, maxSpeed);
                    break;
                case "airplane":
                    System.out.print("Enter Max Altitude (ft): ");
                    double maxAlt = scanner.nextDouble();
                    scanner.nextLine();
                    v = new Airplane(id, model, maxSpeed, maxAlt);
                    break;
                case "cargoship":
                    System.out.print("Does it have a sail? (true/false): ");
                    boolean hasSail = scanner.nextBoolean();
                    scanner.nextLine();
                    v = new CargoShip(id, model, maxSpeed, hasSail);
                    break;
                default:
                    System.out.println("Invalid vehicle type.");
                    return;
            }
            if (v instanceof PassengerCarrier) {
                System.out.print("Enter initial passengers: ");
                int passengers = scanner.nextInt();
                scanner.nextLine();
                ((PassengerCarrier) v).setCurrentPassengers(passengers);
            }
            
            if (v instanceof CargoCarrier) {
                System.out.print("Enter initial cargo (kg): ");
                double cargo = scanner.nextDouble();
                scanner.nextLine();
                ((CargoCarrier) v).setCurrentCargo(cargo);
            }

            fleetManager.addVehicle(v);

        } catch (InputMismatchException e) {
            System.out.println("Invalid numeric input.");
            scanner.nextLine();
        } catch (Exception e) { // Catches OverloadException and InvalidOperationException
            System.out.println("Error adding vehicle: " + e.getMessage());
        }
    }

    private static void removeVehicle() {
        System.out.print("Enter ID of vehicle to remove: ");
        String id = scanner.nextLine();
        try {
            fleetManager.removeVehicle(id);
        } catch (InvalidOperationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void startJourney() {
        try {
            System.out.print("Enter distance for the journey (km): ");
            double distance = scanner.nextDouble();
            scanner.nextLine();
            fleetManager.startAllJourneys(distance);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number for distance.");
            scanner.nextLine();
        }
    }

    private static void refuelAll() {
        try {
            System.out.print("Enter amount to refuel (liters): ");
            double amount = scanner.nextDouble();
            scanner.nextLine();
            fleetManager.refuelAll(amount);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number for amount.");
            scanner.nextLine();
        }
    }

    private static void saveFleet() {
        System.out.print("Enter filename to save (e.g., fleetdata.csv): ");
        String filename = scanner.nextLine();
        try {
            fleetManager.saveToFile(filename);
        } catch (IOException e) {
            System.out.println("Could not save file: " + e.getMessage());
        }
    }

    private static void loadFleet() {
        System.out.print("Enter filename to load (e.g., fleetdata.csv): ");
        String filename = scanner.nextLine();
        try {
            fleetManager.loadFromFile(filename);
        } catch (IOException e) {
            System.out.println("Could not load file: " + e.getMessage());
        }
    }
    
    private static void searchByType() {
        System.out.print("Enter vehicle type to search for (e.g., Car, Truck): ");
        String type = scanner.nextLine();
        List<Vehicle> results = fleetManager.searchByType(type);

        if (results.isEmpty()) {
            System.out.println("No vehicles of type '" + type + "' found.");
        } else {
            System.out.println("\nFound " + results.size() + " vehicle(s) of type '" + type + "':");
            for (Vehicle v : results) {
                v.displayInfo();
            }
        }
    }
    
    private static void listMaintenanceNeeds() {
        List<Vehicle> needsMaintenance = fleetManager.getVehiclesNeedingMaintenance();
        if (needsMaintenance.isEmpty()) {
            System.out.println("No vehicles currently need maintenance.");
        } else {
            System.out.println("\nVehicles Needing Maintenance");
            for (Vehicle v : needsMaintenance) {
                System.out.println("ID: " + v.getId() + ", Type: " + v.getClass().getSimpleName() + ", Mileage: " + v.getCurrentMileage() + " km");
            }
        }
    }
}