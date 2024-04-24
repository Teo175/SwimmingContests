package org.example.RPCProtocol;

import org.example.DTO.AdminDTO;
import org.example.DTO.DTOUtils;
import org.example.DTO.ParticipantContestsDTO;
import org.example.SwimmingException;
import org.example.domain.*;
import org.example.interfaces.ObserverInterface;
import org.example.interfaces.ServiceInterface;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.sql.SQLOutput;

public class ClientWorker implements Runnable, ObserverInterface {
    private ServiceInterface server;
    private Socket connection;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private volatile boolean connected;

    public ClientWorker(ServiceInterface server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try{
            outputStream = new ObjectOutputStream(connection.getOutputStream());
            outputStream.flush();

            inputStream = new ObjectInputStream(connection.getInputStream());
            connected = true;
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void run() {
        while(connected){
            try{
                Object request = inputStream.readObject();
                Response response = handleRequest((Request) request);
                if(response != null)
                    sendResponse(response);
            }
            catch(IOException e){
                e.printStackTrace();
            }
            catch (ClassNotFoundException e){
                e.printStackTrace();
            }

            try{
                Thread.sleep(1000);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void sendResponse(Response response) throws IOException{
        System.out.println("Sending response!");
        synchronized (outputStream){
            outputStream.writeObject(response);
            outputStream.flush();
        }
    }

    private Response handleRequest(Request request) {
        Response response = null;
        String handlerName="handle"+(request).getType();

        try{
            Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
            response = (Response)method.invoke(this, request);
        }
        catch (NoSuchMethodException e){
            e.printStackTrace();
        }
        catch(InvocationTargetException e){
            e.printStackTrace();
        }
        catch (IllegalAccessException e){
            e.printStackTrace();
        }

        return response;
    }

    private static Response okResponse = new Response.Builder().type(ResponseType.OK).build();
    private Response handleLOGIN(Request request){
        AdminDTO cashierDTO = (AdminDTO)request.getData();
        Admin cashier = DTOUtils.getFromDTO(cashierDTO);

        try{
            server.login(cashier, this);
            return okResponse;
        }
        catch (SwimmingException e){
            connected = false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }


    private Response handleUPLOAD_TABLE(Request request){
        try{
            System.out.println("sunt in clientworker");
            Iterable<Tuple3> contests = server.findTuple3_table();
            return new Response.Builder().type(ResponseType.OK).data(contests).build();
        }
        catch(SwimmingException e){
            connected = false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleADD_PARTICIPANT(Request request){
        ParticipantContestsDTO participantContestsDTO = (ParticipantContestsDTO) request.getData();
        Participant participant = DTOUtils.getDTO_Participant(participantContestsDTO);
        Iterable<Tuple3> contests = DTOUtils.getDTO_Contests(participantContestsDTO);

        try{
            System.out.println("sunt in clientworker");
            server.addParticipantandParticipation(contests,participant);
            return new Response.Builder().type(ResponseType.OK).build(); //reinitializarea se face din obs
        }
        catch (SwimmingException e){
            connected = false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleSEARCH(Request request){
        Contest contest = (Contest) request.getData();


        try{
            System.out.println("sunt in search");
            Iterable<ParticipationDTO> participationList = server.findAllParticipationByDistStyle(contest.getDistance(), contest.getStyle());
            return new Response.Builder().type(ResponseType.OK).data(participationList).build();
        }
        catch (SwimmingException e){
            connected = false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleLOGOUT(Request request){
        System.out.println("Logout request...");

        AdminDTO adminDTO = (AdminDTO) request.getData();
        Admin admin = DTOUtils.getFromDTO(adminDTO);
        try {
            server.logout(admin);
            connected=false;
            return okResponse;

        } catch (SwimmingException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    @Override
    public void updateContests(Iterable<Tuple3> contests) throws SwimmingException {
        Response response = new Response.Builder().type(ResponseType.UPDATE).data(contests).build();

        try{
            sendResponse(response);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }



}
