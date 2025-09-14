package com.airticket.common.util;

import java.util.*;

/**
 * Custom Graph data structure for flight route optimization
 * Implements Dijkstra's algorithm for shortest path calculation
 */
public class FlightGraph {
    private final Map<String, List<Edge>> adjacencyList;

    public FlightGraph() {
        this.adjacencyList = new HashMap<>();
    }

    public void addAirport(String airport) {
        adjacencyList.putIfAbsent(airport, new ArrayList<>());
    }

    public void addRoute(String from, String to, double cost, int duration) {
        addAirport(from);
        addAirport(to);
        adjacencyList.get(from).add(new Edge(to, cost, duration));
    }

    public RouteResult findShortestRoute(String source, String destination) {
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Map<String, Integer> totalDuration = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(Node::getCost));

        // Initialize
        for (String airport : adjacencyList.keySet()) {
            distances.put(airport, Double.MAX_VALUE);
            totalDuration.put(airport, Integer.MAX_VALUE);
        }
        distances.put(source, 0.0);
        totalDuration.put(source, 0);
        pq.offer(new Node(source, 0.0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            String currentAirport = current.getAirport();

            if (currentAirport.equals(destination)) {
                break;
            }

            if (current.getCost() > distances.get(currentAirport)) {
                continue;
            }

            List<Edge> neighbors = adjacencyList.get(currentAirport);
            if (neighbors != null) {
                for (Edge edge : neighbors) {
                    double newDistance = distances.get(currentAirport) + edge.getCost();
                    
                    if (newDistance < distances.get(edge.getDestination())) {
                        distances.put(edge.getDestination(), newDistance);
                        previous.put(edge.getDestination(), currentAirport);
                        totalDuration.put(edge.getDestination(), 
                            totalDuration.get(currentAirport) + edge.getDuration());
                        pq.offer(new Node(edge.getDestination(), newDistance));
                    }
                }
            }
        }

        // Reconstruct path
        List<String> path = new ArrayList<>();
        String current = destination;
        while (current != null) {
            path.add(0, current);
            current = previous.get(current);
        }

        return new RouteResult(path, distances.get(destination), totalDuration.get(destination));
    }

    // Inner classes
    public static class Edge {
        private final String destination;
        private final double cost;
        private final int duration;

        public Edge(String destination, double cost, int duration) {
            this.destination = destination;
            this.cost = cost;
            this.duration = duration;
        }

        public String getDestination() { return destination; }
        public double getCost() { return cost; }
        public int getDuration() { return duration; }
    }

    private static class Node {
        private final String airport;
        private final double cost;

        public Node(String airport, double cost) {
            this.airport = airport;
            this.cost = cost;
        }

        public String getAirport() { return airport; }
        public double getCost() { return cost; }
    }

    public static class RouteResult {
        private final List<String> path;
        private final double totalCost;
        private final int totalDuration;

        public RouteResult(List<String> path, double totalCost, int totalDuration) {
            this.path = path;
            this.totalCost = totalCost;
            this.totalDuration = totalDuration;
        }

        public List<String> getPath() { return path; }
        public double getTotalCost() { return totalCost; }
        public int getTotalDuration() { return totalDuration; }
    }
}