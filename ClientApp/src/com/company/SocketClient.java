package com.company;

import com.company.listeners.SocketListener;
import com.company.model.Deposit;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import static com.company.Constants.*;

/**
 * Created by sails on 02.04.2017.
 */
public class SocketClient implements SocketListener {

    private Socket socket;

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private SocketListener socketListener;
    private String host;
    private int port;

    public SocketClient(SocketListener socketListener, String host, int port) {
        this.host = host;
        this.port = port;
        this.socketListener = socketListener;
        System.out.println("socketClient created");
    }

    public void connect() {
        System.out.println("connecting...");
        try {
            socket = new Socket(host, port);
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());

            connectionStatusChanged(true);

        } catch (IOException e) {
            connectionStatusChanged(false);
        }

        new SocketReaderThread().start();
    }

    public boolean checkIsConnected() {
        return null != socket && socket.isConnected() && null != inputStream && null != outputStream;
    }

    public void sendRequest(String request) {

        System.out.println("request = " + request);

        if (!checkIsConnected()) {
            connectionStatusChanged(false);
            return;
        }

        try {
            outputStream.reset();
            outputStream.writeUTF(request);

            outputStream.flush();
        } catch (IOException e) {
            connectionStatusChanged(false);
        }
    }

    public void sendRequest(String request, Deposit deposit) {
        System.out.println("request = " + request + " DEPOSIT = " + deposit);
        if (!checkIsConnected()) {
            connectionStatusChanged(false);
            return;
        }

        if (null == deposit) return;

        System.out.println(deposit);

        try {
            outputStream.reset();
            outputStream.writeUTF(request);
            outputStream.writeObject(deposit);

            outputStream.flush();
        } catch (IOException e) {
            connectionStatusChanged(false);
        }
    }

    @Override
    public void onUpdateTable(List<Deposit> deposits) {
        javafx.application.Platform.runLater(new Runnable() {
            @Override
            public void run() {
                socketListener.onUpdateTable(deposits);
            }
        });
    }

    @Override
    public void onUpdateTable(Deposit deposit) {
        javafx.application.Platform.runLater(() -> socketListener.onUpdateTable(deposit));
    }

    @Override
    public void onMessageReceived(String msg) {
        javafx.application.Platform.runLater(() -> socketListener.onMessageReceived(msg));
    }

    @Override
    public void connectionStatusChanged(boolean isConnected) {

        if (!isConnected)
            socket = null;

        javafx.application.Platform.runLater(() -> {
            System.out.println("connectionStatusChanged started");
            socketListener.connectionStatusChanged(isConnected);
            System.out.println("connectionStatusChanged finished");
        });
    }

    class SocketReaderThread extends Thread {

        @Override
        public void run() {

            System.out.println("socketReaderThread started");

            try {
                while (null != socket && socket.isConnected()) {

                    String command = inputStream.readUTF();
                    String response = inputStream.readUTF();

                    onMessageReceived(response);
                    if (!response.equals(RESULT_OK)) {
                        continue;
                    }

                    if (command.equals(Constants.COMMAND_LIST) ||
                            command.startsWith(Constants.COMMAND_INFO_DEPOSITOR) ||
                            command.startsWith(Constants.COMMAND_SHOW_BANK) ||
                            command.startsWith(Constants.COMMAND_SHOW_TYPE)) {

                        List<Deposit> deposits = (List<Deposit>) inputStream.readObject();
                        onUpdateTable(deposits);

                    } else if (command.equals(Constants.COMMAND_SUM)) {
                        long sum = inputStream.readLong();
                        onMessageReceived("Sum = " + sum);

                    } else if (command.equals(Constants.COMMAND_COUNT)) {
                        int count = inputStream.readInt();
                        onMessageReceived("Count = " + count);

                    } else if (command.startsWith(Constants.COMMAND_INFO_ACCOUNT)) {
                        Deposit deposit = (Deposit) inputStream.readObject();
                        onUpdateTable(deposit);

                    } else if (command.startsWith(Constants.COMMAND_ADD) ||
                            command.startsWith(Constants.COMMAND_DELETE)) {
                        String res = inputStream.readUTF();
                        onMessageReceived(res);

                    } else {
                        onMessageReceived("Unknown answer");
                    }
                }
            } catch (IOException e) {
                connectionStatusChanged(false);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            System.out.println("socketReaderThread finished");

        }
    }

}
