package com.example.expensesharing.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "settlements")
public class Settlement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paid_by_user_id")
    private User paidBy; // The debtor who is paying

    @ManyToOne
    @JoinColumn(name = "paid_to_user_id")
    private User paidTo; // The creditor who receives the payment

    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    private LocalDateTime settledAt;

    public Settlement(User paidBy, User paidTo, BigDecimal amount, Group group) {
        this.paidBy = paidBy;
        this.paidTo = paidTo;
        this.amount = amount;
        this.group = group;
        this.settledAt = LocalDateTime.now();
    }
}
