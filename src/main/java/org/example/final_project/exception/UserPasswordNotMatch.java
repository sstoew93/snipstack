package org.example.final_project.exception;

public class UserPasswordNotMatch extends RuntimeException {

    public UserPasswordNotMatch(String message) {
        super(message);
    }

    public UserPasswordNotMatch(String message, Throwable cause) {
        super(message, cause);
    }
}
