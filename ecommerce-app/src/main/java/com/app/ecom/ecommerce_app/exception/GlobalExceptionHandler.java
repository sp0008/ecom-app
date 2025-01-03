package com.app.ecom.ecommerce_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidArgumentException.class)
	public ResponseEntity<String> handleInvalidArgumentException(InvalidArgumentException ex){
	return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);	
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex){
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex){
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(DatabaseAccessException.class)
	public ResponseEntity<String> handleDatabaseAccessException(DatabaseAccessException ex){
		return new ResponseEntity<>("A database error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(DatabaseConstraintViolationException.class)
	public ResponseEntity<String> handleDatabaseConstraintEexception(DatabaseConstraintViolationException ex){
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
}
