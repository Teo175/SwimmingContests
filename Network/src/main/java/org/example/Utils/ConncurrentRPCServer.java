package org.example.Utils;



import org.example.RPCProtocol.ClientWorker;
import org.example.interfaces.ServiceInterface;

import java.net.Socket;

public class ConncurrentRPCServer extends AbstractConcurrentServer{
    private ServiceInterface service;

    public ConncurrentRPCServer(int port, ServiceInterface service) {
        super(port);
        this.service = service;
    }

    @Override
    protected Thread createWorker(Socket client) {
        ClientWorker worker = new ClientWorker(service, client);

        return new Thread(worker);
    }

    @Override
    public void stop(){

    }
}
