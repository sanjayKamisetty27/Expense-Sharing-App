package com.example.expensesharing.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Global exception handler to prevent Whitelabel error pages
 * and provide user-friendly error messages.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handle GroupDeletionException - redirect to groups with error message
     */
    @ExceptionHandler(GroupDeletionException.class)
    public String handleGroupDeletionException(GroupDeletionException ex, RedirectAttributes redirectAttributes) {
        logger.warn("Group deletion failed: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/groups";
    }
    
    /**
     * Handle EntityNotFoundException or similar runtime exceptions for missing entities
     */
    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException ex, RedirectAttributes redirectAttributes) {
        logger.warn("Illegal state exception: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", "Operation failed: " + ex.getMessage());
        return "redirect:/groups";
    }
    
    /**
     * Handle generic RuntimeException - catch-all for unexpected errors
     */
    /**
     * Handle generic Exception - catch-all for unexpected errors
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, RedirectAttributes redirectAttributes) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        // Check if it's a "not found" type error
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found")) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/groups";
        }
        
        // Generic error - redirect to error page
        redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred. Please try again.");
        return "redirect:/groups";
    }
}
