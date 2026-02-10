package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Enum.Role;
import MOdel.Organization;
import MOdel.User;
import util.Mysqldb;

public class UserDAO implements CommonDAO<User> {

	@Override
	public boolean create(User u) throws Exception {
		// TODO Auto-generated method stub
		String query = "Insert into user (Name,Email,password,role,org_id) values(?,?,?,?,?)";
		try (Connection con = Mysqldb.getConnection();
				PreparedStatement ptst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			ptst.setString(1, u.getName());
			ptst.setString(2, u.getEmail());
			ptst.setString(3, u.getPassword());
			ptst.setString(4, u.getRole().name());
			ptst.setInt(5, u.getOrg_id());
			int afffectedrows = ptst.executeUpdate();
			ResultSet rs = ptst.getGeneratedKeys();
			while (rs.next()) {
				u.setId(rs.getInt(1));
			}
			return afffectedrows > 0;
		}

	}

	@Override
	public User find(int id) throws Exception {
		// TODO Auto-generated method stub
		String query = "Select * from user where id = ?";
		try (Connection con = Mysqldb.getConnection(); PreparedStatement ptst = con.prepareStatement(query)) {
			ptst.setInt(1, id);
			ResultSet rs = ptst.executeQuery();
			while (rs.next()) {
				return new User(rs.getInt("id"), rs.getString("Name"), rs.getString("Email"), rs.getString("password"),
						Role.valueOf(rs.getString("role").toUpperCase()), rs.getInt("org_id"));
			}
		}

		return null;
	}

	@Override
	public boolean delete(int id) throws Exception {
		// TODO Auto-generated method stub
		String query = "delete from user where id = ?";
		try (Connection con = Mysqldb.getConnection(); PreparedStatement ptst = con.prepareStatement(query)) {
			ptst.setInt(1, id);
			int affectedrows = ptst.executeUpdate();
			return affectedrows > 0;
		}
	}

	@Override
	public boolean update(User u) throws Exception {
		// TODO Auto-generated method stub
		String query = "Update user set Name = ?,password = ? where id = ?";
		try (Connection con = Mysqldb.getConnection(); PreparedStatement ptst = con.prepareStatement(query)) {
			ptst.setString(1, u.getName());
			ptst.setString(2, u.getPassword());
			ptst.setInt(3, u.getId());

			int affectedrows = ptst.executeUpdate();
			return affectedrows > 0;
		}

	}

	public User findbyemail(String email) throws SQLException {
		String query = "Select * from user where Email = ?";
		try (Connection con = Mysqldb.getConnection(); PreparedStatement ptst = con.prepareStatement(query)) {
			ptst.setString(1, email);
			ResultSet rs = ptst.executeQuery();
			while (rs.next()) {
				return new User(rs.getInt("id"), rs.getString("Name"), rs.getString("Email"), rs.getString("password"),
						Role.valueOf((rs.getString("role")).toUpperCase()), rs.getInt("org_id"));

			}
		}
		return null;

	}

	public List<User> searchUser(String searchname) throws SQLException {
		String query = "Select * from User where Name Like ?";
		List<User> allusers = null;
		try (Connection con = Mysqldb.getConnection(); PreparedStatement ptst = con.prepareStatement(query)) {
			ptst.setString(1, "%" + searchname + "%");
			ResultSet rs = ptst.executeQuery();
			while (rs.next()) {
				allusers.add(new User(rs.getInt("id"), rs.getString("Name"), rs.getString("Email"),
						rs.getString("password"), Role.valueOf(rs.getString("role")), rs.getInt("org_id")));
			}
		}
		return allusers;

	}

	public List<User> findall(int org_id) throws SQLException {
	    String query = "Select * from user where org_id = ?";
	    List<User> users = new ArrayList<>(); 
	    
	    try (Connection con = Mysqldb.getConnection(); 
	         PreparedStatement ptst = con.prepareStatement(query)) {
	        
	        ptst.setInt(1, org_id);
	        
	        try (ResultSet rs = ptst.executeQuery()) {
	            while (rs.next()) {
	                users.add(new User(
	                    rs.getInt("id"), 
	                    rs.getString("Name"), 
	                    rs.getString("Email"),
	                    rs.getString("password"), 
	                    Role.valueOf(rs.getString("role").toUpperCase()), 
	                    rs.getInt("org_id")
	                ));
	            }
	        }
	    }
	    return users; 
	}

}
