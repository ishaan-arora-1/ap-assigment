1. how to compile and run
compilation:

open a terminal in the project's root folder (where main.java and the fleet, vehicles, exceptions folders are).

type this command and press enter: javac $(find . -name "*.java")

running the application:

after it compiles, type this command and press enter: java -cp . main

2. justification for collections used 

i used a few different collection types for this assignment:

arraylist (in fleetmanager):

use: this is the main collection i used to store the list<vehicle> fleet.

justification: i chose an arraylist because it's a list that can grow and shrink as i add or remove vehicles. it's also very fast to loop over, which is perfect for generating reports or starting journeys for all vehicles.

hashset (in fleetmanager.getdistinctmodels):

use: i used this in the getdistinctmodels() helper method, which is called by the generatereport() method.


justification: i chose a hashset to find all the unique vehicle models. when i add a model name (like "toyota camry") to the set, it automatically handles duplicates. this gives me a clean list of distinct models for the report summary.

hashmap (in fleetmanager.generatereport):

use: i used this to count how many vehicles of each type i have (like "cars: 2", "trucks: 1").

justification: a hashmap is perfect for counting things. i used the vehicle type (a string) as the key and the count (an integer) as the value. this made it very simple to increment the count for each vehicle in the fleet.

3. sorting and ordering
i set up sorting in two different ways, as required by the assignment:




comparable (in vehicle.java):

the vehicle class itself implements comparable<vehicle>. this gives it a "natural" or default sort order.

i defined this default sort to be by fuel efficiency (highest first).

this is used in the sortfleetbyefficiency() method, which just calls collections.sort(fleet).

comparator (in fleetmanager.java):

for all other sorting options, i used comparators. these are like special, one-time rules for sorting.

the sortfleetbymaxspeed() and sortfleetbymodelname() methods use list.sort() along with a comparator.

these are connected to the "sort fleet" menu in the cli.

4. file i/o implementation 

saving: the savetofile method loops through the arraylist of vehicles. it calls a helper method (vehicletocsv) that turns all of a vehicle's data (type, id, model, mileage, fuel, passengers, cargo, maintenance status, etc.) into a single line of text, separated by commas. it then writes that line to the file.

loading: the loadfromfile method reads the file one line at a time. it splits the line by commas and uses the data to create a new vehicle object. it uses setters (like setmileage, setfuellevel, setcurrentpassengers) to restore the vehicle's full saved state.


error handling: i used try-with-resources to make sure files are closed automatically, as suggested . if the user tries to load a file that doesn't exist, it prints a 'file not found' error . if a line inside the file is broken or has bad data, the code prints a warning, skips that line, and continues loading the rest of the file without crashing.



5. sample run / demo features 

when you run java -cp . main, the program will first run an automatic demo:

it adds four vehicles to the fleet (including two "toyota camry" cars to test duplicate models).

it displays an initial report. this report will correctly show "total vehicles: 4" but "distinct vehicle models: 3".

it refuels them all and sends them on a 100 km journey.

it displays a final report, showing that all 4 vehicles now have 100.0 km mileage. it also shows the fastest vehicle (a001) and slowest (t001).

it saves this demo fleet to demo_fleet.csv.

after the demo, the interactive cli menu appears, which now includes a "10. sort fleet" option.



sample cli interaction (sorting):

fleet management system menu
...
9. search by type
10. sort fleet
11. list vehicles needing maintenance
12. exit
choose an option: **10**

--- sort fleet by ---
1. fuel efficiency (highest first)
2. max speed (fastest first)
3. model name (a-z)
4. cancel
choose sorting option: **2**

fleet sorted by max speed (fastest first).

fleet status report
total vehicles: 4

  - id: a001, type: airplane, model: boeing 747, mileage: 100.0 km, needs maintenance: no
  - id: c001, type: car, model: toyota camry, mileage: 100.0 km, needs maintenance: no
  - id: c002, type: car, model: toyota camry, mileage: 100.0 km, needs maintenance: no
  - id: t001, type: truck, model: volvo fh16, mileage: 100.0 km, needs maintenance: no

summary
...
fastest vehicle: a001 (boeing 747) at 900.0 km/h
slowest vehicle: t001 (volvo fh16) at 140.0 km/h
...