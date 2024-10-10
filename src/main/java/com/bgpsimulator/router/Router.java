package com.bgpsimulator.router;


import java.util.*;

import com.bgpsimulator.network.NetworkTopology;
import com.bgpsimulator.packet.IPPacket;

import java.util.*;

public class Router {
    private String name;
    private String ipAddress;
    private Map<String, Router> routingTable = new HashMap<>();  // Maps destination IP to Router object
    private List<Router> neighbors = new ArrayList<>();
    private boolean coldStart = true;  // Indicates if the router is in the cold start phase

    public Router(String name, String ipAddress) {
        this.name = name;
        this.ipAddress = ipAddress;
    }

    public String getName() {
        return name;
    }

    public String getIpAddress() {
        return ipAddress;
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
    public void buildRoutingTable() {
        // Add direct neighbors to the routing table
        for (Router neighbor : neighbors) {
            routingTable.put(neighbor.getIpAddress(), neighbor);  // Route to neighbor's IP
        }

        System.out.println(name + " built initial routing table: " + routingTable);
        
        // Propagate routing information to neighbors
        for (Router neighbor : neighbors) {
            neighbor.updateRoutingTable(this.routingTable, this); // Propagate routes to neighbors
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
    public void updateRoutingTable(Map<String, Router> neighborRoutingTable, Router nextHop) {
        for (Map.Entry<String, Router> entry : neighborRoutingTable.entrySet()) {
            String destinationIP = entry.getKey();
            Router neighborRouter = entry.getValue();

            // If we don't have a route to the destination, learn it from the neighbor
            if (!routingTable.containsKey(destinationIP)) {
                routingTable.put(destinationIP, nextHop); // Route via the neighbor
            }
        }
        
        // Print updated routing table
        System.out.println(name + " updated routing table: ");
    }

    /**
     * Lookup the next hop router for a given destination IP address.
     *
     * @param destinationIp The IP address of the destination.
     * @return The Router object corresponding to the next hop, or null if no route is found.
     */
    public Router lookupNextHop(String destinationIp) {
        return routingTable.getOrDefault(destinationIp, null);  // Return next hop Router or null
    }

    /**
     * Process an incoming packet.
     * This method checks if the current router is the destination for the packet.
     * If not, it looks up the next hop router based on the destination IP and forwards the packet.
     *
     * @param packet The IP packet to be processed.
     * @param topology The network topology to resolve routers.
     */
    public void processPacket(IPPacket packet, NetworkTopology topology) {
        String destinationIP = packet.getDestinationIP();
        System.out.println("Router " + name + " is processing packet from " + packet.getSourceIP() + " to " + destinationIP);
        
        // Check if this router is the destination
        if (ipAddress.equals(destinationIP)) {
            System.out.println("Packet arrived at destination: " + name + " with message: " + packet.getMessage());
        } else {
            // Lookup the routing table for the next hop
            Router nextHopRouter = lookupNextHop(destinationIP);

            // Debugging info: print the next hop router name
            if (nextHopRouter != null) {
                System.out.println("Next hop for destination IP " + destinationIP + " is: " + nextHopRouter.getName());
                
                // Forward packet to next hop
                nextHopRouter.processPacket(packet, topology);
            } else {
                System.out.println("No route found to destination IP: " + destinationIP);
            }
        }
    }

    /**
     * Create a packet to send a message to a specific destination IP.
     * 
     * @param destinationIp The IP address of the destination router.
     * @param message The message to be sent in the packet.
     * @param topology The network topology to resolve routers.
     */
    public void sendMessage(String destinationIp, String message, NetworkTopology topology) {
        IPPacket packet = new IPPacket(this.ipAddress, destinationIp, message);
        processPacket(packet, topology);
    }

    private String getNeighborNames() {
        StringBuilder sb = new StringBuilder();
        for (Router neighbor : neighbors) {
            sb.append(neighbor.getName()).append(" ");
        }
        return sb.toString();
    }

    // For testing purposes, allow external access to the routing table
    public Map<String, Router> getRoutingTable() {
        return routingTable;
    }
}
