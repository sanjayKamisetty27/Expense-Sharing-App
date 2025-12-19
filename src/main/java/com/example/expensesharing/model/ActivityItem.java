package com.example.expensesharing.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO representing an activity item (expense or settlement) for the history view.
 */
public class ActivityItem {
    
    public enum ActivityType {
        EXPENSE,
        SETTLEMENT
    }
    
    private Long id;
    private ActivityType type;
    private String description;
    private BigDecimal amount;
    private User paidBy;
    private User paidTo;  // Only for settlements
    private LocalDateTime createdAt;
    private SplitType splitType;  // Only for expenses

    // Constructor for Expense
    public ActivityItem(Expense expense) {
        this.id = expense.getId();
        this.type = ActivityType.EXPENSE;
        this.description = expense.getDescription();
        this.amount = expense.getAmount();
        this.paidBy = expense.getPaidBy();
        this.paidTo = null;
        this.createdAt = expense.getCreatedAt() != null ? expense.getCreatedAt() : LocalDateTime.now();
        this.splitType = expense.getSplitType();
    }

    // Constructor for Settlement
    public ActivityItem(Settlement settlement) {
        this.id = settlement.getId();
        this.type = ActivityType.SETTLEMENT;
        this.description = "Settlement";
        this.amount = settlement.getAmount();
        this.paidBy = settlement.getPaidBy();
        this.paidTo = settlement.getPaidTo();
        this.createdAt = settlement.getSettledAt();
        this.splitType = null;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public ActivityType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public User getPaidBy() {
        return paidBy;
    }

    public User getPaidTo() {
        return paidTo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public SplitType getSplitType() {
        return splitType;
    }

    public boolean isExpense() {
        return type == ActivityType.EXPENSE;
    }

    public boolean isSettlement() {
        return type == ActivityType.SETTLEMENT;
    }
}
