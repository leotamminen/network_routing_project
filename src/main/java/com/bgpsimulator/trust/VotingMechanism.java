package com.bgpsimulator.trust;

import com.bgpsimulator.network.NetworkTopology;
import com.bgpsimulator.router.Router;

public class VotingMechanism {
    private TrustManager trustManager;

    public VotingMechanism(TrustManager trustManager) {
        this.trustManager = trustManager;
    }

    public void voteOnNeighbors(Router router, NetworkTopology topology) {
        for (Router neighbor : topology.getNeighbors(router)) {
            // Simulate a vote for neighbor trust
            int voteValue = (int) (Math.random() * 100);  // Random trust vote
            trustManager.voteTrust(neighbor, router, voteValue);
        }
    }
}

