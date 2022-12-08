package edu.ucsb.cs156.courses.errors;

public class NameAndQuarterExistsException extends RuntimeException{
    public NameAndQuarterExistsException(String name, String quarter) {
        super("Name and Quarter are invalid (There already exists a personal schedule named %s during %s)"
        .formatted(name.toString(), quarter.toString()));
    }
}
