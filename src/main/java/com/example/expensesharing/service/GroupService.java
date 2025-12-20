package com.example.expensesharing.service;

import com.example.expensesharing.dto.DeletionResult;
import com.example.expensesharing.exception.GroupDeletionException;
import com.example.expensesharing.model.Group;
import com.example.expensesharing.model.User;
import com.example.expensesharing.repository.ExpenseRepository;
import com.example.expensesharing.repository.GroupRepository;
import com.example.expensesharing.repository.SettlementRepository;
import com.example.expensesharing.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GroupService {
    
    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);
    
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private SettlementRepository settlementRepository;
    
    @Autowired
    private BalanceService balanceService;

    public Group createGroup(Group group) {
        return groupRepository.save(group);
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroupById(Long id) {
        return groupRepository.findById(id).orElseThrow(() -> new RuntimeException("Group not found"));
    }

    public void addUserToGroup(Long groupId, Long userId) {
        Group group = getGroupById(groupId);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        group.getUsers().add(user);
        groupRepository.save(group);
    }

    public Group updateGroup(Long id, String name) {
        Group group = getGroupById(id);
        group.setName(name);
        return groupRepository.save(group);
    }

    /**
     * Safely delete a group with validation.
     * 
     * @param groupId the ID of the group to delete
     * @return DeletionResult with success status and message
     * @throws GroupDeletionException if group cannot be deleted due to business rules
     */
    @Transactional
    public DeletionResult deleteGroup(Long groupId) {
        logger.info("Attempting to delete group with ID: {}", groupId);
        
        // 1. Check if group exists
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            logger.warn("Group deletion failed: Group with ID {} not found", groupId);
            throw new GroupDeletionException("Group not found. It may have already been deleted.");
        }
        
        Group group = groupOpt.get();
        String groupName = group.getName();
        
        // 2. Check for active expenses
        long expenseCount = expenseRepository.countByGroupId(groupId);
        if (expenseCount > 0) {
            logger.warn("Group deletion blocked: Group '{}' (ID: {}) has {} active expense(s)", 
                    groupName, groupId, expenseCount);
            throw new GroupDeletionException(
                    "Group '" + groupName + "' cannot be deleted because it contains " + 
                    expenseCount + " active expense(s). Please remove all expenses first.");
        }
        
        // 3. Check for unsettled balances
        Map<User, Map<User, BigDecimal>> balances = balanceService.calculateBalances(groupId);
        if (!balances.isEmpty()) {
            logger.warn("Group deletion blocked: Group '{}' (ID: {}) has unsettled balances", 
                    groupName, groupId);
            throw new GroupDeletionException(
                    "Group '" + groupName + "' cannot be deleted because it has unsettled balances. " +
                    "Please settle all dues first.");
        }
        
        // 4. Delete settlements (if any exist from previously settled balances)
        settlementRepository.deleteByGroupId(groupId);
        logger.debug("Deleted settlements for group ID: {}", groupId);
        
        // 5. Delete the group
        groupRepository.delete(group);
        
        logger.info("Successfully deleted group '{}' (ID: {})", groupName, groupId);
        return DeletionResult.success("Group '" + groupName + "' has been successfully deleted.");
    }

    public void removeUserFromGroup(Long groupId, Long userId) {
        Group group = getGroupById(groupId);
        group.getUsers().removeIf(user -> user.getId().equals(userId));
        groupRepository.save(group);
    }
}
