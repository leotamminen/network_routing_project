package com.bgpsimulator;


import java.util.ArrayList;
import java.util.List;

import com.bgpsimulator.network.TopologyParser;
import com.bgpsimulator.router.Router;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        List<TopologyParser.Link> links = new ArrayList<>();
        links.add(new TopologyParser.Link("localhost", 5000, "localhost", 5001));
        links.add(new TopologyParser.Link("localhost", 5001, "localhost", 5002));
        links.add(new TopologyParser.Link("localhost", 5002, "localhost", 5003));

        Router router1 = new Router("localhost", 5000);
        Router router2 = new Router("localhost", 5001);
        Router router3 = new Router("localhost", 5002);
        Router router4 = new Router("localhost", 5003);

        // Start the routers
        new Thread(() -> router1.startServer()).start();
        new Thread(() -> router2.startServer()).start();
        new Thread(() -> router3.startServer()).start();
        new Thread(() -> router4.startServer()).start();

        Thread.sleep(2000);  // Wait for servers to start

        // Define routing tables (e.g., Router1 sends to Router4 via Router2 -> Router3)
        router1.addRoute("localhost", "localhost");  // Next hop to router4

        // Connect routers
        router1.connectToNeighbors(links);
        router2.connectToNeighbors(links);
        router3.connectToNeighbors(links);
        router4.connectToNeighbors(links);

        // Send a packet from Router1 to Router4
        byte[] data = "Hello, Router4!".getBytes();
        router1.sendPacket("localhost", data);  // Route the packet to Router4
    }
}
