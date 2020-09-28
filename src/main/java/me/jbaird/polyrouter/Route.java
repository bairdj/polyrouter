package me.jbaird.polyrouter;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import java.util.List;

public class Route {
    private List<Point> points;

    public Route(List<Point> points) {
        this.points = points;

    }

    public List<Point> getPoints() {
        return this.points;
    }

    public LineString getLineString() {
        GeometryFactory factory = JTSFactoryFinder.getGeometryFactory();
        Coordinate[] coordinates = this.points.stream().map(p -> p.getCoordinate()).toArray(Coordinate[]::new);
        return factory.createLineString(coordinates);
    }
}
