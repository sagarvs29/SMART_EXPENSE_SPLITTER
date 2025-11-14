package com.app;

import java.time.LocalDateTime;

/**
 * The Model Class for the 'settlements' table.
 */
public class Settlement {
    
    private final long settlementId;
    private final int groupId; // Group the settlement belongs to (NO LONGER HARDCODED)
    private final int payerId; // The user making the payment (debtor)
    private final int receiverId; // The user receiving the payment (creditor)
    private final double amount;
    private final LocalDateTime settlementDate;

    /**
     * Constructor used when creating a new settlement record (before saving to DB).
     */
    public Settlement(int groupId, int payerId, int receiverId, double amount) {
        this.settlementId = -1;
        this.groupId = groupId; // NOW ACCEPTS THE GENERATED GROUP ID
        this.payerId = payerId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.settlementDate = LocalDateTime.now();
    }

    /**
     * Constructor used when retrieving a settlement record from the database.
     */
    public Settlement(long settlementId, int groupId, int payerId, int receiverId, double amount, LocalDateTime settlementDate) {
        this.settlementId = settlementId;
        this.groupId = groupId;
        this.payerId = payerId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.settlementDate = settlementDate;
    }

    // --- Getter Methods ---
    public long getSettlementId() { return settlementId; }
    public int getGroupId() { return groupId; }
    public int getPayerId() { return payerId; }
    public int getReceiverId() { return receiverId; }
    public double getAmount() { return amount; }
    public LocalDateTime getSettlementDate() { return settlementDate; }
}