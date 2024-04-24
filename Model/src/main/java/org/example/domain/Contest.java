package org.example.domain;


import java.io.Serializable;

public class Contest extends Entity<Long> implements Serializable {
    private DistEnum Distance;
    private StyleEnum Style;

    public Contest(DistEnum distance, StyleEnum style) {
        Distance = distance;
        Style = style;
    }

    public DistEnum getDistance() {
        return Distance;
    }

    public void setDistance(DistEnum distance) {
        Distance = distance;
    }

    public StyleEnum getStyle() {
        return Style;
    }

    public void setStyle(StyleEnum style) {
        Style = style;
    }

    @Override
    public String toString() {
        return "Contest{" +
                "Distance=" + Distance +
                ", Style=" + Style +
                '}';
    }
}
