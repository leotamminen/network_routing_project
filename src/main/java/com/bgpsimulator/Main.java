package com.bgpsimulator;


import com.bgpsimulator.network.NetworkTopology;
import com.bgpsimulator.packet.IPPacket;

import com.bgpsimulator.router.Router;
// Main.java
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        {
            List<Router> routers = new ArrayList<>();
            int numberOfRouters = 4;

            // Create and start routers in a loop
            for (int i = 1; i <= numberOfRouters; i++) {
                String name = "Router" + i;
                String ipAddress = ("127.0.0.1");
                int port = 5000 + i;

                Router router = new Router(name, ipAddress, port);
                routers.add(router);
            }

            NetworkTopology topology = new NetworkTopology();
            for (Router router : routers) {
                topology.addRouter(router);
            }

            // Define connections (assumed bidirectional)
            topology.addConnection(routers.get(0), routers.get(1));
            topology.addConnection(routers.get(1), routers.get(2));
            topology.addConnection(routers.get(0), routers.get(3));

            // Discover neighbors and build routing tables
            for (Router router : routers) {
                router.discoverNeighbors(topology);
            }

            for (Router router : routers) {
                router.buildRoutingTable();
            }

            // Start the routers after building the routing tables
            for (Router router : routers) {
                router.start();
                System.out.println(router.getName() + " started on port: " + router.getPort() + " with IP address: "+ router.getIpAddress());
            }

            // Create a Scanner object for user input
            Scanner scanner = new Scanner(System.in);

            // Loop to allow user to send messages
            while (true) {
                System.out.print("Enter destination port (or type 'exit' to quit): ");
                String input = scanner.nextLine();

                // Exit if the user types 'exit'
                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting...");
                    break;
                }

                int destinationPort = Integer.parseInt(input);

                System.out.print("Enter your message: ");
                String message = scanner.nextLine();

                // Send the message from Router1 for demonstration purposes
                routers.get(2).sendMessage(destinationPort, message);
            }

            // Close the scanner
            scanner.close();
        }
    }
}
