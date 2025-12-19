package com.example.expensesharing.service;

import com.example.expensesharing.model.*;
import com.example.expensesharing.repository.ExpenseRepository;
import com.example.expensesharing.repository.GroupRepository;
import com.example.expensesharing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    public Expense addExpense(Long groupId, Long paidByUserId, String description, BigDecimal amount, SplitType splitType, Map<Long, BigDecimal> splits) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));
        User paidBy = userRepository.findById(paidByUserId).orElseThrow(() -> new RuntimeException("User not found"));

        Expense expense = new Expense();
        expense.setGroup(group);
        expense.setPaidBy(paidBy);
        expense.setDescription(description);
        expense.setAmount(amount);
        expense.setSplitType(splitType);

        List<User> participants = group.getUsers();
        if (participants.isEmpty()) {
            throw new RuntimeException("Group has no members");
        }

        switch (splitType) {
            case EQUAL:
                BigDecimal splitAmount = amount.divide(BigDecimal.valueOf(participants.size()), 2, RoundingMode.CEILING);
                for (User user : participants) {
                    expense.getShares().add(new ExpenseShare(user, splitAmount));
                }
                break;
            case EXACT:
                BigDecimal totalExact = BigDecimal.ZERO;
                for (Map.Entry<Long, BigDecimal> entry : splits.entrySet()) {
                    User user = userRepository.findById(entry.getKey()).orElseThrow(() -> new RuntimeException("User not found"));
                    expense.getShares().add(new ExpenseShare(user, entry.getValue()));
                    totalExact = totalExact.add(entry.getValue());
                }
                if (totalExact.compareTo(amount) != 0) {
                    throw new RuntimeException("Exact shares do not sum up to total amount");
                }
                break;
            case PERCENTAGE:
                BigDecimal totalPercent = BigDecimal.ZERO;
                for (Map.Entry<Long, BigDecimal> entry : splits.entrySet()) {
                    User user = userRepository.findById(entry.getKey()).orElseThrow(() -> new RuntimeException("User not found"));
                    BigDecimal shareAmount = amount.multiply(entry.getValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.CEILING);
                    expense.getShares().add(new ExpenseShare(user, shareAmount));
                    totalPercent = totalPercent.add(entry.getValue());
                }
                if (totalPercent.compareTo(BigDecimal.valueOf(100)) != 0) {
                    throw new RuntimeException("Percentages do not sum up to 100");
                }
                break;
        }

        // Set expense reference in shares
        for (ExpenseShare share : expense.getShares()) {
            share.setExpense(expense);
        }

        return expenseRepository.save(expense);
    }

    public List<Expense> getExpensesByGroup(Long groupId) {
        return expenseRepository.findByGroupId(groupId);
    }

    public void deleteExpense(Long expenseId) {
        expenseRepository.deleteById(expenseId);
    }

    public void deleteAllByGroup(Long groupId) {
        List<Expense> expenses = expenseRepository.findByGroupId(groupId);
        expenseRepository.deleteAll(expenses);
    }
}

