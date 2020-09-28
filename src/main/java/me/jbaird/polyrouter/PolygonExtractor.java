package me.jbaird.polyrouter;

import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureIterator;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;

import java.io.IOException;

public class PolygonExtractor {
    static MultiPolygon firstMultiPolygon(SimpleFeatureSource source) throws IOException {
        SimpleFeatureIterator iterator = source.getFeatures().features();
        if (iterator.hasNext()) {
            SimpleFeature first = iterator.next();
            try {
                iterator.close();
                return (MultiPolygon) first.getDefaultGeometry();
            } catch (ClassCastException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            iterator.close();
            return null;
        }
    }
}
