package com.bgpsimulator.router;

import java.io.*;
import java.util.*;
import com.bgpsimulator.network.NetworkTopology;
import com.bgpsimulator.packet.IPPacket;
import java.net.*;

public class Router {
    private String name;
    private String ipAddress;
    private Map<Integer, Router> routingTable = new HashMap<>();  // Maps destination IP to Router object
    private List<Router> neighbors = new ArrayList<>();
    private boolean coldStart = true;  // Indicates if the router is in the cold start phase
    private ServerSocket serverSocket;
    private int port;

    public Router(String name, String ipAddress, int port) throws IOException {
        this.name = name;
        this.ipAddress = ipAddress;
        this.port = port;
        this.serverSocket = new ServerSocket(port);
    }

    public String getName() {
        return name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() { return port; }

    public void start() {
        new Thread(() -> {
            try {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new PacketHandler(clientSocket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    /**
     * Discover neighbors for the router.
     * During the cold start phase, the router will discover its direct neighbors
     * from the network topology and will output the names of these neighbors.
     * This method sets the coldStart flag to false after discovery.
     *
     * The discovery of neighbors is critical for building an initial routing table,
     * as it enables the router to establish direct communication links.
     */
    public void discoverNeighbors(NetworkTopology topology) {
        if (coldStart) {
            neighbors = topology.getNeighbors(this);  // Get list of neighboring routers
            System.out.println(name + " discovered neighbors: " + getNeighborNames());
            coldStart = false;  // Mark the router as having completed neighbor discovery
        }
    }

    /**
     * Build the initial routing table based on discovered neighbors.
     * This method maps the IP addresses of direct neighbors to their respective Router objects.
     * It also propagates routing information to its neighbors, allowing them to learn about
     * routes that this router knows.
     */
    public synchronized void buildRoutingTable() {
        // Add direct neighbors to the routing table
        for (Router neighbor : neighbors) {
            routingTable.put(neighbor.getPort(), neighbor);  // Route to neighbor's IP
        }

        System.out.println(name + " built initial routing table: " + routingTable);
        
        // Propagate routing information to neighbors
        for (Router neighbor : neighbors) {
            neighbor.updateRoutingTable(new HashMap<>(this.routingTable), this); // Propagate routes to neighbors
        }
    }

    /**
     * Update the routing table with routes learned from neighboring routers.
     * This method allows a router to incorporate new routes based on the routing
     * information shared by its neighbors.
     *
     * If the router does not have a route to a specific destination, it learns
     * the route through its neighbor.
     *
     * @param neighborRoutingTable A mapping of destination IPs to Router objects from the neighbor.
     * @param nextHop The Router object that provided the routing information.
     */
    public void updateRoutingTable(Map<Integer, Router> neighborRoutingTable, Router nextHop) {
        for (Map.Entry<Integer, Router> entry : neighborRoutingTable.entrySet()) {
            int destinationPort = entry.getKey();
            Router neighborRouter = entry.getValue();

            // If we don't have a route to the destination, learn it from the neighbor
            if (!routingTable.containsKey(destinationPort)) {
                routingTable.put(destinationPort, nextHop); // Route via the neighbor
            }
        }
        
        // Print updated routing table
        System.out.println(name + " updated routing table: ");
    }

    /**
     * Lookup the next hop router for a given destination IP address.
     *
     * @param destinationPort The IP address of the destination.
     * @return The Router object corresponding to the next hop, or null if no route is found.
     */
    public Router lookupNextHop(int destinationPort) {
        return routingTable.getOrDefault(destinationPort, null);  // Return next hop Router or null
    }

    /**
     * Process an incoming packet.
     * This method checks if the current router is the destination for the packet.
     * If not, it looks up the next hop router based on the destination IP and forwards the packet.
     *
     * @param packet The IP packet to be processed.
     */
    public void processPacket(IPPacket packet) {
        int destinationPort = packet.getDestinationPort();  // Assuming destination IP is now a port
        System.out.println("Router " + name + " is processing packet from " + packet.getSourceIP() + " to port " + destinationPort);

        if (port == destinationPort) {
            System.out.println("Packet arrived at destination: " + name + " with message: " + packet.getMessage());
        } else {
            Router nextHopRouter = lookupNextHop(destinationPort);

            if (nextHopRouter != null) {
                System.out.println("Next hop for destination port " + destinationPort + " is: " + nextHopRouter.getName());
                sendPacketToNextHop(packet, nextHopRouter);
            } else {
                System.out.println("No route found to destination port: " + destinationPort);
            }
        }
    }

    /**
     * Create a packet to send a message to a specific destination IP.
     * 
     * @param destinationPort The port of the destination router.
     * @param message The message to be sent in the packet.
     */
    public void sendMessage(int destinationPort, String message) {
        IPPacket packet = new IPPacket(this.ipAddress, destinationPort, message);
        processPacket(packet);
    }

    private void sendPacketToNextHop(IPPacket packet, Router nextHopRouter) {
        try (Socket socket = new Socket(nextHopRouter.getIpAddress(), nextHopRouter.getPort());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.writeObject(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getNeighborNames() {
        StringBuilder sb = new StringBuilder();
        for (Router neighbor : neighbors) {
            sb.append(neighbor.getName()).append(" ");
        }
        return sb.toString();
    }

    // For testing purposes, allow external access to the routing table
    public Map<Integer, Router> getRoutingTable() {
        return routingTable;
    }

    private class PacketHandler implements Runnable {
        private Socket clientSocket;

        public PacketHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
                IPPacket packet = (IPPacket) in.readObject();
                processPacket(packet);  // Assuming topology is accessible or passed in some way
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}


