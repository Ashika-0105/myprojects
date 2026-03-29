package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Enum.Role;
import MOdel.User;
import MOdel.groupchat;
import util.Mysqldb;

public class GroupChatDAO implements CommonDAO<groupchat>{

	
	public boolean create(groupchat gc,Connection con) throws Exception {
		// TODO Auto-generated method stub
		String query = "INSERT INTO groupchat (name) VALUES (?)";
	    try (PreparedStatement ptst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
	        ptst.setString(1, gc.getName());
	        int affected = ptst.executeUpdate();
	        
	        try (ResultSet rs = ptst.getGeneratedKeys()) {
	            if (rs.next()) {
	                gc.setId(rs.getInt(1));
	            }
	        }
	        return affected > 0;
	    }
	}

	@Override
	public groupchat find(int id) throws Exception {
		// TODO Auto-generated method stub
		String query = "Select * from groupchat where id = ?";
		try(Connection con = Mysqldb.getConnection();
			PreparedStatement ptst = con.prepareStatement(query)){
			ptst.setInt(1, id);
			ResultSet rs = ptst.executeQuery();
			while(rs.next()) {
				return new groupchat(rs.getInt("id"),rs.getString("name"));
			}
		}
		return null;
	}

	
	 
	 
	

	public boolean update(groupchat entity) throws Exception {
		// TODO Auto-generated method stub
	    String query = "UPDATE groupchat SET name = ? WHERE id = ?";
	    try (Connection con = Mysqldb.getConnection();
	         PreparedStatement ptst = con.prepareStatement(query)) {

	        ptst.setString(1, entity.getName());
	        ptst.setInt(2, entity.getId());

	        int affectedRows = ptst.executeUpdate();
	        return affectedRows > 0;
	    }
	}

	@Override
	public boolean delete(int id) throws Exception {
		// TODO Auto-generated method stub
	    String query = "DELETE FROM groupchat WHERE id = ?";
	    try (Connection con = Mysqldb.getConnection();
	         PreparedStatement ptst = con.prepareStatement(query)) {

	        ptst.setInt(1, id);

	        int affectedRows = ptst.executeUpdate();
	        return affectedRows > 0;
	    }
	}
   
	public List<groupchat> findallgroupchatbyuserid(int userid) throws SQLException {
	    List<groupchat> group_chats = new ArrayList<>();
	    
	    String query = "SELECT gc.* FROM group_members gm " +
	                   "INNER JOIN groupchat gc ON gm.group_id = gc.id " +
	                   "WHERE gm.user_id = ?";

	    try (Connection con = Mysqldb.getConnection();
	         PreparedStatement ptst = con.prepareStatement(query)) {
	        
	        ptst.setInt(1, userid);
	        
	        try (ResultSet rs = ptst.executeQuery()) {
	            while (rs.next()) {
	                group_chats.add(new groupchat(
	                    rs.getInt("id"), 
	                    rs.getString("name")
	                ));
	            }
	        }
	    }
	    return group_chats;
	}

	@Override
	public boolean create(groupchat entity) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

  
	
}
