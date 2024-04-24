package org.example.interfaces;

import org.example.SwimmingException;
import org.example.domain.*;

import java.util.Optional;

public interface ServiceInterface {

   public void login(Admin admin,ObserverInterface client) throws SwimmingException;

    public Iterable<Tuple3> findTuple3_table() throws SwimmingException;

    public Iterable<ParticipationDTO> findAllParticipationByDistStyle(DistEnum dist, StyleEnum style) throws SwimmingException;

    void logout(Admin admin) throws SwimmingException;

    void addParticipantandParticipation(Iterable<Tuple3> selectedItems, Participant participant) throws SwimmingException;
}
