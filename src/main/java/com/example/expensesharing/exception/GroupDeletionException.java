package com.example.expensesharing.exception;

/**
 * Custom exception thrown when group deletion fails due to business rule violations.
 * Examples: group has active expenses, unsettled balances, or doesn't exist.
 */
public class GroupDeletionException extends RuntimeException {
    
    public GroupDeletionException(String message) {
        super(message);
    }
    
    public GroupDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}
