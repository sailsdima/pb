package com.company.model;

import java.io.Serializable;

/**
 * Created by sails on 31.03.2017.
 */
public class Deposit implements Serializable{

    String name;
    String country;
    String type;
    String depositor;
    int accountId;
    long amountOnDeposit;
    double profitability;
    int timeConstraints;



    public Deposit(String name, String country, String type, String depositor, int accountId, long amountOnDeposit, double profitability, int timeConstraints) {
        this.name = name;
        this.country = country;
        this.type = type;
        this.depositor = depositor;
        this.accountId = accountId;
        this.amountOnDeposit = amountOnDeposit;
        this.profitability = profitability;
        this.timeConstraints = timeConstraints;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getType() {
        return type;
    }

    public String getDepositor() {
        return depositor;
    }

    public int getAccountId() {
        return accountId;
    }

    public long getAmountOnDeposit() {
        return amountOnDeposit;
    }

    public double getProfitability() {
        return profitability;
    }

    public int getTimeConstraints() {
        return timeConstraints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Deposit deposit = (Deposit) o;

        return accountId == deposit.accountId;
    }

    @Override
    public int hashCode() {
        return accountId;
    }

    @Override
    public String toString() {
        return "Deposit{" +
                "name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", type='" + type + '\'' +
                ", depositor='" + depositor + '\'' +
                ", accountId=" + accountId +
                ", amountOnDeposit=" + amountOnDeposit +
                ", profitability=" + profitability +
                ", timeConstraints=" + timeConstraints +
                '}';
    }
}
