package me.jbaird.polyrouter;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class Router {
    private MultiPolygon polygon;
    private Point origin;
    private Point destination;
    private GeometryFactory factory;
    private RouterConfiguration configuration;

    public Router(MultiPolygon polygon, Point origin, Point destination, RouterConfiguration configuration) throws InvalidRouterException {
        this.polygon = polygon;
        if (!polygon.contains(origin) || !polygon.contains(destination)) {
            throw new InvalidRouterException();
        }
        this.origin = origin;
        this.destination = destination;
        this.factory = polygon.getFactory();
        this.configuration = configuration;
    }

    public Route route() {
        PriorityQueue<RoutingPoint> queue = new PriorityQueue<>();
        ArrayList<RoutingPoint> explored = new ArrayList<>();
        RoutingPoint origin = new RoutingPoint(this.origin);
        queue.add(origin);
        RoutingPoint next;
        while ((next = queue.poll()) != null) {
            if (next.getPoint().isWithinDistance(this.destination, this.configuration.getProximity())) {
                return new Route(next.getPath());
            }
            explored.add(next);
            List<RoutingPoint> neighbours = next.getNeighbours(this.configuration.getDelta());
            for (RoutingPoint neighbour : neighbours) {
                if (!explored.contains(neighbour)) {
                    // Check if line from point to neighbour is within the polygon
                    Coordinate[] lineCoordinates = new Coordinate[]{next.getPoint().getCoordinate(), neighbour.getPoint().getCoordinate()};
                    if (!this.polygon.contains(this.factory.createLineString(lineCoordinates))) {
                        continue;
                    }
                    double heuristic = this.destination.distance(neighbour.getPoint());
                    neighbour.setHeuristic(heuristic);
                    // Update priority if better than current queue value
                    if (queue.contains(neighbour)) {
                        boolean toAdd = false;
                        RoutingPoint inQueue;
                        Iterator<RoutingPoint> queueIterator = queue.iterator();
                        while ((queueIterator.hasNext())) {
                            inQueue = queueIterator.next();
                            if (inQueue.equals(neighbour)) {
                                int cmp = neighbour.compareTo(inQueue);
                                if (cmp == -1) {
                                    queueIterator.remove();
                                    toAdd = true;
                                    break;
                                }
                            }
                        }
                        if (toAdd) {
                            queue.add(neighbour);
                        }
                    } else {
                        queue.add(neighbour);
                    }
                }
            }
        }
        return null;
    }
}
