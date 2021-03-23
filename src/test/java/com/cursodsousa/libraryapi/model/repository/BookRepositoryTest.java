package com.cursodsousa.libraryapi.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cursodsousa.libraryapi.model.entity.Book;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class BookRepositoryTest {

	@Autowired
	TestEntityManager manager;
	
	@Autowired
	BookRepository repository;
	
	@Test
	@DisplayName("Deve retornar verdadeiro quando existir um livro na base com isbn informado")
	void returnTrueWhenIsbnExistsTest() {
		
		String isbn = "123";
		Book book = createNewBook();
		book.setIsbn(isbn);
		manager.persist(book);
		
		boolean exists = repository.existsByIsbn(isbn);
		
		assertThat(exists).isTrue();
	}
	
	@Test
	@DisplayName("Deve retornar false quando não existir um livro na base com isbn informado")
	void returnFalseWhenIsbnDoesntExistTest() {
		
		String isbn = "123";
		
		boolean exists = repository.existsByIsbn(isbn);
		
		assertThat(exists).isFalse();
	}
	
	@Test
	@DisplayName("Deve obter um livro por id.")
	void findByIdTest() {
		Book book = createNewBook();
		manager.persist(book);
		
		Optional<Book> foundBook = repository.findById(book.getId());
		
		assertThat(foundBook).isPresent();
	}
	
	@Test
	@DisplayName("Deve salvar um livro")
	void saveBookTest() {
		
		Book book = createNewBook();
		
		Book savedBook = repository.save(book);
		
		assertThat(savedBook.getId()).isNotNull();
		
	}
	
	@Test
	@DisplayName("Deve excluir um livro")
	void deleteBookTest() {
		Book book = createNewBook();
		manager.persist(book);
		
		Book foundBook = manager.find(Book.class, book.getId());
		
		repository.delete(foundBook);
		
		Book deletedBook = manager.find(Book.class, book.getId());
		assertThat(deletedBook).isNull();
		
	}

	private Book createNewBook() {
		return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
	}
}
