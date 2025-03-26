## DTEK8060 Programming Project

This is a **BGP (Border Gateway Protocol) simulator** project that can be executed as a standalone Java application.  
Users can run the simulator by downloading the compiled `.jar` file and following the instructions below.

---

## Overview

A console-based simulator for understanding BGP-style routing behavior.  
Users input router topology and can send messages between routers, which are forwarded along the shortest available path.

---

## Prerequisites

Before running the BGP Simulator, make sure you have the following:

### Java Runtime Environment (JRE)

You need **Java 17 or higher** installed on your system.

Check your version with:

```bash
java -version
```

---

## Setup & Run Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/leotamminen/network_routing_project
cd network_routing_project
```

### 2. Run the Program

```bash
java -jar target/bgpsimulator-1.0-SNAPSHOT.jar
```

---

## Usage

1. Run the program and input the number of routers.
2. Manually enter the neighbors for each router (e.g., `2,3`).
3. Specify the router number from which to send the message (e.g., `5`).
4. Enter the destination router number (e.g., `6`).
5. Type the message — it will be routed along the shortest path.

---

## Example

```
Enter the number of routers: 7
Enter neighbors for Router1 (comma separated, e.g., 2,3): 2,3,7
Enter neighbors for Router2 (comma separated, e.g., 2,3): 1,4,6
Enter neighbors for Router3 (comma separated, e.g., 2,3): 1,5
Enter neighbors for Router4 (comma separated, e.g., 2,3): 2
Enter neighbors for Router5 (comma separated, e.g., 2,3): 3
Enter neighbors for Router6 (comma separated, e.g., 2,3): 2
Enter neighbors for Router7 (comma separated, e.g., 2,3): 1
...
Enter the router number to send messages from: 5
Enter destination router number (or type 'exit' to quit): 6
Enter your message: hello
...
Routing packet via: [com.bgpsimulator.router.Router@6193b845, com.bgpsimulator.router.Router@1f17ae12, com.bgpsimulator.router.Router@76fb509a, com.bgpsimulator.router.Router@300ffa5d, com.bgpsimulator.router.Router@2e817b38]
Router5 forwarding packet to Router3
Router3 forwarding packet to Router1
Router1 forwarding packet to Router2
Router2 forwarding packet to Router6
Packet arrived at destination: Router6 with message: hello
Enter destination router number (or type 'exit' to quit):
```
