package edu.ucsb.cs156.courses.controllers;

import edu.ucsb.cs156.courses.errors.EntityNotFoundException;
import edu.ucsb.cs156.courses.errors.NameAndQuarterExistsException;
import edu.ucsb.cs156.courses.errors.BadEnrollCdException;
import edu.ucsb.cs156.courses.errors.CharLimitExceededException;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;

import edu.ucsb.cs156.courses.models.CurrentUser;
import edu.ucsb.cs156.courses.services.CurrentUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@Slf4j
public abstract class ApiController {
  @Autowired
  private CurrentUserService currentUserService;

  protected CurrentUser getCurrentUser() {
    return currentUserService.getCurrentUser();
  }

  protected Object genericMessage(String message) {
    return Map.of("message", message);
  }

  @ExceptionHandler({ CharLimitExceededException.class, NameAndQuarterExistsException.class })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Object handleCustomExceptions(Throwable e) {
    return Map.of(
      "type", e.getClass().getSimpleName(),
      "message", e.getMessage()
    );
  }

  @ExceptionHandler({ EntityNotFoundException.class, BadEnrollCdException.class })
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Object handleGenericException(Throwable e) {
    return Map.of(
      "type", e.getClass().getSimpleName(),
      "message", e.getMessage()
    );
  }
}
