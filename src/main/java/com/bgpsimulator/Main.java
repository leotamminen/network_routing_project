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

            // Create a Scanner object for user input
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter the number of routers: ");
            int numberOfRouters = scanner.nextInt();

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

            // Define customizable connections
            scanner.nextLine(); // Consume newline
            for (Router router : routers) {
                System.out.print("Enter neighbors for " + router.getName() + " (comma separated, e.g., 2,3): ");
                String[] neighborInputs = scanner.nextLine().split(",");
                for (String neighborIndex : neighborInputs) {
                    try {
                        int index = Integer.parseInt(neighborIndex.trim()) - 1;
                        if (index >= 0 && index < routers.size() && index != routers.indexOf(router)) {
                            topology.addConnection(router, routers.get(index));
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Skipping neighbor.");
                    }
                }
            }

            // Discover neighbors and build routing tables using shortest path computation
            for (Router router : routers) {
                router.discoverNeighbors(topology);
            }

            for (Router router : routers) {
                router.buildRoutingTable();
            }

            // Start the routers after building the routing tables
            for (Router router : routers) {
                router.start();
                System.out.println(router.getName() + " started on port: " + router.getPort() + " with IP address: " + router.getIpAddress());
            }

            // Select the router from which messages will be sent
            System.out.print("Enter the router number to send messages from: ");
            int senderIndex = scanner.nextInt() - 1;
            while (senderIndex < 0 || senderIndex >= routers.size()) {
                System.out.print("Invalid choice. Enter a valid router number: ");
                senderIndex = scanner.nextInt() - 1;
            }
            Router senderRouter = routers.get(senderIndex);

            // Loop to allow user to send messages
            while (true) {
                System.out.print("Enter destination router number (or type 'exit' to quit): ");
                String input = scanner.next();

                // Exit if the user types 'exit'
                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting...");
                    break;
                }

                int destinationRouterIndex = Integer.parseInt(input) - 1;
                if (destinationRouterIndex < 0 || destinationRouterIndex >= routers.size()) {
                    System.out.println("Invalid router number. Try again.");
                    continue;
                }
                int destinationPort = routers.get(destinationRouterIndex).getPort();

                System.out.print("Enter your message: ");
                scanner.nextLine(); // Consume newline
                String message = scanner.nextLine();

                // Ensure a valid route exists before sending
                List<Router> path = topology.computeShortestPath(senderRouter, routers.get(destinationRouterIndex));
                if (!path.isEmpty()) {
                    System.out.println("Routing packet via: " + path);
                    senderRouter.forwardPacket(path, message);
                } else {
                    System.out.println("No route found to destination router.");
                }
            }

            // Close the scanner
            scanner.close();
        }
    }
}
