package com.example.expensesharing.service;

import com.example.expensesharing.model.Group;
import com.example.expensesharing.model.User;
import com.example.expensesharing.repository.GroupRepository;
import com.example.expensesharing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

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

    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }

    public void removeUserFromGroup(Long groupId, Long userId) {
        Group group = getGroupById(groupId);
        group.getUsers().removeIf(user -> user.getId().equals(userId));
        groupRepository.save(group);
    }
}

