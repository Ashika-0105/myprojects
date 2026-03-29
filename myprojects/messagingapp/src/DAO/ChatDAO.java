package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Enum.Role;
import MOdel.Chat;
import MOdel.User;
import util.Mysqldb;

public class ChatDAO implements CommonDAO<Chat>{

	@Override
	public boolean create(Chat c) throws Exception {
		// TODO Auto-generated method stub
		String query = "Insert into chat (user1_id,user2_id) values(?,?)";
		try(Connection con = Mysqldb.getConnection();
			PreparedStatement ptst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
			ptst.setInt(1, c.getUser1_id());
			ptst.setInt(2, c.getUser2_id());
			int afffectedrows = ptst.executeUpdate();
			ResultSet rs = ptst.getGeneratedKeys();
			while(rs.next()) {
				c.setId(rs.getInt(1));
			}
			return afffectedrows >0;
		}
	}


	@Override
	public Chat find(int id) throws Exception {
		// TODO Auto-generated method stub
		String query = "Select * from chat where id = ?";
		try(Connection con = Mysqldb.getConnection();
			PreparedStatement ptst = con.prepareStatement(query)){
			ptst.setInt(1, id);
			ResultSet rs = ptst.executeQuery();
			while(rs.next()) {
				return new Chat(
						rs.getInt("id"),
						rs.getInt("user1_id"),
						rs.getInt("user2_id")
						);
			}
		}
		
		return null;
	}
   
	
	
	public List<Chat> findallchats(int userid) throws SQLException {
	    String query = "SELECT * FROM chat WHERE (user1_id = ? ) OR (user2_id = ?)";
	    List<Chat> chats = new ArrayList<>();
	    
	    try(Connection con = Mysqldb.getConnection();
				PreparedStatement ptst = con.prepareStatement(query)){
				ptst.setInt(1, userid);
				ptst.setInt(2, userid);
				ResultSet rs = ptst.executeQuery();
				while(rs.next()) {
					chats.add( new Chat(
							rs.getInt("id"),
							rs.getInt("user1_id"),
							rs.getInt("user2_id")
							));
				}
			}
			
			return chats;
		
	}

	public Chat findchat(int userid,int receiverid) throws SQLException {
	    String query = "SELECT * FROM chat WHERE (user1_id = ?  and user2_id = ?) || (user1_id = ?  and user2_id = ?)";
	    List<Chat> chats = new ArrayList<>();
	    
	    try(Connection con = Mysqldb.getConnection();
				PreparedStatement ptst = con.prepareStatement(query)){
				ptst.setInt(1, userid);
				ptst.setInt(2, receiverid);
				ptst.setInt(3, receiverid);
				ptst.setInt(4, userid);
				ResultSet rs = ptst.executeQuery();
				while(rs.next()) {
					return new Chat(
							rs.getInt("id"),
							rs.getInt("user1_id"),
							rs.getInt("user2_id")
							);
				}
			}
		return null;
	}
	@Override
	public boolean update(Chat entity) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(int id) throws Exception {
		// TODO Auto-generated method stub
		String query = "delete from chat where id = ?";
		try(Connection con = Mysqldb.getConnection();
			PreparedStatement ptst = con.prepareStatement(query)){
			ptst.setInt(1, id);
			int affectedrows = ptst.executeUpdate();
			return affectedrows >0;
		}
	}

}
