package com.cursodsousa.libraryapi.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cursodsousa.libraryapi.model.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

	boolean existsByIsbn(String isbn);

}
