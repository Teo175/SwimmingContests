package org.example.service;


import org.example.SwimmingException;
import org.example.domain.*;
import org.example.interfaces.ObserverInterface;
import org.example.interfaces.ServiceInterface;
import org.example.repository.adminrepo.AdminRepoDB;
import org.example.repository.contestrepo.ContestRepoDB;
import org.example.repository.participantrepo.ParticipantRepoDB;
import org.example.repository.participationrepo.ParticipationRepoDB;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Service implements ServiceInterface {

    private AdminRepoDB adminRepoDB;
    private ContestRepoDB contestRepoDB;
    private ParticipantRepoDB participantRepoDB;
    private ParticipationRepoDB participationRepoDB;

    private Map<String, ObserverInterface> loggedAdmin;

    private final int defaultThreadsNo = 5;


    public Service(AdminRepoDB adminRepoDB, ContestRepoDB contestRepoDB, ParticipantRepoDB participantRepoDB, ParticipationRepoDB participationRepoDB) {
        this.adminRepoDB = adminRepoDB;
        this.contestRepoDB = contestRepoDB;
        this.participantRepoDB = participantRepoDB;
        this.participationRepoDB = participationRepoDB;
        this.loggedAdmin = new ConcurrentHashMap<>();
    }



    @Override
    public synchronized void login(Admin admin, ObserverInterface client) throws SwimmingException {

        Optional<Admin> searched_admin = adminRepoDB.findOneByUsername(admin.getUsername(), admin.getPassword());

        if(searched_admin == null) {
            throw new SwimmingException("Error! Username not found!");
        }

        if(loggedAdmin.get(searched_admin.get().getUsername()) != null)
            throw new SwimmingException("User already logged in.");

        loggedAdmin.put(searched_admin.get().getUsername(), client);
        System.out.println("Log In OK!");

    }

    public Iterable<Tuple3> findTuple3_table() {
        System.out.println("sunt in service");
        return participationRepoDB.getCompetitionParticipants();
    }


    @Override
    public synchronized void logout(Admin admin) throws SwimmingException {
        loggedAdmin.remove(admin.getUsername());
    }


    @Override
    public synchronized void addParticipantandParticipation(Iterable<Tuple3> selectedItems, Participant participant) throws SwimmingException {
        System.out.println("service");
        List<Long> id_contests = new ArrayList<>();

        for (Tuple3 tpl : selectedItems) {
            Optional<Contest> contest = contestRepoDB.findByDistStyle(tpl.getDistance(), tpl.getStyle());
            id_contests.add(contest.get().getId());
        }
        if (!id_contests.isEmpty()) {
            if (participantRepoDB.findByNameAge(participant.getName(),participant.getAge()) == null) {
                participantRepoDB.save(new Participant(participant.getName(),participant.getAge()));
                Participant p = participantRepoDB.findByNameAge(participant.getName(),participant.getAge());

                for (Long id_contest : id_contests) {
                    Participation participation = new Participation(p, contestRepoDB.findOne(id_contest));
                    participationRepoDB.save(participation);
                }
                Iterable<Tuple3> all = participationRepoDB.getCompetitionParticipants();
                notifyClient(all);

            }
        }
    }

    private void notifyClient(Iterable<Tuple3> allcontests) {
        System.out.println("Clients notified!");

        ExecutorService executorService = Executors.newFixedThreadPool(defaultThreadsNo);
        for(String user : loggedAdmin.keySet()){
            ObserverInterface client = loggedAdmin.get(user);
            if(client != null){
                executorService.execute(() ->{
                    try{
                        System.out.println("Notifying: " + user);
                        client.updateContests(allcontests);
                    }
                    catch(Exception e){
                        System.err.println("Error notifying " + e);
                    }
                });
            }
        }

        executorService.shutdown();
    }


    public Iterable<ParticipationDTO> findAllParticipationByDistStyle(DistEnum dist, StyleEnum style) {
        Iterable<Participation> participations = participationRepoDB.findAllByDistStyle(dist,style);
        Map<Participant, List<Contest>> participantMap = new HashMap<>();
        for (Participation p : participations) {
            Participant participant = p.getParticipant();
            Contest contest = p.getContest();

            if (!participantMap.containsKey(participant)) {
                participantMap.put(participant, new ArrayList<>());
            }
            participantMap.get(participant).add(contest);
        }

        List<ParticipationDTO> participantDTOS = new ArrayList<>();
        for (Map.Entry<Participant, List<Contest>> entry : participantMap.entrySet()) {
            Participant participant = entry.getKey();
            List<Contest> contests = entry.getValue();
            StringBuilder contestString = new StringBuilder();

            for (Contest contest : contests) {
                if (contest.getDistance() != dist && contest.getStyle() != style)
                    contestString.append(contest.getDistance()).append(" ").append(contest.getStyle()).append(" | ");
            }

            Iterable<Contest> participantContests = participationRepoDB.getAllContestsFromAParticipant(participant.getId());
            for (Contest c : participantContests) {
                contestString.append(c.getDistance()).append(" ").append(c.getStyle()).append(" | ");
            }


            participantDTOS.add(new ParticipationDTO(participant.getName(), participant.getAge(), contestString.toString()));
        }
        return participantDTOS;
    }

}
