package com.bgpsimulator.network;

import java.util.*;

import com.bgpsimulator.router.Router;


public class NetworkTopology {
    private Map<Router, List<Router>> topology; // Maps each Router to its neighbors

    public NetworkTopology() {
        topology = new HashMap<>();
    }

    /**
     * Adds a router to the network topology.
     * 
     * @param router The Router to add.
     */
    public void addRouter(Router router) {
        topology.put(router, new ArrayList<>()); // Initialize an empty list for its neighbors
    }

    /**
     * Adds a bidirectional connection between two routers in the topology.
     * 
     * @param routerA One end of the connection.
     * @param routerB The other end of the connection.
     */
    public void addConnection(Router routerA, Router routerB) {
        // Add routerB to routerA's neighbors
        topology.get(routerA).add(routerB);
        // Add routerA to routerB's neighbors
        topology.get(routerB).add(routerA);
    }

    /**
     * Returns the list of neighbors for a given router.
     * 
     * @param router The Router whose neighbors to return.
     * @return A list of neighboring routers.
     */
    public List<Router> getNeighbors(Router router) {
        return topology.getOrDefault(router, Collections.emptyList());
    }

    /**
     * Returns the entire topology as a Map for debugging.
     * 
     * @return The network topology.
     */
    public Map<Router, List<Router>> getTopology() {
        return topology;
    }

    public List<Router> computeShortestPath(Router source, Router destination) {
        if (!topology.containsKey(source) || !topology.containsKey(destination)) {
            return Collections.emptyList(); // No path found
        }

        Queue<List<Router>> queue = new LinkedList<>();
        Set<Router> visited = new HashSet<>();

        queue.add(Collections.singletonList(source));
        visited.add(source);

        while (!queue.isEmpty()) {
            List<Router> path = queue.poll();
            Router lastRouter = path.get(path.size() - 1);

            if (lastRouter.equals(destination)) {
                return path; // Found the shortest path
            }

            for (Router neighbor : topology.get(lastRouter)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    List<Router> newPath = new ArrayList<>(path);
                    newPath.add(neighbor);
                    queue.add(newPath);
                }
            }
        }

        return Collections.emptyList(); // No route found
    }

}
