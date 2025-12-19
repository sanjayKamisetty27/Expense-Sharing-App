package com.example.expensesharing.service;

import com.example.expensesharing.model.Expense;
import com.example.expensesharing.model.ExpenseShare;
import com.example.expensesharing.model.Settlement;
import com.example.expensesharing.model.User;
import com.example.expensesharing.repository.ExpenseRepository;
import com.example.expensesharing.repository.SettlementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BalanceService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private SettlementRepository settlementRepository;

    public Map<User, Map<User, BigDecimal>> calculateBalances(Long groupId) {
        List<Expense> expenses = expenseRepository.findByGroupId(groupId);
        Map<User, Map<User, BigDecimal>> balances = new HashMap<>();

        // Add balances from expenses
        for (Expense expense : expenses) {
            User paidBy = expense.getPaidBy();
            for (ExpenseShare share : expense.getShares()) {
                User user = share.getUser();
                if (!user.equals(paidBy)) {
                    addBalance(balances, user, paidBy, share.getAmount());
                }
            }
        }

        // Subtract settlements from balances
        List<Settlement> settlements = settlementRepository.findByGroupId(groupId);
        for (Settlement settlement : settlements) {
            User paidBy = settlement.getPaidBy();   // The debtor who paid
            User paidTo = settlement.getPaidTo();   // The creditor who received
            // Settlement reduces what paidBy owes to paidTo
            subtractBalance(balances, paidBy, paidTo, settlement.getAmount());
        }
        
        // Simplify balances (basic simplification: A->B 10, B->A 5 => A->B 5)
        // This is a simplified version. Full graph simplification is more complex.
        simplifyBalances(balances);

        return balances;
    }

    private void addBalance(Map<User, Map<User, BigDecimal>> balances, User from, User to, BigDecimal amount) {
        balances.putIfAbsent(from, new HashMap<>());
        Map<User, BigDecimal> fromBalances = balances.get(from);
        fromBalances.put(to, fromBalances.getOrDefault(to, BigDecimal.ZERO).add(amount));
    }

    private void subtractBalance(Map<User, Map<User, BigDecimal>> balances, User from, User to, BigDecimal amount) {
        if (balances.containsKey(from) && balances.get(from).containsKey(to)) {
            Map<User, BigDecimal> fromBalances = balances.get(from);
            BigDecimal currentBalance = fromBalances.get(to);
            BigDecimal newBalance = currentBalance.subtract(amount);
            
            if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
                fromBalances.remove(to);
                if (fromBalances.isEmpty()) {
                    balances.remove(from);
                }
            } else {
                fromBalances.put(to, newBalance);
            }
        }
    }
    
    private void simplifyBalances(Map<User, Map<User, BigDecimal>> balances) {
        // Create a copy of keys to avoid ConcurrentModificationException
        List<User> debtors = new java.util.ArrayList<>(balances.keySet());
        
        for (User debtor : debtors) {
            if (!balances.containsKey(debtor)) continue;
            
            List<User> creditors = new java.util.ArrayList<>(balances.get(debtor).keySet());
            for (User creditor : creditors) {
                if (!balances.get(debtor).containsKey(creditor)) continue;
                
                if (balances.containsKey(creditor) && balances.get(creditor).containsKey(debtor)) {
                    BigDecimal debt = balances.get(debtor).get(creditor);
                    BigDecimal credit = balances.get(creditor).get(debtor);
                    
                    if (debt.compareTo(credit) > 0) {
                        balances.get(debtor).put(creditor, debt.subtract(credit));
                        balances.get(creditor).remove(debtor);
                    } else if (credit.compareTo(debt) > 0) {
                        balances.get(creditor).put(debtor, credit.subtract(debt));
                        balances.get(debtor).remove(creditor);
                    } else {
                        // Equal amounts - remove both
                        balances.get(debtor).remove(creditor);
                        balances.get(creditor).remove(debtor);
                    }
                }
            }
        }
        
        // Remove empty entries
        balances.entrySet().removeIf(e -> e.getValue().isEmpty());
    }
}
