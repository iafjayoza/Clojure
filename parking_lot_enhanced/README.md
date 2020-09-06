parking_lot
=========
Parking lot that can hold up to 'n' cars at any given point in time.
Each slot is given a number starting at 1 increasing with increasing distance from the entry point in steps of one. 
It create an automated system that allows customers to use parking lot without human intervention.

* [Requirements] (parking_lot/ParkingLot-1.4.2.pdf)

# Functional Suite

`functional_spec/` contains clojure test cases for the different scenarios to verify the functionality of program.

## Usage

You can run the the test suits from
```
parking_lot $ bin/run_functional_specs
```

You can run the initial setup and test cases from
```
parking_lot $ bin/setup
```

You can run the program by 2 ways,
# 1.It provide us with an interactive command prompt based shell where commands can be typed in.
# 2.It takes an input file as an argument and prints the output.
```
parking_lot $ bin/parking_lot
```

## Build with

Maven

## Author

Jaykumar Oza