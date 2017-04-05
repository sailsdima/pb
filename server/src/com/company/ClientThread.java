package com.company;

import com.company.model.Deposit;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

import static com.company.Commands.*;

/**
 * Created by sails on 31.03.2017.
 */
public class ClientThread extends Thread {

    int clientId;
    Socket socket;
    String filename = "deposits.dat";
    Set<Deposit> deposits;

    public ClientThread(Socket socket, Set<Deposit> deposits, int clientId) {
        log("New client connected. Id : " + clientId);
        this.socket = socket;
        this.deposits = deposits;
        this.clientId = clientId;

        start();
    }

    private void serialize(Set<Deposit> deposits) {

        log("Client " + clientId + ": serializing data...");

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(deposits);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setDeposits(Set<Deposit> deposits) {
        this.deposits = deposits;
    }

    @Override
    public void run() {

        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            while (socket.isConnected()) {
                log("Client " + clientId + ": waiting for command");


                String command = inputStream.readUTF().trim();

                log("Client " + clientId + ": command received (" + command + ")");

                outputStream.writeUTF(command);

                if (command.equals(COMMAND_LIST)) {
                    outputStream.writeUTF(RESULT_OK);
                    outputStream.writeObject(new ArrayList<>(deposits));

                } else if (command.equals(COMMAND_SUM)) {

                    long sum = findDepositsAmountSum();
                    outputStream.writeUTF(RESULT_OK);
                    outputStream.writeLong(sum);

                } else if (command.equals(COMMAND_COUNT)) {
                    outputStream.writeUTF(RESULT_OK);
                    outputStream.writeInt(deposits.size());

                } else if (command.startsWith(COMMAND_INFO_ACCOUNT + " ") && command.split(" ").length == 3) {
                    try {
                        int accountId = Integer.parseInt(command.substring(COMMAND_INFO_ACCOUNT.length()).trim());
                        Deposit deposit = getDepositById(accountId);

                        outputStream.writeUTF(deposit != null ? RESULT_OK : "No account with such ID");

                        if (deposit != null)
                            outputStream.writeObject(deposit);

                    } catch (NumberFormatException e) {
                        outputStream.writeUTF("Account id parameter must be a number.");
                    }

                } else if (command.startsWith(COMMAND_INFO_DEPOSITOR + " ")) {
                    String depositor = command.substring(COMMAND_INFO_DEPOSITOR.length()).trim();
                    List<Deposit> deposits = getDepositsByDepositor(depositor);
                    outputStream.writeUTF(deposits.size() > 0 ? RESULT_OK : "There is no deposits owned by " + depositor);
                    if (deposits.size() > 0)
                        outputStream.writeObject(deposits);

                } else if (command.startsWith(COMMAND_SHOW_TYPE + " ")) {
                    String type = command.substring(COMMAND_SHOW_TYPE.length()).trim();

                    List<Deposit> deposits = getDepositsByType(type);
                    outputStream.writeUTF(deposits.size() > 0 ? RESULT_OK : "There is no deposits with type '" + type + "'");
                    if (deposits.size() > 0)
                        outputStream.writeObject(deposits);

                } else if (command.startsWith(COMMAND_SHOW_BANK + " ")) {
                    String bankName = command.substring(COMMAND_SHOW_BANK.length()).trim();

                    List<Deposit> deposits = getDepositsByBankName(bankName);
                    outputStream.writeUTF(deposits.size() > 0 ? RESULT_OK : "There is no deposits with bank '" + bankName + "'");
                    if (deposits.size() > 0)
                        outputStream.writeObject(deposits);


                } else if (command.equals(COMMAND_ADD)) {
                    Deposit deposit = (Deposit) inputStream.readObject();
                    outputStream.writeUTF(RESULT_OK);
                    outputStream.writeUTF(addDeposit(deposit));

                } else if (command.startsWith(COMMAND_DELETE + " ") && command.split(" ").length == 2) {
                    try {
                        Integer accountId = Integer.parseInt(command.substring(COMMAND_DELETE.length()).trim());
                        outputStream.writeUTF(deleteDeposit(accountId));
                    } catch (NumberFormatException e) {
                        outputStream.writeUTF("Account id parameter must be a number.");
                    }
                } else {
                    outputStream.writeUTF("unknown command or missing parameters: \n" + command);
                }

                outputStream.flush();


                log("Client " + clientId + ": response sent");
            }
        } catch (SocketException e) {

            log("Client " + clientId + ": connection reset");

            try {
                socket.close();
                log("Client " + clientId + ": socket closed");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private long findDepositsAmountSum() {
        long sum = 0;
        for (Deposit deposit : deposits) {
            sum += deposit.getAmountOnDeposit();
        }
        return sum;
    }

    private Deposit getDepositById(int id) {
        for (Deposit deposit : deposits) {
            if (deposit.getAccountId() == id)
                return deposit;
        }
        return null;
    }

    private List<Deposit> getDepositsByDepositor(String depositor) {
        List<Deposit> deps = new ArrayList<>();
        for (Deposit deposit : deposits) {
            if (deposit.getDepositor().equalsIgnoreCase(depositor))
                deps.add(deposit);
        }
        return deps;
    }

    private List<Deposit> getDepositsByType(String type) {
        List<Deposit> deps = new ArrayList<>();
        for (Deposit deposit : deposits) {
            if (deposit.getType().equalsIgnoreCase(type))
                deps.add(deposit);
        }
        return deps;
    }

    private List<Deposit> getDepositsByBankName(String bankName) {
        List<Deposit> deps = new ArrayList<>();
        for (Deposit deposit : deposits) {
            if (deposit.getName().equalsIgnoreCase(bankName))
                deps.add(deposit);
        }
        return deps;
    }

    private String addDeposit(Deposit deposit) {

        if(deposit.getAmountOnDeposit() <= 0)
            return "Amount on deposit must be positive.";
        if(deposit.getProfitability() <= 0)
            return "Profitability must be positive.";
        if(deposit.getTimeConstraints() < 0)
            return "Time constaints must be positive.";

        int oldSize = deposits.size();

        deposits.add(deposit);

        if (deposits.size() == oldSize)
            return "Deposit with same ID already exists.";

        serialize(deposits);
        Server.updateDepositsInAllThreads(deposits);

        return "Added";
    }

    private String deleteDeposit(int id) {
        int oldSize = deposits.size();

        for (Deposit deposit : deposits) {
            if (deposit.getAccountId() == id) {
                deposits.remove(deposit);
                break;
            }
        }

        if (deposits.size() == oldSize)
            return "No deposit with such ID.";

        serialize(deposits);
        Server.updateDepositsInAllThreads(deposits);

        return "Deleted";
    }

    private void log(String msg) {
        System.out.println(msg);
    }

}
