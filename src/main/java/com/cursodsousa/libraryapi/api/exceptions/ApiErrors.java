package com.cursodsousa.libraryapi.api.exceptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import com.cursodsousa.libraryapi.exception.BusinessException;

public class ApiErrors {

	private List<String> errors;
	
	public ApiErrors(BindingResult result) {
		this.errors = new ArrayList<>();
		result.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()));
	}
	
	public ApiErrors(BusinessException ex) {
		this.errors = Arrays.asList(ex.getMessage());
	}

	public ApiErrors(ResponseStatusException ex) {
		this.errors = Arrays.asList(ex.getReason());
	}

	public List<String> getErrors() {
		return errors;
	}
	
}
