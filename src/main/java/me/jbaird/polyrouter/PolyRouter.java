package me.jbaird.polyrouter;

import org.apache.commons.cli.*;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.WKTReader2;
import org.geotools.geometry.jts.WKTWriter2;
import org.locationtech.jts.geom.*;

import java.io.*;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class PolyRouter
{
    public static void main( String[] args ) throws ParseException {
        Options options = new Options();
        options.addOption("h", "Show this page");
        options.addOption("s", "shapefile", true, "Path to shapefile containing polygon");
        options.addOption("wkt", true, "Path to file containing Polygon WKT");
        options.addOption("d", "delta", true, "Delta value to generate neighbours");
        options.addOption("o", "output", true, "Path to output. Default is stdout");

        Option.Builder coordinate = Option.builder().numberOfArgs(2).valueSeparator(',');
        options.addOption(coordinate.longOpt("from").desc("Origin coordinate (x,y)").build());
        options.addOption(coordinate.longOpt("to").desc("Destination coordinate (x,y)").build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("h")) {
            HelpFormatter help = new HelpFormatter();
            help.printHelp("polyrouter", options);
            return;
        }

        // Check all required options are specified
        if (!cmd.hasOption("s") && !cmd.hasOption("wkt")) {
            System.err.println("Shapefile or WKT must be specified.");
            return;
        }

        if (!cmd.hasOption("from") || !cmd.hasOption("to")) {
            System.err.println("To and from must be specified.");
            return;
        }

        GeometryFactory factory = JTSFactoryFinder.getGeometryFactory();
        MultiPolygon polygon = null;
        // Handle shapefile. Will extract first polygon
        if (cmd.hasOption("s")) {
            try {
                File shapefile = new File(cmd.getOptionValue("s"));
                Map<String, Object> params = new HashMap<>();
                params.put("url", shapefile.toURI().toURL());
                DataStore store = DataStoreFinder.getDataStore(params);
                String[] typeNames = store.getTypeNames();
                if (typeNames.length == 0) {
                    System.err.println("No shape type.");
                    return;
                }
                polygon = PolygonExtractor.firstMultiPolygon(store.getFeatureSource(typeNames[0]));
            } catch (MalformedURLException e) {
                System.err.println("Invalid shapefile path.");
                return;
            } catch (IOException e) {
                System.err.println(e.getLocalizedMessage());
                return;
            }
        } else if (cmd.hasOption("wkt")) {
            try {
                WKTReader2 wktReader = new WKTReader2();
                FileReader reader = new FileReader(new File(cmd.getOptionValue("wkt")));
                Geometry imported = wktReader.read(reader);
                if (imported.getClass() == MultiPolygon.class) {
                    polygon = (MultiPolygon) imported;
                } else {
                    polygon = factory.createMultiPolygon(new Polygon[]{(Polygon) imported});
                }
            } catch (FileNotFoundException | org.locationtech.jts.io.ParseException e) {
                System.err.println(e.getLocalizedMessage());
                return;
            }

        }

        String[] originCoordinates = cmd.getOptionValues("from");
        String[] destinationCoordinates = cmd.getOptionValues("to");
        if (originCoordinates != null && destinationCoordinates != null) {
            Point origin = factory.createPoint(new CoordinateXY(Double.parseDouble(originCoordinates[0]), Double.parseDouble(originCoordinates[1])));
            Point destination = factory.createPoint(new CoordinateXY(Double.parseDouble(destinationCoordinates[0]), Double.parseDouble(destinationCoordinates[1])));

            RouterConfiguration configuration = RouterConfiguration.defaultConfiguration();
            if (cmd.hasOption("d")) {
                configuration.setDelta(Double.parseDouble(cmd.getOptionValue("d")));
            }
            try {
                Router router = new Router(polygon, origin, destination, configuration);
                Route route = router.route();
                Writer writer;
                if (cmd.hasOption("o")) {
                    writer = new FileWriter(new File(cmd.getOptionValue("o")));
                } else {
                    writer = new PrintWriter(System.out);
                }
                WKTWriter2 wktWriter = new WKTWriter2();
                wktWriter.write(route.getLineString(), writer);
                writer.flush();
            } catch (InvalidRouterException e) {
                System.err.println(e.getLocalizedMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
