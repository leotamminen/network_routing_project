package com.bgpsimulator.packet;


public class IPPacket {
    private String sourceIP;
    private String destinationIP;
    private String message;

    public IPPacket(String sourceIP, String destinationIP, String message) {
        this.sourceIP = sourceIP;
        this.destinationIP = destinationIP;
        this.message = message;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public String getDestinationIP() {
        return destinationIP;
    }

    public String getMessage() {
        return message;
    }
}
