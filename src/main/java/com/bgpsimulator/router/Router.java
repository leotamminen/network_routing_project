package com.bgpsimulator.router;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bgpsimulator.network.TopologyParser;

public class Router {
    private String routerIp;
    private int routerPort;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private Map<String, String> routingTable;  // Destination IP to Next Hop IP

    public Router(String ip, int port) {
        this.routerIp = ip;
        this.routerPort = port;
        this.threadPool = Executors.newFixedThreadPool(10); // Handle up to 10 concurrent connections
        this.routingTable = new HashMap<>();
    }

    // Method to start the server socket for this router
    public void startServer() {
        try {
            serverSocket = new ServerSocket(routerPort);
            System.out.println("Router started on port " + routerPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket.getInetAddress());

                // Handle each connection in a new thread
                threadPool.submit(() -> handleConnection(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle incoming connections (read/write messages)
    private void handleConnection(Socket socket) {
        System.out.println("Handling connection with " + socket.getInetAddress());
        // Implement BGP message handling here...
    }

    // Method to update the routing table
    public void addRoute(String destinationIp, String nextHopIp) {
        routingTable.put(destinationIp, nextHopIp);
    }

    // Method to connect to neighbors based on topology links
    public void connectToNeighbors(List<TopologyParser.Link> links) {
        for (TopologyParser.Link link : links) {
            if (link.router1Ip.equals(this.routerIp) && link.router1Port == this.routerPort) {
                try {
                    Socket socket = new Socket(link.router2Ip, link.router2Port);
                    System.out.println("Connected to neighbor " + link.router2Ip + ":" + link.router2Port);
                    threadPool.submit(() -> handleConnection(socket));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // New Method to send data from this router to the designated router
    public void sendPacket(String destinationIp, byte[] data) {
        // Step 1: Find the next hop from the routing table
        String nextHopIp = routingTable.get(destinationIp);

        if (nextHopIp != null) {
            // Step 2: Create a socket connection to the next hop
            try {
                Socket socket = new Socket(nextHopIp, routerPort);  // Assuming routers have the same port
                OutputStream outputStream = socket.getOutputStream();

                // Step 3: Send the data to the next hop
                outputStream.write(data);
                outputStream.flush();
                System.out.println("Packet sent to " + destinationIp + " via " + nextHopIp);

                // Close the socket after sending the data
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No route to destination " + destinationIp);
        }
    }

    // Close resources
    public void shutdown() {
        try {
            serverSocket.close();
            threadPool.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
