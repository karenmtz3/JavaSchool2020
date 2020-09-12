package com.shippingapp.shipping.component;

import com.shippingapp.shipping.models.CityPath;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DfsFindPaths {
    private final Map<String, Set<String>> graph = new HashMap<>();
    private List<CityPath> cityPaths;
    private String origin;
    private String destination;

    public void setCityPaths(List<CityPath> cityPaths) {
        this.cityPaths = cityPaths;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getFirstPathFromOriginToDestination() {
        generateGraph();

        List<List<String>> paths = new ArrayList<>();
        List<String> visited = new ArrayList<>();
        visited.add(origin);
        findAllPaths(visited, paths, origin);

        return String.join(" -> ", paths
                .stream()
                .findFirst().get());
    }

    private void findAllPaths(List<String> visited, List<List<String>> paths, String origin) {
        if (origin.equals(destination)) {
            paths.add(visited);
        } else {
            Set<String> adjacent = adjacentCities(origin);
            for (String city : adjacent) {
                if (visited.contains(city)) {
                    continue;
                }
                List<String> routeList = new ArrayList<>(visited);
                routeList.add(city);
                findAllPaths(routeList, paths, city);
            }
        }
    }

    private void generateGraph() {
        cityPaths.forEach(cityPath -> addEdge(cityPath.getFrom(), cityPath.getTo()));
    }

    private void addEdge(String origin, String destination) {
        Set<String> adjacent = graph.computeIfAbsent(origin, k -> new HashSet<>());
        adjacent.add(destination);
    }

    private Set<String> adjacentCities(String origin) {
        Set<String> adjacent = graph.get(origin);
        if (adjacent == null) {
            return new HashSet<>();
        }
        return new HashSet<>(adjacent);
    }
}
