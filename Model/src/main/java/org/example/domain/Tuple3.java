package org.example.domain;

import java.io.Serializable;

public class Tuple3 implements Serializable {
    private DistEnum distance;
    private StyleEnum style;
    private long nrParticipants;

    public Tuple3(DistEnum distance, StyleEnum style, long nrParticipants) {
        this.distance = distance;
        this.style = style;
        this.nrParticipants = nrParticipants;
    }

    public DistEnum getDistance() {
        return distance;
    }

    public StyleEnum getStyle() {
        return style;
    }

    public long getNrParticipants() {
        return nrParticipants;
    }

    @Override
    public String toString() {
        return "CompetitionTuple{" +
                "distance='" + distance + '\'' +
                ", style='" + style + '\'' +
                ", nrParticipants=" + nrParticipants +
                '}';
    }
}
