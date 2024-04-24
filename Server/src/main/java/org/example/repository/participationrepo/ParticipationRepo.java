package org.example.repository.participationrepo;


import org.example.domain.Participation;
import org.example.domain.Tuple;
import org.example.repository.Repository;

public interface ParticipationRepo extends Repository<Tuple<Long,Long>, Participation> {
}
