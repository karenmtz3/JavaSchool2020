package com.shippingapp.shipping.component;

import com.shippingapp.shipping.models.CityPath;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

@Component
public class DfsFindPaths {
    private final Map<String, Set<String>> graph = new HashMap<>();

    public String getFirstPathFromOriginToDestination(List<CityPath> cityPaths, String origin, String destination) {
        generateGraph(cityPaths);

        List<List<String>> paths = new ArrayList<>();
        List<String> visited = new ArrayList<>();
        visited.add(origin);
        findAllPaths(visited, paths, origin, destination);

        return String.join(" -> ", paths
                .stream()
                .findFirst().get());
    }

    private void findAllPaths(List<String> visited, List<List<String>> paths, String origin, String destination) {
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
                findAllPaths(routeList, paths, city, destination);
            }
        }
    }

    private void generateGraph(List<CityPath> cityPaths) {
        cityPaths.forEach(cityPath -> addEdge(cityPath.getFrom(), cityPath.getTo()));
    }

    private void addEdge(String origin, String destination) {
        Set<String> adjacent = graph.computeIfAbsent(origin, k -> new HashSet<>());
        adjacent.add(destination);
    }

    private Set<String> adjacentCities(String origin) {
        Set<String> adjacent = graph.get(origin);
        if (Objects.isNull(adjacent)) {
            return new HashSet<>();
        }
        return new HashSet<>(adjacent);
    }
}
