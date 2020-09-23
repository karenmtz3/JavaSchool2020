package com.shippingapp.shipping.services.impl;

import com.shippingapp.shipping.models.CityPath;
import com.shippingapp.shipping.models.Node;
import com.shippingapp.shipping.models.Route;

import com.shippingapp.shipping.services.OptimalPathService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

@Service
public class OptimalPathServiceImpl implements OptimalPathService {
    private String optimalPath;

    public OptimalPathServiceImpl() {
        optimalPath = "";
    }

    public String getOptimalPathBetweenTwoCities(List<CityPath> cityPaths, String origin, String destination) {
        Map<String, Set<Node>> graph = new HashMap<>();
        generateGraph(graph, cityPaths);

        Route originRoute = new Route(Collections.singletonList(origin), 0);
        PriorityQueue<Route> queue = new PriorityQueue<>(Comparator.comparing(Route::getCost));
        queue.add(originRoute);

        while (!queue.isEmpty()) {
            Route route = queue.poll();
            int size = route.getPath().size();
            String city = route.getPath().get(size - 1);
            if (city.equals(destination)) {
                optimalPath = String.join(" -> ", route.getPath());
                return optimalPath;
            }
            Set<Node> adjacentCities = getAdjacentCities(graph, city);
            for (Node nodeCity : adjacentCities) {
                if (!route.getPath().contains(nodeCity.getCity())) {
                    List<String> pathList = new ArrayList<>(route.getPath());
                    pathList.add(nodeCity.getCity());
                    int cost = route.getCost() + nodeCity.getCost();
                    Route newRoute = new Route(pathList, cost);
                    queue.add(newRoute);
                }
            }
        }
        return optimalPath;
    }

    private void generateGraph(Map<String, Set<Node>> graph, List<CityPath> cityPaths) {
        cityPaths.forEach(cityPath -> {
            Set<Node> adjacent = graph.computeIfAbsent(cityPath.getFrom(), k -> new HashSet<>());
            Node node = new Node(cityPath.getTo(), cityPath.getDistance());
            adjacent.add(node);
        });
    }

    private Set<Node> getAdjacentCities(Map<String, Set<Node>> graph, String origin) {
        Set<Node> adjacent = graph.get(origin);
        if (Objects.isNull(adjacent)) {
            return new HashSet<>();
        }
        return new HashSet<>(adjacent);
    }
}