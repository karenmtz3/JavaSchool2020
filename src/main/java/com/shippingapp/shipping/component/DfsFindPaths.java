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

    public String getFirstPathFromOriginToDestination(List<CityPath> cityPaths, String origin, String destination) {
        Map<String, Set<String>> graph = new HashMap<>();
        generateGraph(graph, cityPaths);

        List<List<String>> paths = new ArrayList<>();
        List<String> visited = new ArrayList<>();
        visited.add(origin);

        findAllPaths(graph, visited, paths, origin, destination);

        return String.join(" -> ", paths
                .stream()
                .findFirst().get());
    }

    private void findAllPaths(Map<String, Set<String>> graph, List<String> visited, List<List<String>> paths, String origin, String destination) {
        if (origin.equals(destination)) {
            paths.add(visited);
        } else {
            Set<String> adjacent = adjacentCities(graph, origin);
            for (String city : adjacent) {
                if (visited.contains(city)) {
                    continue;
                }
                List<String> routeList = new ArrayList<>(visited);
                routeList.add(city);
                findAllPaths(graph, routeList, paths, city, destination);
            }
        }
    }

    private void generateGraph(Map<String, Set<String>> graph, List<CityPath> cityPaths) {
        cityPaths.forEach(cityPath -> {
            Set<String> adjacent = graph.computeIfAbsent(cityPath.getFrom(), k -> new HashSet<>());
            adjacent.add(cityPath.getTo());
        });
    }

    private Set<String> adjacentCities(Map<String, Set<String>> graph, String origin) {
        Set<String> adjacent = graph.get(origin);
        if (Objects.isNull(adjacent)) {
            return new HashSet<>();
        }
        return new HashSet<>(adjacent);
    }
}
