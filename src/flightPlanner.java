import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

// Class to represent a flight
class Flight {
    String destination;
    double cost;
    int time;

    // Constructor
    Flight(String dest, double c, int t) {
        destination = dest;
        cost = c;
        time = t;
    }
}

// Class to represent a city
class City {
    String name;
    LinkedList <Flight> flights;

    // Constructor
    City(String n) {
        name = n;
        flights = new LinkedList<>();
    }

    // Method to add a flight from this city
    void addFlight(String dest, double cost, int time) {
        flights.add(new Flight(dest, cost, time));
    }
}

// Linked List Node class
class ListNode<T> {
    T data;
    ListNode<T> next;

    // Constructor
    ListNode(T data) {
        this.data = data;
        this.next = null;
    }
}

// Linked List class
class LinkedList<T> implements Iterable<T> {
    private ListNode<T> head;

    // Constructor
    LinkedList() {
        head = null;
    }

    // Method to add data to the linked list
    void add(T data) {
        ListNode<T> newNode = new ListNode<>(data);
        if (head == null) {
            head = newNode;
        } else {
            ListNode<T> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
    }

    // Method to check if the linked list is empty
    boolean isEmpty() {
        return head == null;
    }

    // Method to get the data at the head of the linked list
    T getFirst() {
        if (head != null) {
            return head.data;
        }
        return null;
    }

    // Method to remove and return the data at the head of the linked list
    T removeFirst() {
        if (head != null) {
            T data = head.data;
            head = head.next;
            return data;
        }
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListIterator();
    }

    // Iterator for the linked list
    private class LinkedListIterator implements Iterator<T> {
        private ListNode<T> current = head;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T data = current.data;
            current = current.next;
            return data;
        }
    }
}
// Stack class using linked list
class Stack<T> {
    private LinkedList<T> list;

    // Constructor
    Stack() {
        list = new LinkedList<>();
    }

    // Method to push data onto the stack
    void push(T data) {
        list.add(data);
    }

    // Method to pop data from the stack
    T pop() {
        return list.removeFirst();
    }

    // Method to check if the stack is empty
    boolean isEmpty() {
        return list.isEmpty();
    }
}

// Main class for flight planner
public class flightPlanner {
    private LinkedList  <City> cities;

    // Constructor
    public flightPlanner() {
        cities = new LinkedList<>();
    }

    // Method to add a city
    public void addCity(String name) {
        // Check if the city already exists before adding
        if (findCity(name) == null) {
            cities.add(new City(name));
        }
    }

    // Method to add a flight between two cities
    public void addFlight(String from, String to, double cost, int time) {
        City fromCity = findCity(from);
        City toCity = findCity(to);
        if (fromCity != null && toCity != null) {
            fromCity.addFlight(to, cost, time);
            // Add a flight in the reverse direction
            toCity.addFlight(from, cost, time);
        }
    }

    // Method to find a city by name
    private City findCity(String name) {
        for (City city : cities) {
            if (city.name.equals(name)) {
                return city;
            }
        }
        return null;
    }

    // Method to find flight paths between two cities using iterative backtracking algorithm
    public List<String> findPaths(String from, String to, char sortingPreference) {
        List<String> paths = new ArrayList<>();
        Stack<List<Object>> stack = new Stack<>(); // Stack to track progress

        // Push initial state onto the stack
        List<Object> initialState = new ArrayList<>();
        initialState.add(from);
        initialState.add("");
        initialState.add(0.0);
        initialState.add(0);
        stack.push(initialState);

        while (!stack.isEmpty()) {
            List<Object> currentState = stack.pop();
            String currentCityName = (String) currentState.get(0);
            String currentPath = (String) currentState.get(1);
            double currentCost = (double) currentState.get(2);
            int currentTime = (int) currentState.get(3);

            // Check if the current city is the destination
            if (currentCityName.equals(to)) {
                paths.add(currentPath + ". Time: " + currentTime + " Cost: " + String.format("%.2f", currentCost));
                continue;
            }

            City currentCity = findCity(currentCityName);
            if (currentCity == null) {
                continue;
            }

            for (Flight flight : currentCity.flights) {
                // Check if destination city has already been visited in this path
                if (!currentPath.contains(flight.destination)) {
                    double newCost = currentCost + flight.cost;
                    int newTime = currentTime + flight.time;
                    String newPath = currentPath.isEmpty() ? currentCityName + " -> " + flight.destination : currentPath + " -> " + flight.destination;
                    stack.push(List.of(flight.destination, newPath, newCost, newTime));
                }
            }
        }

        return paths;
    }

    // Main method
    public static void main(String[] args) {
        // Create a new flight planner instance
        flightPlanner planner = new flightPlanner();

        // Sample flight data input
        try {
            System.out.println("Enter flight data: ");
            Scanner scnr = new Scanner(System.in);
            String fileData = scnr.next();
            FileInputStream dataInputStream = new FileInputStream(fileData);
            Scanner datInFs = new Scanner(dataInputStream);
            
            // Read the number of flights
            int numFlights  = Integer.parseInt(datInFs.nextLine());
            // Read flight information and add cities and flights
            for(int i = 0; i < numFlights; i++) {
                String[] flightInfo = datInFs.nextLine().split("\\|");
                planner.addCity(flightInfo[0]);
                planner.addCity(flightInfo[1]);
                planner.addFlight(flightInfo[0], flightInfo[1], Double.parseDouble(flightInfo[2]), Integer.parseInt(flightInfo[3]));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Requested flight plans input
        try {
            System.out.println("Enter requested file: ");
            Scanner scnr = new Scanner(System.in);
            String fileRequested = scnr.next();
            FileInputStream reqInputStream = new FileInputStream(fileRequested);
            Scanner reqInFs = new Scanner(reqInputStream);

            // Read the number of flight requests
            int numRequests = reqInFs.nextInt();
            reqInFs.nextLine(); // Consume newline

            // Process each flight request
            for (int i = 0; i < numRequests; i++) {
                String[] request = reqInFs.nextLine().split("\\|");
                String from = request[0];
                String to = request[1];
                char sortingPreference = request[2].charAt(0);

                // Find flight paths and print the results
                List<String> paths = planner.findPaths(from, to, sortingPreference);
                if (paths.isEmpty()) {
                    System.out.println("No viable flight plan from " + from + " to " + to);
                } else {
                    System.out.println("Flight " + (i + 1) + ": " + from + ", " + to + " (" + (sortingPreference == 'T' ? "Time" : "Cost") + ")");
                    for (int j = 0; j < Math.min(3, paths.size()); j++) {
                        System.out.println("Path " + (j + 1) + ": " + paths.get(j));
                    }
                }
            }

            reqInFs.close();
            scnr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}