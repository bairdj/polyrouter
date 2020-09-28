package me.jbaird.polyrouter;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.locationtech.jts.geom.MultiPolygon;
import org.opengis.feature.simple.SimpleFeature;

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
