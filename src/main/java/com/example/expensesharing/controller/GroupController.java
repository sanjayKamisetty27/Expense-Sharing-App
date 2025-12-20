package com.example.expensesharing.controller;

import com.example.expensesharing.dto.DeletionResult;
import com.example.expensesharing.exception.GroupDeletionException;
import com.example.expensesharing.model.Group;
import com.example.expensesharing.service.GroupService;
import com.example.expensesharing.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/groups")
public class GroupController {
    
    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);
    
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
    public String createGroup(@ModelAttribute Group group, RedirectAttributes redirectAttributes) {
        groupService.createGroup(group);
        redirectAttributes.addFlashAttribute("successMessage", "Group '" + group.getName() + "' created successfully!");
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
    public String addUserToGroup(@PathVariable("groupId") Long groupId, 
                                  @RequestParam("userId") Long userId,
                                  RedirectAttributes redirectAttributes) {
        groupService.addUserToGroup(groupId, userId);
        redirectAttributes.addFlashAttribute("successMessage", "Member added successfully!");
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
    public String updateGroup(@PathVariable("groupId") Long groupId, 
                               @RequestParam("name") String name,
                               RedirectAttributes redirectAttributes) {
        groupService.updateGroup(groupId, name);
        redirectAttributes.addFlashAttribute("successMessage", "Group updated successfully!");
        return "redirect:/groups/" + groupId;
    }

    /**
     * Delete a group with proper validation and user feedback.
     * 
     * @param groupId the ID of the group to delete
     * @param redirectAttributes for flash messages
     * @return redirect to groups list with success or error message
     */
    @PostMapping("/{groupId}/delete")
    public String deleteGroup(@PathVariable("groupId") Long groupId, 
                               RedirectAttributes redirectAttributes) {
        logger.info("Delete request received for group ID: {}", groupId);
        
        DeletionResult result = groupService.deleteGroup(groupId);
        
        if (result.success()) {
            redirectAttributes.addFlashAttribute("successMessage", result.message());
            logger.info("Group deletion successful: {}", result.message());
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", result.message());
            logger.warn("Group deletion returned failure: {}", result.message());
        }
        
        return "redirect:/groups";
    }

    @PostMapping("/{groupId}/remove-user")
    public String removeUserFromGroup(@PathVariable("groupId") Long groupId, 
                                       @RequestParam("userId") Long userId,
                                       RedirectAttributes redirectAttributes) {
        groupService.removeUserFromGroup(groupId, userId);
        redirectAttributes.addFlashAttribute("successMessage", "Member removed from group.");
        return "redirect:/groups/" + groupId;
    }
}
