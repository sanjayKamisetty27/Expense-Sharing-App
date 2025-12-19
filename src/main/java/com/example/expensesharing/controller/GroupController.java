package com.example.expensesharing.controller;

import com.example.expensesharing.model.Group;
import com.example.expensesharing.service.GroupService;
import com.example.expensesharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/groups")
public class GroupController {
    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listGroups(Model model) {
        model.addAttribute("groups", groupService.getAllGroups());
        return "groups/list-groups";
    }

    @GetMapping("/new")
    public String showCreateGroupForm(Model model) {
        model.addAttribute("group", new Group());
        return "groups/create-group";
    }

    @PostMapping
    public String createGroup(@ModelAttribute Group group) {
        groupService.createGroup(group);
        return "redirect:/groups";
    }

    @GetMapping("/{groupId}")
    public String viewGroup(@PathVariable("groupId") Long groupId, Model model) {
        Group group = groupService.getGroupById(groupId);
        if (group == null) {
            return "redirect:/groups";
        }
        model.addAttribute("group", group);
        model.addAttribute("allUsers", userService.getAllUsers());
        return "groups/group-details";
    }

    @PostMapping("/{groupId}/add-user")
    public String addUserToGroup(@PathVariable("groupId") Long groupId, @RequestParam("userId") Long userId) {
        groupService.addUserToGroup(groupId, userId);
        return "redirect:/groups/" + groupId;
    }

    @GetMapping("/{groupId}/edit")
    public String showEditGroupForm(@PathVariable("groupId") Long groupId, Model model) {
        Group group = groupService.getGroupById(groupId);
        if (group == null) {
            return "redirect:/groups";
        }
        model.addAttribute("group", group);
        return "groups/edit-group";
    }

    @PostMapping("/{groupId}/update")
    public String updateGroup(@PathVariable("groupId") Long groupId, @RequestParam("name") String name) {
        groupService.updateGroup(groupId, name);
        return "redirect:/groups/" + groupId;
    }

    @PostMapping("/{groupId}/delete")
    public String deleteGroup(@PathVariable("groupId") Long groupId) {
        groupService.deleteGroup(groupId);
        return "redirect:/groups";
    }

    @PostMapping("/{groupId}/remove-user")
    public String removeUserFromGroup(@PathVariable("groupId") Long groupId, @RequestParam("userId") Long userId) {
        groupService.removeUserFromGroup(groupId, userId);
        return "redirect:/groups/" + groupId;
    }
}

