package com.bgpsimulator.router;

public class BGPProtocol {
    // Method to handle different BGP message types
    public void handleMessage(String messageType, Router fromRouter, Router toRouter) {
        switch (messageType) {
            case "KEEPALIVE":
                System.out.println(fromRouter.getName() + " sent KEEPALIVE to " + toRouter.getName());
                break;
            case "UPDATE":
                System.out.println(fromRouter.getName() + " sent UPDATE to " + toRouter.getName());
                break;
            case "NOTIFICATION":
                System.out.println(fromRouter.getName() + " sent NOTIFICATION to " + toRouter.getName());
                break;
            default:
                System.out.println("Unknown message type");
        }
    }

    // Establish a TCP-like connection (simplified for the simulation)
    public void establishConnection(Router router1, Router router2) {
        System.out.println("TCP connection established between " + router1.getName() + " and " + router2.getName());
    }
}

