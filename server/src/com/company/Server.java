package com.company;

import com.company.model.Deposit;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class Server  {

    static Set<Deposit> deposits;
    static String filename = "deposits.dat";
    static List<ClientThread> clients;

    private static Set<Deposit> deserialize() {
        Set<Deposit> deposits = null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            deposits = (Set<Deposit>) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        return deposits;
    }

    public synchronized static void updateDepositsInAllThreads(Set<Deposit> deposits) {
        if (clients.size() > 0) {
            clients.forEach(clientThread -> clientThread.setDeposits(deposits));
        }
    }

    public static void main(String[] args) {

        clients = new ArrayList<>();
        deposits = deserialize();

        try {
            ServerSocket serverSocket = new ServerSocket(1555);

            int clientId = 0;
            while (true) {
                Socket client = null;
                while (client == null) {
                    client = serverSocket.accept();
                }
                clients.add(new ClientThread(client, deposits, clientId++));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
