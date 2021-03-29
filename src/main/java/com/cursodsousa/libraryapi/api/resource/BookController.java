package com.cursodsousa.libraryapi.api.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cursodsousa.libraryapi.api.dto.BookDTO;
import com.cursodsousa.libraryapi.api.dto.LoanDTO;
import com.cursodsousa.libraryapi.model.entity.Book;
import com.cursodsousa.libraryapi.model.entity.Loan;
import com.cursodsousa.libraryapi.service.BookService;
import com.cursodsousa.libraryapi.service.LoanService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Api("Book API")
@Slf4j
public class BookController {

	private final BookService service;
	private final ModelMapper mapper;
	private final LoanService loanService;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation("Creates a book")
	public BookDTO create(@RequestBody @Valid BookDTO dto) {
		log.info("creating a book for isbn: {}", dto.getIsbn());
		Book entity = mapper.map(dto, Book.class);
		entity = service.save(entity);
		return mapper.map(entity, BookDTO.class);
	}
	
	@GetMapping("/{id}")
	@ApiOperation("Obtains a book details by id")
	public BookDTO get(@PathVariable Long id) {
		log.info("obtaining details for book id: {}", id);
		return service.getById(id)
				.map(book -> mapper.map(book, BookDTO.class))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		 
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation("Deletes a book by id")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Book succesfully deleted")
	})
	public void delete(@PathVariable Long id) {
		log.info("deleting book of id: {}", id);
		Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		service.delete(book);
	}
	
	@PutMapping("/{id}")
	@ApiOperation("Updates a book")
	public BookDTO update(@PathVariable Long id, @RequestBody BookDTO dto) {
		log.info("updating book of id: {}", id);
		return service.getById(id)
				.map(book -> {
					book.setAuthor(dto.getAuthor());
					book.setTitle(dto.getTitle());
					book = service.update(book);					
					return mapper.map(book, BookDTO.class);
				})
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}
	
	@GetMapping
	@ApiOperation("Find books by params")
	public Page<BookDTO> find(BookDTO dto, Pageable pageRequest) {
		Book filter = mapper.map(dto, Book.class);
		Page<Book> result = service.find(filter, pageRequest);
		
		List<BookDTO> list = result.getContent()
			.stream()
				.map(entity -> mapper.map(entity, BookDTO.class))
				.collect(Collectors.toList());
		
		return new PageImpl<>(list, pageRequest, result.getTotalElements());
		
	}	
	
	@GetMapping("/{id}/loans")
	@ApiOperation("List loans by book id")
	public PageImpl<LoanDTO> loansByBook(@PathVariable Long id, Pageable pageable) {
		Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		Page<Loan> result = loanService.getLoansByBook(book, pageable);
		
		List<LoanDTO> list = result.getContent().stream()
			.map(loan -> {
				Book loanBook = loan.getBook();
				BookDTO bookDTO = mapper.map(loanBook, BookDTO.class);
				LoanDTO loanDTO = mapper.map(loan, LoanDTO.class);
				loanDTO.setBook(bookDTO);
				return loanDTO;
			}).collect(Collectors.toList());
		
		return new PageImpl<LoanDTO>(list, pageable, result.getTotalElements());
	}
	
}
