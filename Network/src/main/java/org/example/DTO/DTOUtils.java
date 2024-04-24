package org.example.DTO;


import org.example.domain.Admin;
import org.example.domain.Participant;
import org.example.domain.Tuple3;

public class DTOUtils {
    public static AdminDTO getDTO(Admin admin){
        String user = admin.getUsername();
        String password = admin.getPassword();
        return new AdminDTO(user, password);
    }

    public static ParticipantContestsDTO getDTO(Participant participant,Iterable<Tuple3> contests){
        return new ParticipantContestsDTO(participant,contests);
    }
    public static Admin getFromDTO(AdminDTO adminDTO){
        String user = adminDTO.getUser();
        String password = adminDTO.getPasswd();
        return new Admin(user, password);
    }

    public static Participant getDTO_Participant(ParticipantContestsDTO participantContestsDTO){
        return participantContestsDTO.getParticipant();
    }

    public static Iterable<Tuple3> getDTO_Contests(ParticipantContestsDTO participantContestsDTO){
        return participantContestsDTO.getContests();
    }
/*
    public static Game getFromDTO(GameDTO gameDTO){
        Game game = new Game(gameDTO.getFirstTeamName(), gameDTO.getSecondTeamName(),gameDTO.getPhase(), gameDTO.getArenaName(), gameDTO.getNumberOfSeats(),gameDTO.getPrice());
        game.setId(gameDTO.getId());
        return game;
    }*/
}
