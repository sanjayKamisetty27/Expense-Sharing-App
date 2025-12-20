package com.example.expensesharing.dto;

/**
 * Result object for deletion operations.
 * Provides feedback on whether operation succeeded and a user-friendly message.
 */
public record DeletionResult(boolean success, String message) {
    
    public static DeletionResult success(String message) {
        return new DeletionResult(true, message);
    }
    
    public static DeletionResult failure(String message) {
        return new DeletionResult(false, message);
    }
}
