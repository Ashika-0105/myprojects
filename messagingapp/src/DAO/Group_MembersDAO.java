package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Enum.GroupMemberRole;
import MOdel.group_members;
import util.Mysqldb;

public class Group_MembersDAO implements CommonDAO<group_members> {

    
    public boolean create(group_members gm, Connection con) throws Exception {
        String query = "INSERT INTO group_members (group_id, user_id, role) VALUES (?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, gm.getGroup_id());
            ps.setInt(2, gm.getUser_id());
            ps.setString(3, gm.getRole().name().toLowerCase()); 

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public group_members find(int groupId) throws Exception {

        String query = "SELECT * FROM group_members WHERE group_id = ?";
        group_members gm = null;

        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, groupId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                gm = new group_members(
                        rs.getInt("group_id"),
                        rs.getInt("user_id"),
                        GroupMemberRole.valueOf(rs.getString("role").toUpperCase())
                );
            }
        }
        return gm;
    }
    

    @Override
    public boolean update(group_members gm) throws Exception {

        String query = "UPDATE group_members SET role = ? WHERE group_id = ? AND user_id = ?";

        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, gm.getRole().name().toLowerCase());
            ps.setInt(2, gm.getGroup_id());
            ps.setInt(3, gm.getUser_id());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int userId) throws Exception {

        String query = "DELETE FROM group_members WHERE user_id = ?";

        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }
    public List<group_members> findGroupMember(int groupId) throws Exception {

        String query = "SELECT * FROM group_members WHERE group_id = ? ";
        List<group_members> gm = new ArrayList<>();

        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, groupId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                gm.add( new group_members(
                        rs.getInt("group_id"),
                        rs.getInt("user_id"),
                        GroupMemberRole.valueOf(rs.getString("role").toUpperCase())
                ));
            }
        }
        return gm;
    }

	@Override
	public boolean create(group_members entity) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
   
	public boolean ismember(int grpid , int userid) throws SQLException {

        String query = "SELECT * FROM group_members WHERE group_id = ? AND user_id =?";
        List<group_members> gm = new ArrayList<>();

        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, grpid);
            ps.setInt(2, userid);
            ResultSet rs = ps.executeQuery();

           return rs.next();
		
	}
	}
	
	public boolean addMembers(List<group_members> members) throws SQLException {
	    String query = "INSERT INTO group_members (group_id, user_id, role) VALUES (?, ?, ?)";
	    
	    try (Connection con = Mysqldb.getConnection();
	         PreparedStatement ps = con.prepareStatement(query)) {
	        
	        con.setAutoCommit(false);

	        for (group_members gm : members) {
	            ps.setInt(1, gm.getGroup_id());
	            ps.setInt(2, gm.getUser_id());
	            ps.setString(3, gm.getRole().name().toLowerCase());
	            ps.addBatch(); 
	        }

	        int[] results = ps.executeBatch(); 
	        con.commit(); 
	        
	        return results.length > 0;
	    } catch (SQLException e) {
	        throw e;
	    }
	}
	public group_members findrole(int userId,int groupId) throws Exception {

        String query = "SELECT * FROM group_members WHERE user_id = ? and group_id = ?";
        group_members gm = null;

        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, userId);
            ps.setInt(2, groupId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                gm = new group_members(
                        rs.getInt("group_id"),
                        rs.getInt("user_id"),
                        GroupMemberRole.valueOf(rs.getString("role").toUpperCase())
                );
            }
        }
        return gm;
    }
	public boolean promoteNextAdmin(int groupid) throws Exception {
	    String query = "UPDATE group_members " +
	                   "SET role = 'admin' " +
	                   "WHERE group_id = ? AND role = 'member' " +
	                   "ORDER BY added_time ASC " +
	                   "LIMIT 1";

	    try (Connection con = Mysqldb.getConnection();
	         PreparedStatement ps = con.prepareStatement(query)) {

	        ps.setInt(1, groupid);
	        
	        int affectedRows = ps.executeUpdate();
	        return affectedRows > 0;
	    }
	}
	}
