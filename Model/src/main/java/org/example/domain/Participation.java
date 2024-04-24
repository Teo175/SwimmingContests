package org.example.domain;


public class Participation extends Entity<Tuple<Long,Long>> {
    private Participant participant;
    private Contest contest;

    public Participation(Participant participant, Contest contest) {
        this.participant = participant;
        this.contest = contest;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Contest getContest() {
        return contest;
    }

    public void setContest(Contest contest) {
        this.contest = contest;
    }

    @Override
    public String toString() {
        return "Participation{" +
                "participant=" + participant +
                ", contest=" + contest +
                '}';
    }
}
