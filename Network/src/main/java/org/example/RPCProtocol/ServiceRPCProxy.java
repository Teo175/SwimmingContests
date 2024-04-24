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
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServiceRPCProxy implements ServiceInterface {
    private String host;
    private int port;
    private ObserverInterface client;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Socket connection;
    private BlockingQueue<Response> qresponses;

    private volatile boolean finished;

    public ServiceRPCProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<>();
    }

    private void initializeConnection() throws SwimmingException{
        try{
            connection = new Socket(host, port);
            outputStream = new ObjectOutputStream(connection.getOutputStream());
            outputStream.flush();

            inputStream = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void startReader(){
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }


    private boolean isUpdate(Response response){
        return response.getType() == ResponseType.UPDATE;
    }



    private class ReaderThread implements Runnable{
        public void run(){
            while(!finished){
                try{
                    Object response = inputStream.readObject();

                    if(isUpdate((Response)response)){
                        handleUpdate((Response)response);
                    }
                    else{
                        try{
                            qresponses.put((Response) response);
                        }
                        catch(InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                }
                catch (IOException e){
                    System.err.println("Reading error: " + e);
                }
                catch (ClassNotFoundException e){
                    System.err.println("Reading error: " + e);
                }
            }
        }
    }

    private void sendRequest(Request request) throws SwimmingException{
        try{
            outputStream.writeObject(request);
            outputStream.flush();
        }
        catch(IOException e){
            throw new SwimmingException("Error sending object: " + e.getMessage());
        }
    }

    private Response readResponse() throws SwimmingException{
        Response response = null;

        try{
           response = qresponses.take();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }

        return response;
    }

    private void closeConnection(){
        finished = true;
        try{
            inputStream.close();
            outputStream.close();
            connection.close();
            client = null;
        }catch (IOException e){
            e.printStackTrace();
        }
    }
 //-----------------------------------------------------------------------------------\\


    @Override
    public void login(Admin admin, ObserverInterface client) throws SwimmingException {
        initializeConnection();
        AdminDTO a = DTOUtils.getDTO(admin);

        Request req = new Request.Builder().type(RequestType.LOGIN).data(a).build();
        sendRequest(req);

        Response response = readResponse();

        if(response.getType() == ResponseType.ERROR){
            String error = response.getData().toString();
            closeConnection();
            throw new SwimmingException(error);
        }
        else if(response.getType() == ResponseType.OK){
            this.client = client;
            System.out.println("LogIn OK!");
        }
    }

    @Override
    public Iterable<Tuple3> findTuple3_table() throws SwimmingException {

        Request req = new Request.Builder().type(RequestType.UPLOAD_TABLE).build();
        sendRequest(req);

        Response response = readResponse();

        if(response.getType() == ResponseType.ERROR){
            String error = response.getData().toString();
            closeConnection();
            throw new SwimmingException(error);
        }

        else {
           return (Iterable<Tuple3>)response.getData();
        }

    }

    @Override
    public Iterable<ParticipationDTO> findAllParticipationByDistStyle(DistEnum dist, StyleEnum style) throws SwimmingException {
        Contest contest = new Contest(dist, style);
        Request req = new Request.Builder().type(RequestType.SEARCH).data(contest).build();
        sendRequest(req);

        Response response = readResponse();

        if(response.getType() == ResponseType.ERROR){
            String error = response.getData().toString();
            closeConnection();
            throw new SwimmingException(error);
        }

        else {
            return (Iterable<ParticipationDTO>)response.getData();
        }
    }


    public Optional<Contest> findContestId(DistEnum dist, StyleEnum style) throws SwimmingException {
        return Optional.empty();
    }




    @Override
    public void logout(Admin admin) throws SwimmingException {
        AdminDTO adminDTO = DTOUtils.getDTO(admin);
        Request req = new Request.Builder().type(RequestType.LOGOUT).data(adminDTO).build();
        sendRequest(req);

        Response response = readResponse();
        closeConnection();

        if (response.getType()== ResponseType.ERROR){
            String err = response.getData().toString();
            throw new SwimmingException(err);
        }
        else{
            System.out.println("LogOut OK!");
        }
    }



    @Override
    public void addParticipantandParticipation(Iterable<Tuple3> selectedItems, Participant participant) throws SwimmingException {
        System.out.println("proxy");
        ParticipantContestsDTO participantContestsDTO = DTOUtils.getDTO(participant,selectedItems);
        Request req = new Request.Builder().type(RequestType.ADD_PARTICIPANT).data(participantContestsDTO).build();
        sendRequest(req);

        Response response = readResponse();

        if(response.getType() == ResponseType.ERROR){
            String error = response.getData().toString();
            closeConnection();
            throw new SwimmingException(error);
        }

    }
    private void handleUpdate(Response response){
        if(response.getType() == ResponseType.UPDATE){
            System.out.println("Am primit UPDATE");

            try{
                Iterable<Tuple3> allContests = (Iterable<Tuple3>)response.getData();
                client.updateContests(allContests);
            }
            catch (SwimmingException e){
                e.printStackTrace();
            }
        }
    }
}
