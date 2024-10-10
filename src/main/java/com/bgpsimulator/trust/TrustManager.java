package com.bgpsimulator.trust;

import java.util.HashMap;
import java.util.Map;

import com.bgpsimulator.router.Router;

public class TrustManager {
    private Map<Router, Integer> trustValues = new HashMap<>();

    // Set trust value for a router
    public void setTrust(Router router, int trustValue) {
        trustValues.put(router, trustValue);
    }

    // Get trust value for a router
    public int getTrust(Router router) {
        return trustValues.getOrDefault(router, 50);  // Default trust value is 50
    }

    // Voting-based trust calculation
    public void voteTrust(Router neighbor, Router voter, int voteValue) {
        System.out.println(voter.getName() + " voted " + voteValue + " trust for " + neighbor.getName());
        // Update trust value based on voting results
    }
}

