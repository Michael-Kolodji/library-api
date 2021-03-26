package com.cursodsousa.libraryapi.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cursodsousa.libraryapi.model.entity.Book;
import com.cursodsousa.libraryapi.model.entity.Loan;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class LoanRepositoryTest {

	@Autowired
	private TestEntityManager manager;

	@Autowired
	private LoanRepository repository;
	
	@Test
	@DisplayName("Deve verificar se existe empréstimo não devolvido para o livro.")
	void existsByBookAndNotReturnedTest() {
		
		Loan loan = createAndPersistLoan(LocalDate.now());
		Book book = loan.getBook();
		
		boolean exists = repository.existsByBookAndNotRetruned(book);
		
		assertThat(exists).isTrue();
		
	}
	
	@Test
	@DisplayName("Deve buscar empréstimo pelo isbn do livro ou pelo customer")
	void findByBookIsbnOrCustomerTest() {
		Loan loan = createAndPersistLoan(LocalDate.now());
		
		Page<Loan> result = repository.findByBookIsbnOrCustomer("123", "Fulano", PageRequest.of(0, 10));
		
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent()).contains(loan);
		assertThat(result.getPageable().getPageSize()).isEqualTo(10);
		assertThat(result.getPageable().getPageNumber()).isZero();
		assertThat(result.getTotalElements()).isEqualTo(1);
		
	}
	
	@Test
	@DisplayName("Deve obter empréstimos cuja data empréstimo for menor ou igual a três dias atrás e não retornados")
	void findByLoanDateLessThanAndNotReturned() {
		Loan loan = createAndPersistLoan(LocalDate.now().minusDays(5));
		
		List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));
		
		assertThat(result).hasSize(1).contains(loan);
	}
	
	@Test
	@DisplayName("Deve retornar vazio quando não houver empréstimos atrasados.")
	void notFindByLoanDateLessThanAndNotReturned() {
		createAndPersistLoan(LocalDate.now());
		
		List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));
		
		assertThat(result).isEmpty();
	}
	
	/*COMMONS METHODS*/
	private Loan createAndPersistLoan(LocalDate loanDate) {
		
		Book book = BookRepositoryTest.createNewBook();
		manager.persist(book);
		
		Loan loan = Loan.builder().book(book).customer("Fulano").loanDate(loanDate).build();
		manager.persist(loan);
		
		return loan;
	}
	
}
