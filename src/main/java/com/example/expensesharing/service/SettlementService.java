package com.example.expensesharing.service;

import com.example.expensesharing.model.Group;
import com.example.expensesharing.model.Settlement;
import com.example.expensesharing.model.User;
import com.example.expensesharing.repository.GroupRepository;
import com.example.expensesharing.repository.SettlementRepository;
import com.example.expensesharing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SettlementService {

    @Autowired
    private SettlementRepository settlementRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    public Settlement recordSettlement(Long groupId, Long paidByUserId, Long paidToUserId, BigDecimal amount) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User paidBy = userRepository.findById(paidByUserId)
                .orElseThrow(() -> new RuntimeException("Payer user not found"));
        User paidTo = userRepository.findById(paidToUserId)
                .orElseThrow(() -> new RuntimeException("Receiver user not found"));

        if (paidByUserId.equals(paidToUserId)) {
            throw new RuntimeException("Cannot settle with yourself");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Settlement amount must be positive");
        }

        Settlement settlement = new Settlement(paidBy, paidTo, amount, group);
        return settlementRepository.save(settlement);
    }

    public List<Settlement> getSettlementsByGroup(Long groupId) {
        return settlementRepository.findByGroupId(groupId);
    }

    public void deleteSettlement(Long settlementId) {
        settlementRepository.deleteById(settlementId);
    }

    public void deleteAllByGroup(Long groupId) {
        List<Settlement> settlements = settlementRepository.findByGroupId(groupId);
        settlementRepository.deleteAll(settlements);
    }
}

