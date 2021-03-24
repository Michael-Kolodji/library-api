package com.cursodsousa.libraryapi.api.resource;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cursodsousa.libraryapi.api.dto.LoanDTO;
import com.cursodsousa.libraryapi.model.entity.Book;
import com.cursodsousa.libraryapi.model.entity.Loan;
import com.cursodsousa.libraryapi.service.BookService;
import com.cursodsousa.libraryapi.service.LoanService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

	private final LoanService loanService;

	private final BookService bookService;
	
//	private final ModelMapper mapper;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Long create(@RequestBody LoanDTO dto) {
		Book book = bookService.getBookByIsbn(dto.getIsbn()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Livro não encontrado."));
		
		Loan loan = Loan.builder().book(book).loanDate(LocalDate.now()).customer(dto.getCustomer()).build();
		
		loan = loanService.save(loan);
		
		return loan.getId();
	}
}
