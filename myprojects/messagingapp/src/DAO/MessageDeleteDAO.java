package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import MOdel.Message;
import MOdel.message_delete;
import util.Mysqldb;

public class MessageDeleteDAO implements CommonDAO<message_delete> {

    @Override
    public boolean create(message_delete md) throws Exception {

        String query = "INSERT INTO message_delete (message_id, user_id) VALUES (?, ?)";

        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, md.getMessage_id());
            ps.setInt(2, md.getUser_id());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public message_delete find(int messageId) throws Exception {

        String query = "SELECT * FROM message_delete WHERE message_id = ?";
        message_delete md = null;

        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, messageId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                md = new message_delete(
                        rs.getInt("message_id"),
                        rs.getInt("user_id")
                );
            }
        }
        return md;
    }

    @Override
    public boolean update(message_delete entity) {
        return false;
    }

    @Override
    public boolean delete(int messageId) throws Exception {

        String query = "DELETE FROM message_delete WHERE message_id = ?";

        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, messageId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int messageId, int userId) throws Exception {

        String query = "DELETE FROM message_delete WHERE message_id = ? AND user_id = ?";

        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, messageId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteAllMessagesByChatId(int chatId, int userId) throws Exception {
        String query = "INSERT INTO message_delete (message_id, user_id) "
                + "SELECT m.id, ? "
                + "FROM message m "
                + "WHERE m.chat_id = ? "
                + "AND NOT EXISTS ( "
                + "    SELECT 1 FROM message_delete md "
                + "    WHERE md.message_id = m.id AND md.user_id = ? "
                + ")";

        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query)) {

            ptst.setInt(1, userId); 
            ptst.setInt(2, chatId); 
            ptst.setInt(3, userId); 

            return ptst.executeUpdate() > 0;
        }
    }
    public boolean deleteAllMessagesBygroupId(int groupId, int userId) throws Exception {
    	String query = "INSERT INTO message_delete (message_id, user_id) "
    	        + "SELECT m.id, ? "
    	        + "FROM message m "
    	        + "WHERE m.chat_id = ? "
    	        + "AND m.type = 'GROUP' " 
    	        + "AND NOT EXISTS ( "
    	        + "    SELECT 1 FROM message_delete md "
    	        + "    WHERE md.message_id = m.id AND md.user_id = ? "
    	        + ")";

        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query)) {

            ptst.setInt(1, userId); 
            ptst.setInt(2, groupId); 
            ptst.setInt(3, userId); 

            return ptst.executeUpdate() > 0;
        }
    }
    
    public boolean ismsgdelete(int userid, int msgid) throws SQLException {
        String query = "SELECT 1 FROM message_delete WHERE message_id = ? AND user_id = ?";

        try (Connection con = Mysqldb.getConnection(); 
             PreparedStatement ptst = con.prepareStatement(query)) {
            
            ptst.setInt(1, msgid);
            ptst.setInt(2, userid);
            
            try (ResultSet rs = ptst.executeQuery()) {
                return rs.next(); 
            }
        }
    }
    
    public Set<Integer> deltedmsgids(int userid, int chatid) throws SQLException {
        Set<Integer> deletedIds = new HashSet<>();
        String query = "SELECT md.message_id FROM message_delete md " +
                       "JOIN message m ON m.id = md.message_id " +
                       "WHERE md.user_id = ? AND m.chat_id = ?";
        
        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query)) {
            
            ptst.setInt(1, userid);
            ptst.setInt(2, chatid);
            
            try (ResultSet rs = ptst.executeQuery()) {
                while (rs.next()) {
                    deletedIds.add(rs.getInt("message_id"));
                }
            }
        }
        return deletedIds;
    }
}
