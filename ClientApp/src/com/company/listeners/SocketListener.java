package com.company.listeners;

import com.company.model.Deposit;

import java.util.List;

/**
 * Created by sails on 02.04.2017.
 */
public interface SocketListener {

    public void onUpdateTable(List<Deposit> deposits);
    public void onUpdateTable(Deposit deposit);
    public void onMessageReceived(String msg);
    public void connectionStatusChanged(boolean isConnected);

}
