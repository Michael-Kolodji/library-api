package com.cursodsousa.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cursodsousa.libraryapi.exception.BusinessException;
import com.cursodsousa.libraryapi.model.entity.Book;
import com.cursodsousa.libraryapi.model.repository.BookRepository;
import com.cursodsousa.libraryapi.service.impl.BookServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class BookServiceTest {

	BookService service;
	@MockBean
	BookRepository repository;
	
	@BeforeEach
	public void setUp() {
		this.service = new BookServiceImpl(repository);
	}
	
	@Test
	@DisplayName("Deve salvar um livro")
	void saveBookTest() {

		Book book = createValidBook();
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
		Mockito.when(repository.save(book)).thenReturn(Book.builder().id(1l).isbn("123").author("Fulano").title("As aventuras").build());
		
		Book savedBook = service.save(book);
		
		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getIsbn()).isEqualTo("123");
		assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
		assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
	}
	
	@Test
	@DisplayName("Deve lançar erro de negócio ao tentar salvar um livro com isbn duplicado")
	void shouldNotSavedABookwithDuplicatedISBN() {
		
		Book book = createValidBook();
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
		
		Throwable exception = Assertions.catchThrowable(() -> service.save(book));
		
		assertThat(exception)
			.isInstanceOf(BusinessException.class)
			.hasMessage("Isbn já cadastrado");
		
		verify(repository, never()).save(book);
	}
	
	@Test
	@DisplayName("Deve obter um livro por Id")
	void getById() {
		Long id = 1l;
		
		Book book = createValidBook();
		book.setId(id);
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));
		
		Optional<Book> foundBook = service.getById(id);
		
		assertThat(foundBook).isPresent();
		assertThat(foundBook.get().getId()).isEqualTo(id);
		assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
		assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
		assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
	}
	
	@Test
	@DisplayName("Deve retornar vazio ao obter um livro por Id quando ele não existe na base")
	void getNotFoundById() {
		Long id = 1l;
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		Optional<Book> book = service.getById(id);
		
		assertThat(book).isNotPresent();
	}
	
	@Test
	@DisplayName("Deve excluir um livro")
	void deleteBook() {
		Book book = Book.builder().id(1l).build();
		
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(book));
		
		Mockito.verify(repository, Mockito.times(1)).delete(book);
	}
	
	@Test
	@DisplayName("Deve retornar erro ao excluir um livro sem id")
	void deleteInexistentIdBook() {
		Book book = new Book();
		
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(book));
		
		Mockito.verify(repository, Mockito.never()).delete(book);
		
	}
	
	@Test
	@DisplayName("Deve atualizar um livro")
	void updateBook() {
		Long id = 1l;
		//livro a atualizar
		Book updatingBook = Book.builder().id(id).build();		
		
		//simulação
		Book updatedBook = createValidBook();
		updatedBook.setId(id);
		
		Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);
		
		Book book = service.save(updatingBook);
		
		assertThat(book).isNotNull();
		assertThat(book.getId()).isEqualTo(updatedBook.getId());
		assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
		assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
		assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
		
	}
	
	@Test
	@DisplayName("Deve retornar erro ao atualizar um livro sem id")
	void updateInexistentIdBook() {
		Book book = new Book();
		
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(book));
		
		Mockito.verify(repository, Mockito.never()).save(book);
		
	}
	
	@Test
	@DisplayName("Deve filtrar livros pelas propriedades")
	public void findBook() {
		
		Book book = createValidBook();
		
		Page<Book> page = new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 10), 1);
		Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))).thenReturn(page);
	}
	
	private Book createValidBook() {
		return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
	}
	
}
