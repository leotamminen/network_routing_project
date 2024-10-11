package com.bgpsimulator.packet;


import java.io.Serializable;

public class IPPacket implements Serializable {
    private static final long serialVersionUID = 1L;  // Add a serialVersionUID for version control

    private String sourceIP;
    private int destinationPort;
    private String message;

    public IPPacket(String sourceIP, int destinationPort, String message) {
        this.sourceIP = sourceIP;
        this.destinationPort = destinationPort;
        this.message = message;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public String getMessage() {
        return message;
    }
}
