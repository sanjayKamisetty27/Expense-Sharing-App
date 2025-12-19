package com.example.expensesharing.controller;

import com.example.expensesharing.model.Expense;
import com.example.expensesharing.model.SplitType;
import com.example.expensesharing.service.ActivityService;
import com.example.expensesharing.service.BalanceService;
import com.example.expensesharing.service.ExpenseService;
import com.example.expensesharing.service.GroupService;
import com.example.expensesharing.service.SettlementService;
import com.example.expensesharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private BalanceService balanceService;

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private ActivityService activityService;

    @GetMapping("/add")
    public String showAddExpenseForm(@RequestParam(required = false) Long groupId, Model model) {
        // Redirect to groups page if no group is selected
        if (groupId == null) {
            return "redirect:/groups";
        }
        model.addAttribute("groups", groupService.getAllGroups());
        model.addAttribute("selectedGroup", groupService.getGroupById(groupId));
        model.addAttribute("splitTypes", SplitType.values());
        return "expenses/add-expense";
    }

    @PostMapping("/add")
    public String addExpense(@RequestParam Long groupId,
                             @RequestParam Long paidByUserId,
                             @RequestParam String description,
                             @RequestParam BigDecimal amount,
                             @RequestParam SplitType splitType,
                             @RequestParam(required = false) Map<String, String> allParams) {
        
        Map<Long, BigDecimal> splits = new HashMap<>();
        if (splitType != SplitType.EQUAL) {
             for (Map.Entry<String, String> entry : allParams.entrySet()) {
                if (entry.getKey().startsWith("split_")) {
                    Long userId = Long.parseLong(entry.getKey().substring(6));
                    BigDecimal splitValue = new BigDecimal(entry.getValue());
                    splits.put(userId, splitValue);
                }
            }
        }

        expenseService.addExpense(groupId, paidByUserId, description, amount, splitType, splits);
        return "redirect:/groups/" + groupId;
    }
    
    @GetMapping("/balances/{groupId}")
    public String viewBalances(@PathVariable Long groupId, Model model) {
        model.addAttribute("balances", balanceService.calculateBalances(groupId));
        model.addAttribute("group", groupService.getGroupById(groupId));
        return "balances/view-balances";
    }

    @GetMapping("/settle/{groupId}")
    public String showSettleDuesForm(@PathVariable Long groupId,
                                     @RequestParam(required = false) Long fromUserId,
                                     @RequestParam(required = false) Long toUserId,
                                     @RequestParam(required = false) BigDecimal amount,
                                     Model model) {
        model.addAttribute("group", groupService.getGroupById(groupId));
        model.addAttribute("balances", balanceService.calculateBalances(groupId));
        model.addAttribute("preselectedFromUserId", fromUserId);
        model.addAttribute("preselectedToUserId", toUserId);
        model.addAttribute("preselectedAmount", amount);
        return "expenses/settle-dues";
    }

    @PostMapping("/settle")
    public String settleDues(@RequestParam Long groupId,
                             @RequestParam Long paidByUserId,
                             @RequestParam Long paidToUserId,
                             @RequestParam BigDecimal amount) {
        settlementService.recordSettlement(groupId, paidByUserId, paidToUserId, amount);
        return "redirect:/expenses/balances/" + groupId;
    }

    @GetMapping("/history/{groupId}")
    public String viewHistory(@PathVariable Long groupId, Model model) {
        model.addAttribute("group", groupService.getGroupById(groupId));
        model.addAttribute("activities", activityService.getGroupActivities(groupId));
        return "expenses/history";
    }

    @PostMapping("/{expenseId}/delete")
    public String deleteExpense(@PathVariable Long expenseId, @RequestParam Long groupId) {
        expenseService.deleteExpense(expenseId);
        return "redirect:/expenses/history/" + groupId;
    }

    @PostMapping("/history/{groupId}/clear")
    public String clearHistory(@PathVariable Long groupId) {
        expenseService.deleteAllByGroup(groupId);
        settlementService.deleteAllByGroup(groupId);
        return "redirect:/expenses/history/" + groupId;
    }

    @PostMapping("/settlements/{settlementId}/delete")
    public String deleteSettlement(@PathVariable Long settlementId, @RequestParam Long groupId) {
        settlementService.deleteSettlement(settlementId);
        return "redirect:/expenses/history/" + groupId;
    }
}




