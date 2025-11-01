Fleet Management System - Assignment 2 Readme

This project extends the Assignment 1 fleet management system to demonstrate
the use of the Java Collections Framework and new sorting capabilities.

---------------------------------
1. How to Compile and Run
---------------------------------

**Prerequisites:**
- Java Development Kit (JDK) 8 or higher.
- `javac` and `java` commands available in your system's PATH.

**Compilation:**
1. Open a terminal or command prompt.
2. Navigate to the root directory of the project (where `Main.java` and the `fleet`, `vehicles`, `exceptions` folders are located).
3. Compile all `.java` files using this command:
   javac $(find . -name "*.java")

**Running the Application:**
1. After successful compilation, run the `Main` class with this command:
   java -cp . Main

---------------------------------
2. Justification for Collections Used [cite: 53]
---------------------------------

This program uses several collection types as required by the assignment:

* **`ArrayList` (in `FleetManager`)**:
    * **Use:** This is the primary collection used to store the main `List<Vehicle> fleet`.
    * **Justification:** An `ArrayList` was chosen because it provides a dynamic, resizable array[cite: 24]. This is ideal for a fleet where vehicles are frequently added or removed, and it offers fast iteration, which is perfect for generating reports or starting journeys for all vehicles.

* **`HashSet` (in `FleetManager.getDistinctModels`)**:
    * **Use:** This is demonstrated in the `getDistinctModels()` method, which is called by `generateReport()`.
    * **Justification:** A `HashSet` was chosen to efficiently find all unique vehicle models in the fleet[cite: 25, 36]. As models are added to the set, it automatically handles duplicates, ensuring each model name is stored only once. This is far more efficient than manually checking for duplicates in a list.

* **`HashMap` (in `FleetManager.generateReport`)**:
    * **Use:** This is used to count the number of vehicles of each type for the report.
    * **Justification:** A `HashMap` is the ideal tool for counting occurrences. The vehicle type (e.g., "Car") is used as the key, and the count is stored as the value, making it simple to increment the count for each vehicle processed.

---------------------------------
3. Sorting and Ordering [cite: 26, 37, 75]
---------------------------------

Sorting is implemented in two ways:

* **`Comparable` (in `Vehicle.java`)**:
    * The `Vehicle` class implements `Comparable<Vehicle>`, providing a "natural" sort order.
    * This is used by `Collections.sort(fleet)` in the `sortFleetByEfficiency()` method to sort vehicles from highest to lowest efficiency.

* **`Comparator` (in `FleetManager.java`)**:
    * `Comparator`s are used to provide *alternative* sorting orders.
    * In the `sortFleetByMaxSpeed()` and `sortFleetByModelName()` methods, modern lambda expressions are used (`Comparator.comparing(...)`) to sort the fleet on the fly based on user selection from the new "Sort Fleet" menu.

---------------------------------
4. File I/O Implementation [cite: 29, 38, 54]
---------------------------------

* **Saving:** The `saveToFile` method iterates through the `ArrayList` of vehicles, converts each `Vehicle` object into a detailed CSV string (using the `vehicleToCsv` helper), and writes each string as a new line.
* **Loading:** The `loadFromFile` method reads the specified file line by line. It uses a `try-catch` block *inside* the loop to ensure that a single malformed line (as mentioned in [cite: 30]) does not crash the entire program. It skips the bad line and continues loading.
* **Error Handling:** Both methods use `try-with-resources` to ensure files are closed automatically [cite: 44] and catch `FileNotFoundException` and `IOException` to provide user-friendly error messages[cite: 30, 81].

---------------------------------
5. Sample Run / Demo Features [cite: 55]
---------------------------------

When you run `java -cp . Main`, the program will first execute an automated demo:

1.  It adds four vehicles to the fleet (including two with the same model, "Toyota Camry").
2.  It displays an initial report. This report will correctly show "Total Vehicles: 4" but "Distinct Vehicle Models: 3".
3.  It refuels and simulates a 100 km journey.
4.  It displays a final report showing updated mileage and statistics, including the fastest vehicle (A001) and slowest (T001).
5.  It saves the demo fleet to `demo_fleet.csv`.
6.  After the demo, the interactive CLI menu appears, which now includes a "10. Sort Fleet" option.

**Sample CLI Interaction (Sorting):**

    Fleet Management System Menu
    ...
    9. Search by Type
    10. Sort Fleet
    11. List Vehicles Needing Maintenance
    12. Exit
    Choose an option: **10**

    --- Sort Fleet By ---
    1. Fuel Efficiency (Highest First)
    2. Max Speed (Fastest First)
    3. Model Name (A-Z)
    4. Cancel
    Choose sorting option: **2**

    Fleet sorted by max speed (fastest first).

    Fleet Status Report
    Total Vehicles: 4
    
      - ID: A001, Type: Airplane, Model: Boeing 747, Mileage: 100.0 km, Needs Maintenance: No
      - ID: C001, Type: Car, Model: Toyota Camry, Mileage: 100.0 km, Needs Maintenance: No
      - ID: C002, Type: Car, Model: Toyota Camry, Mileage: 100.0 km, Needs Maintenance: No
      - ID: T001, Type: Truck, Model: Volvo FH16, Mileage: 100.0 km, Needs Maintenance: No
    
    Summary
    ...
    Fastest Vehicle: A001 (Boeing 747) at 900.0 km/h
    Slowest Vehicle: T001 (Volvo FH16) at 140.0 km/h
    ...