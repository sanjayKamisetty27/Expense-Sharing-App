package com.example.expensesharing.service;

import com.example.expensesharing.model.ActivityItem;
import com.example.expensesharing.model.Expense;
import com.example.expensesharing.model.Settlement;
import com.example.expensesharing.repository.ExpenseRepository;
import com.example.expensesharing.repository.SettlementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private SettlementRepository settlementRepository;

    /**
     * Get all activities (expenses and settlements) for a group, sorted by date descending.
     */
    public List<ActivityItem> getGroupActivities(Long groupId) {
        List<ActivityItem> activities = new ArrayList<>();

        // Add expenses
        List<Expense> expenses = expenseRepository.findByGroupId(groupId);
        for (Expense expense : expenses) {
            activities.add(new ActivityItem(expense));
        }

        // Add settlements
        List<Settlement> settlements = settlementRepository.findByGroupId(groupId);
        for (Settlement settlement : settlements) {
            activities.add(new ActivityItem(settlement));
        }

        // Sort by date descending (most recent first)
        activities.sort(Comparator.comparing(ActivityItem::getCreatedAt).reversed());

        return activities;
    }
}
