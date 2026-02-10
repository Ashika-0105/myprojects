package com.libraryapp.dao;

import java.util.List;

import com.libraryapp.model.Book;
import com.libraryapp.model.Borrower;

public interface BorrowerDAO {
	
	Borrower save(Borrower borrower) throws Exception;
	Borrower findById(int borrowerId) throws Exception;
	List<Borrower> findAll() throws Exception;
	boolean update(int borrowerId, Borrower borrower);
	boolean delete(int borrowerId);
}
