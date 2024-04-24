package org.example.DTO;

import org.example.domain.Participant;
import org.example.domain.Tuple3;

import java.io.Serializable;

public class ParticipantContestsDTO implements Serializable {

   private Participant participant;
   private Iterable<Tuple3> contests;

    public ParticipantContestsDTO(Participant participant, Iterable<Tuple3> contests) {
        this.participant = participant;
        this.contests = contests;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Iterable<Tuple3> getContests() {
        return contests;
    }

    public void setContests(Iterable<Tuple3> contests) {
        this.contests = contests;
    }

    @Override
    public String toString() {
        return "ParticipantContests{" +
                "participant=" + participant +
                ", contests=" + contests +
                '}';
    }
}
