package me.jbaird.polyrouter;

public class RouterConfiguration {
    private double delta;
    private double proximity;

    public static RouterConfiguration defaultConfiguration() {
        RouterConfiguration configuration = new RouterConfiguration();
        configuration.delta = 0.005;
        configuration.proximity = 0.01;
        return configuration;
    }

    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }

    public double getProximity() {
        return proximity;
    }

    public void setProximity(double proximity) {
        this.proximity = proximity;
    }
}
