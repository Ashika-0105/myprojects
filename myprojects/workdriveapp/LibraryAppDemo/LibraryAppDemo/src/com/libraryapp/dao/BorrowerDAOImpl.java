package com.libraryapp.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import com.libraryapp.model.Borrower;
import com.libraryapp.util.MySqlDbUtil;

public class BorrowerDAOImpl implements BorrowerDAO{

	private static final String INSERT_QUERY =
	        "INSERT INTO borrower (student_name, book_id, borrow_date) VALUES (?, ?, ?)";
	@Override
	public Borrower save(Borrower borrower) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(borrower);
		Connection connection = MySqlDbUtil.getConnection();
		PreparedStatement pstmt = connection.prepareStatement(INSERT_QUERY, PreparedStatement.RETURN_GENERATED_KEYS);
		pstmt.setString(1, borrower.getBorrowerName());
		pstmt.setInt(2, borrower.getBookId());
		pstmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
		
		int affectedRows = pstmt.executeUpdate();
		
	    ResultSet rs = pstmt.getGeneratedKeys();
		int borrowerId = 0;
	    if(rs.next()) {
	    	borrowerId = rs.getInt(1);
	    	
	    }
	    
	    borrower.setBorrowerId(borrowerId);
	    System.out.println(borrower);
		return borrower;
	}

	@Override
	public Borrower findById(int borrowerId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Borrower> findAll() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean update(int borrowerId, Borrower borrower) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(int borrowerId) {
		// TODO Auto-generated method stub
		return false;
	}

}
