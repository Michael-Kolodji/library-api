package com.cursodsousa.libraryapi.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cursodsousa.libraryapi.api.dto.LoanFilterDTO;
import com.cursodsousa.libraryapi.exception.BusinessException;
import com.cursodsousa.libraryapi.model.entity.Book;
import com.cursodsousa.libraryapi.model.entity.Loan;
import com.cursodsousa.libraryapi.model.repository.LoanRepository;
import com.cursodsousa.libraryapi.service.LoanService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

	private final LoanRepository repository;

	@Override
	public Loan save(Loan loan) {
		if(repository.existsByBookAndNotRetruned(loan.getBook())) {
			throw new BusinessException("Livro já emprestado.");
		}
		return repository.save(loan);
	}

	@Override
	public Optional<Loan> getByid(Long id) {
		return repository.findById(id);
	}

	@Override
	public Loan update(Loan loan) {
		return repository.save(loan);
	}

	@Override
	public Page<Loan> find(LoanFilterDTO dto, Pageable page) {
		return repository.findByBookIsbnOrCustomer(dto.getIsbn(), dto.getCustomer(), page);
	}

	@Override
	public Page<Loan> getLoansByBook(Book book, Pageable pageable) {
		return repository.findByBook(book, pageable);
	}

	@Override
	public List<Loan> getAllLateLoans() {
		final Integer loanDays = 4;
		LocalDate threDaysAgo = LocalDate.now().minusDays(loanDays);
		return repository.findByLoanDateLessThanAndNotReturned(threDaysAgo);
	}

}
