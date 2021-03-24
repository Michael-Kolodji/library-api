package com.cursodsousa.libraryapi.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cursodsousa.libraryapi.exception.BusinessException;
import com.cursodsousa.libraryapi.model.entity.Book;
import com.cursodsousa.libraryapi.model.repository.BookRepository;
import com.cursodsousa.libraryapi.service.BookService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

	private final BookRepository repository;

	@Override
	public Book save(Book book) {
		if(repository.existsByIsbn(book.getIsbn())) {
			throw new BusinessException("Isbn já cadastrado");
		}
		return repository.save(book);
	}

	@Override
	public Optional<Book> getById(Long id) {
		return repository.findById(id);
	}

	@Override
	public void delete(Book book) {
		if(book == null || book.getId() == null) {
			throw new IllegalArgumentException("O id do livro não pode ser nullo");
		}
		repository.delete(book);
	}

	@Override
	public Book update(Book book) {
		if(book == null || book.getId() == null) {
			throw new IllegalArgumentException("O id do livro não pode ser nullo");
		}
		return repository.save(book);
	}

	@Override
	public Page<Book> find(Book filter, Pageable pageRequest) {

		Example<Book> example = Example.of(filter, ExampleMatcher
				.matching()
				.withIgnoreCase()
				.withIgnoreNullValues()
				.withStringMatcher(StringMatcher.CONTAINING));
		return repository.findAll(example , pageRequest);
	}

	@Override
	public Optional<Book> getBookByIsbn(String isbn) {
		// TODO Auto-generated method stub
		return null;
	}

}
