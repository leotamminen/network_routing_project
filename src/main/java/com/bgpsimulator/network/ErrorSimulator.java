package com.bgpsimulator.network;

import com.bgpsimulator.router.Router;


public class ErrorSimulator {
    public void simulateRouterFailure(Router router) {
        System.out.println("Router " + router.getName() + " has gone offline.");
        // Logic to handle router failure, like removing routes
    }

    public void simulateLinkFailure(NetworkTopology topology, Router router1, Router router2) {
        System.out.println("Link between " + router1.getName() + " and " + router2.getName() + " has dropped.");
        // Logic to handle link failure, like removing the link from topology
    }
}
