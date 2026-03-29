package com.libraryapp.model;

public class Borrower {
	private int borrowerId;
	private String borrowerName;
	private int bookId;
	
	public Borrower() {
		// TODO Auto-generated constructor stub
	}
	
	
	public Borrower(int borrowerId, String borrowerName, int bookId) {
		super();
		this.borrowerId = borrowerId;
		this.borrowerName = borrowerName;
		this.bookId = bookId;
	}


	public int getBorrowerId() {
		return borrowerId;
	}
	public void setBorrowerId(int borrowerId) {
		this.borrowerId = borrowerId;
	}
	public String getBorrowerName() {
		return borrowerName;
	}
	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}
	public int getBookId() {
		return bookId;
	}
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}


	@Override
	public String toString() {
		return "Borrower [borrowerId=" + borrowerId + ", borrowerName=" + borrowerName + ", bookId=" + bookId + "]";
	}
	
	
	
	
	
}
