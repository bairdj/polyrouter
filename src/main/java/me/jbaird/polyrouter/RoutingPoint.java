package me.jbaird.polyrouter;

import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RoutingPoint implements Comparable {
    private final double DELTA = 0.01;
    private List<Point> path;
    private BigDecimal x;
    private BigDecimal y;
    private Point point;
    private double pathCost;
    private double heuristic;

    public RoutingPoint(Point point, List<Point> path, double pathCost) {
        this.point = point;
        this.path = path;
        this.pathCost = pathCost;
    }

    public RoutingPoint(Point point) {
        this.point = point;
        this.path = new ArrayList<>();
        this.pathCost = 0;
    }

    public double getCost() {
        return this.pathCost + this.heuristic;
    }

    public Point getPoint() {
        return this.point;
    }

    public List<RoutingPoint> getNeighbours(double delta) {
        ArrayList<RoutingPoint> neighbours = new ArrayList<>();
        neighbours.add(RoutingPoint.nextPointWithTranslation(this, 0, -delta)); // Up
        neighbours.add(RoutingPoint.nextPointWithTranslation(this, delta, -delta)); // Top Right
        neighbours.add(RoutingPoint.nextPointWithTranslation(this, delta, 0)); // Right
        neighbours.add(RoutingPoint.nextPointWithTranslation(this, delta, delta)); // Bottom Right
        neighbours.add(RoutingPoint.nextPointWithTranslation(this, 0, delta)); // Down
        neighbours.add(RoutingPoint.nextPointWithTranslation(this, -delta, delta)); // Bottom Left
        neighbours.add(RoutingPoint.nextPointWithTranslation(this, -delta, 0)); // Left
        neighbours.add(RoutingPoint.nextPointWithTranslation(this, -delta, -delta)); // Top Left
        return neighbours;
    }

    public List<Point> getPath() {
        return this.path;
    }

    public void setHeuristic(double heuristic) {
        this.heuristic = heuristic;
    }

    private static RoutingPoint nextPointWithTranslation(RoutingPoint current, double translateX, double translateY) {
        List<Point> path = new ArrayList<>(current.getPath());
        path.add(current.point);
        Point newPoint = current.point.getFactory().createPoint(new CoordinateXY(current.point.getX() + translateX, current.point.getY() + translateY));
        return new RoutingPoint(newPoint, path, current.pathCost + current.point.distance(newPoint));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == this.getClass()) {
            boolean equal = ((RoutingPoint) obj).getPoint().equalsExact(this.point, 0.001);
            return equal;
        }
        return false;
    }

    @Override
    public int compareTo(Object o) {
        if (o.getClass() == this.getClass()) {
            return (this.getCost() - ((RoutingPoint)o).getCost()) > 0 ? 1 : -1;
        }
        return 0;
    }
}
