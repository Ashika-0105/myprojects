package com.libraryapp.dao;

import java.util.List;

import com.libraryapp.model.Book;

public interface BookDAO {
	
	Book save(Book book) throws Exception;
	Book findById(int bookId) throws Exception;
	List<Book> findAll() throws Exception;
	boolean update(int bookId, Book book) throws Exception;
	boolean delete(int bookId) throws Exception;
	
	
	
	
}
