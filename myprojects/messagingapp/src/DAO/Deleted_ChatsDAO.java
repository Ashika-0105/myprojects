package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import MOdel.deleted_chats;
import util.Mysqldb;

public class Deleted_ChatsDAO implements CommonDAO<deleted_chats>{

	@Override
	public boolean create(deleted_chats entity) throws Exception {
		// TODO Auto-generated method stub
		String query = "INSERT INTO deleted_chats (chat_id, user_id) VALUES (?, ?)";
		try (Connection con = Mysqldb.getConnection();
			 PreparedStatement ptst = con.prepareStatement(query)) {
			ptst.setInt(1, entity.getChat_id());
			ptst.setInt(2, entity.getUser_id());
			
			int affectedRows = ptst.executeUpdate();
			return affectedRows > 0;
		}
	}

	@Override
	public deleted_chats find(int id) throws Exception {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public boolean update(deleted_chats entity) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(int id) throws Exception {
		// TODO Auto-generated method stub
		String query = "DELETE FROM deleted_chats WHERE chat_id = ?";
		try (Connection con = Mysqldb.getConnection();
			 PreparedStatement ptst = con.prepareStatement(query)) {
			ptst.setInt(1, id);
			
			int affectedRows = ptst.executeUpdate();
			return affectedRows > 0;
		}
	}
	
	
	public List<deleted_chats> isChatDeletedByUser(int chatId, int userId) throws SQLException {
	    List<deleted_chats> deletedchats = new ArrayList<>();
	    String query = "SELECT * FROM deleted_chats WHERE chat_id = ? AND user_id = ?";
	    
	    try (Connection con = Mysqldb.getConnection();
	         PreparedStatement ptst = con.prepareStatement(query)) {
	        
	        ptst.setInt(1, chatId);
	        ptst.setInt(2, userId);
	        
	        try (ResultSet rs = ptst.executeQuery()) {
	            while (rs.next()) {
	                Timestamp sqlTime = rs.getTimestamp("deleted_time");
	                LocalDateTime localDateTime = (sqlTime != null) ? sqlTime.toLocalDateTime() : null;
	                
	                deletedchats.add(new deleted_chats(
	                    rs.getInt("chat_id"),
	                    rs.getInt("user_id"), 
	                    localDateTime
	                ));
	            }
	        }
	    }
	    return deletedchats;
	}

}