package com.libraryapp.service;

import com.libraryapp.dao.BookDAO;
import com.libraryapp.dao.BookDAOImpl;
import com.libraryapp.dao.BorrowerDAO;
import com.libraryapp.dao.BorrowerDAOImpl;
import com.libraryapp.exception.BookIsNotAvailableException;
import com.libraryapp.model.Book;
import com.libraryapp.model.Borrower;

public class BookService {
	private BorrowerDAO borrowerDAO = new BorrowerDAOImpl();
	private BookDAO bookDAO = new BookDAOImpl();

	public boolean borrow(Borrower borrower) throws Exception{
		
		Book book = bookDAO.findById(borrower.getBookId()); //getBook

		if(book.isAvailable()) {
			
			Borrower insertedBorrower = borrowerDAO.save(borrower);//borrower save
			book.setAvailable(false);
			boolean isUpdate = false;
			if(insertedBorrower != null) {
				isUpdate = bookDAO.update(borrower.getBookId(),book); //update Book
				
			}
			
			return isUpdate;
		}else {
			throw new BookIsNotAvailableException("Book is not currently Available");
		}
		
		
		
		
		
	}
}
