package com.bgpsimulator;


import com.bgpsimulator.network.NetworkTopology;
import com.bgpsimulator.packet.IPPacket;

import com.bgpsimulator.router.Router;


// Main.java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Create routers with names and IPs
        Router router1 = new Router("Router1", "1");
        Router router2 = new Router("Router2", "2");
        Router router3 = new Router("Router3", "3");
        Router router4 = new Router("Router4", "4");
        // Create the network topology and add routers
        NetworkTopology topology = new NetworkTopology();
        topology.addRouter(router1);
        topology.addRouter(router2);
        topology.addRouter(router3);
        topology.addRouter(router4);

        // Define connections (assumed bidirectional)
        topology.addConnection(router1, router2);
        topology.addConnection(router2, router3);
        topology.addConnection(router1, router4);
        // Discover neighbors and build routing tables
        router1.discoverNeighbors(topology);
        router2.discoverNeighbors(topology);
        router3.discoverNeighbors(topology);
        router4.discoverNeighbors(topology);
        
        router1.buildRoutingTable();
        router2.buildRoutingTable();
        router3.buildRoutingTable();
        router4.buildRoutingTable();

        // Create a Scanner object for user input
        Scanner scanner = new Scanner(System.in);
        
        // Loop to allow user to send messages
        while (true) {
            System.out.print("Enter destination IP address (or type 'exit' to quit): ");
            String destinationIp = scanner.nextLine();

            // Exit if the user types 'exit'
            if (destinationIp.equalsIgnoreCase("exit")) {
                System.out.println("Exiting...");
                break;
            }

            System.out.print("Enter your message: ");
            String message = scanner.nextLine();

            // Send the message from Router1 for demonstration purposes
            router3.sendMessage(destinationIp, message, topology);
        }

        // Close the scanner
        scanner.close();
    }
}
