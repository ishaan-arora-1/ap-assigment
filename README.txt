my program uses these important java ideas:

inheritance: i created a main "vehicle" class. then i made specific types like "car", "truck", or "airplane" that learn from "vehicle". this means they all share common things like an id and a model, but each also has its own special features. so, a car gets basic vehicle stuff and adds its car-specific parts.

polymorphism: this means i can treat all my different vehicles (cars, trucks, airplanes) as just "vehicles" when i do general things like starting a journey or generating a report. the program then automatically knows how each specific vehicle should move or report its details. it's like telling everyone to "move," and each one moves in its own way (driving, flying).

abstract classes: "vehicle," "landvehicle," "airvehicle," and "watervehicle" are abstract classes. this means they are blueprints for other vehicles, but i cannot create a direct "vehicle" object. they define common behaviors that all their children must have, like how to move or calculate fuel efficiency.

interfaces: these are like contracts. i made interfaces like "fuelconsumable" (for vehicles that use fuel) or "cargocarrier" (for vehicles that carry cargo). if a class (like "truck") uses this contract by "implementing" an interface, it promises to have all the methods listed in that contract. this helps me group vehicles by what they can do, not just what they are.

how to get it running

open your terminal: go to the folder named "fleetmanagementsystem" where all the code files are.

compile the code: type this command and press enter:
javac $(find . -name "*.java")
this command collects all the java files and turns them into something your computer can run. if there are no error messages, it worked.

run the program: type this command and press enter:
java -cp . Main
this starts the application.

how to use the program

when you run the program, it does a quick demo first, then shows you a menu:

demo features:

the program automatically adds a car, truck, and airplane.

it shows an initial report.

it refuels all of them with 1000 liters.

it makes them all go on a 100 km journey.

it shows another report after the journey.

it saves this demo fleet to a file called "demo_fleet.csv".

command line interface (cli) menu:
after the demo, you'll see a list of numbers. just type the number for what you want to do and press enter.

1. add vehicle: lets you add new cars, trucks, airplanes, etc. you'll type in details like its id, model, and speed.

2. remove vehicle: asks for a vehicle's id to take it out of the fleet.

3. start journey for all vehicles: asks for a distance, and all vehicles will try to travel that far.

4. refuel all vehicles: asks for an amount, and all vehicles that use fuel will get that much fuel.

5. perform maintenance on all: checks if any vehicle needs maintenance (based on mileage) and performs it if needed.

6. generate fleet report: prints a summary of all vehicles, their mileage, efficiency, and maintenance status.

7. save fleet to file: asks for a filename (like "my_fleet.csv") and saves the current fleet to that file.

8. load fleet from file: asks for a filename and loads a fleet from it, replacing the current one. you can load "demo_fleet.csv" to see the demo vehicles again.

9. list vehicles needing maintenance: shows you just the vehicles that currently need a check-up.

10. exit: closes the program.

running the demo walkthrough and what to expect

when you first run java -cp . Main, you'll see this:

it will say "running initial demo".

then you'll see messages about "creating and adding vehicles..."

an "initial fleet report" will print, showing 3 vehicles (a car, truck, and airplane), all with 0 km mileage.

it will say "refueling all vehicles..."

it will then say "simulating a 100km journey..." and you'll see messages like "car c001 is driving..."

a "fleet report after journey" will print. this report will show all 3 vehicles now have 100.0 km on their odometers.

finally, it will say "saving fleet to 'demo_fleet.csv'..."

after the demo, you'll see the "fleet management system menu" appear, ready for you to use.