package com.cursodsousa.libraryapi.api.resource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cursodsousa.libraryapi.api.dto.LoanDTO;
import com.cursodsousa.libraryapi.exception.BusinessException;
import com.cursodsousa.libraryapi.model.entity.Book;
import com.cursodsousa.libraryapi.model.entity.Loan;
import com.cursodsousa.libraryapi.service.BookService;
import com.cursodsousa.libraryapi.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
class LoanControllerTest {

	static final String LOAN_API = "/api/loans";

	@Autowired
	MockMvc mvc;
	
	@MockBean
	private BookService bookService;

	@MockBean
	private LoanService loanService;
	
	@Test
	@DisplayName("Deve realizar um empréstimo")
	void createLoanTest() throws Exception {
		
		String isbn = "123";
		String customer = "Fulano";
		LoanDTO dto = LoanDTO.builder().isbn(isbn).customer(customer).build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		Book book = Book.builder().id(1l).isbn(isbn).build();
		BDDMockito.given(bookService.getBookByIsbn(isbn)).willReturn(Optional.of(book));
		
		Loan loan = Loan.builder().id(1l).customer(customer).book(book).loanDate(LocalDate.now()).build();
		BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(LOAN_API)
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.content(json);
		
		mvc.perform(request)
			.andExpect(status().isCreated())
			.andExpect(content().string("1"));
	}
	
	@Test
	@DisplayName("Deve retornar erro ao tentar fazer empréstimo de um livro inexistente.")
	void invalidIsbnCreateLoanTest() throws Exception {
		
		String isbn = "123";
		LoanDTO dto = LoanDTO.builder().isbn(isbn).customer("Fulano").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		BDDMockito.given(bookService.getBookByIsbn(isbn)).willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(LOAN_API)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);
			
			mvc.perform(request)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("errors", Matchers.hasSize(1)))
				.andExpect(jsonPath("errors[0]").value("Livro não encontrado."));
		
	}
	
	@Test
	@DisplayName("Deve retornar erro ao tentar fazer empréstimo de um livro inexistente.")
	void loanedBookErrorOnCreateLoanTest() throws Exception {
		
		String isbn = "123";
		LoanDTO dto = LoanDTO.builder().isbn(isbn).customer("Fulano").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		Book book = Book.builder().id(1l).isbn(isbn).build();
		BDDMockito.given(bookService.getBookByIsbn(isbn)).willReturn(Optional.of(book));
		
		BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willThrow(new BusinessException("Livro já está emprestado."));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(LOAN_API)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);
			
			mvc.perform(request)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("errors", Matchers.hasSize(1)))
				.andExpect(jsonPath("errors[0]").value("Livro já está emprestado."));
		
	}
}
