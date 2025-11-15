package com.app.dto;

import java.time.LocalDate;
import java.util.List;

public class ExpenseRequest {
    private double amount;
    private String description;
    private int payerId;
    private LocalDate date; // ISO yyyy-MM-dd
    private List<Integer> participantIds; // members to split with (equal)
    private String splitType; // equal|percentage|custom (currently equal only)

    public ExpenseRequest() {}

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPayerId() { return payerId; }
    public void setPayerId(int payerId) { this.payerId = payerId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public List<Integer> getParticipantIds() { return participantIds; }
    public void setParticipantIds(List<Integer> participantIds) { this.participantIds = participantIds; }

    public String getSplitType() { return splitType; }
    public void setSplitType(String splitType) { this.splitType = splitType; }
}
