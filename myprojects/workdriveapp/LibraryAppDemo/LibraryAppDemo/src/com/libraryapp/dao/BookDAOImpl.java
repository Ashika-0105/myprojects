package com.libraryapp.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.libraryapp.model.Book;
import com.libraryapp.util.MySqlDbUtil;

public class BookDAOImpl implements BookDAO{

	@Override
	public Book save(Book book) throws Exception {
		
		return null;
	}

	@Override
	public Book findById(int bookId) throws Exception {
		// TODO Auto-generated method stub
		String query = "SELECT * FROM book where book_id=?";
		Connection con = MySqlDbUtil.getConnection();
		PreparedStatement ptst = con.prepareStatement(query);
		
		ptst.setInt(1, bookId);
		
		ResultSet rs = ptst.executeQuery();
		Book book = null;
		while(rs.next()) {
			int id = rs.getInt("book_id");
			String name = rs.getString("title");
			String author = rs.getString("author");
			boolean isAvailable = rs.getBoolean("is_available");
			
			book = new Book(id,name,author, isAvailable);
			
		}
		
		return book;
	}

	@Override
	public List<Book> findAll() throws Exception {
		List<Book> bookList= new ArrayList<>();
		String query = "Select * from book";
		Connection con = MySqlDbUtil.getConnection();
		PreparedStatement ptst = con.prepareStatement(query); 
		
		ResultSet rs= ptst.executeQuery();
		
		while(rs.next()) {
			int id = rs.getInt("book_id");
			String name = rs.getString("title");
			String author = rs.getString("author");
			boolean isAvailable = rs.getBoolean("is_available");
			
			
			Book book= new Book(id,name,author, isAvailable);
			bookList.add(book);
			
		}
		return bookList;
	}

	@Override
	public boolean update(int bookId, Book book) throws SQLException {
		// TODO Auto-generated method stub
		String query = "update book set title = ?,author =?,is_available = ? where book_id = ?";
		Connection con = MySqlDbUtil.getConnection();
		PreparedStatement ptst = con.prepareStatement(query); 
		ptst.setString(1, book.getTitle());
		ptst.setString(2, book.getAuthor());
		ptst.setBoolean(3, book.isAvailable());
		ptst.setInt(4, bookId);

		int affectedRows = ptst.executeUpdate();
		return affectedRows>0;
	}

	@Override
	public boolean delete(int bookId) {
		// TODO Auto-generated method stub
		return false;
	}

}
