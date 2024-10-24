package com.bgpsimulator.network;


public class TopologyParser {
    
    // Inner class to hold router link information
    public static class Link {
        public String router1Ip;
        public int router1Port;
        public String router2Ip;
        public int router2Port;

        public Link(String router1Ip, int router1Port, String router2Ip, int router2Port) {
            this.router1Ip = router1Ip;
            this.router1Port = router1Port;
            this.router2Ip = router2Ip;
            this.router2Port = router2Port;
        }

        @Override
        public String toString() {
            return router1Ip + ":" + router1Port + " <--> " + router2Ip + ":" + router2Port;
        }
    }
}
