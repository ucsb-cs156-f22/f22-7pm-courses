package edu.ucsb.cs156.courses.errors;

public class CharLimitExceededException extends RuntimeException {
  public CharLimitExceededException(String name) {
    super("Name: %s too long (Name must be no more than 15 characters)".formatted(name.toString()));
  }
}