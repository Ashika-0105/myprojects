package DAO;

import MOdel.Message;
import util.Mysqldb;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import Enum.ChaType;

public class MessageDAO implements CommonDAO<Message> {

	@Override
	public boolean create(Message m) throws Exception {
	    String query = "INSERT INTO message (chat_id, sender_id, chat_type, message_text, message_deleted) VALUES (?, ?, ?, ?, ?)";
	    
	    try (Connection con = Mysqldb.getConnection();
	         PreparedStatement ptst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
	        
	        ptst.setInt(1, m.getChat_id());
	        ptst.setInt(2, m.getSender_id()); 
	        ptst.setString(3, m.getChat_type().name().toLowerCase()); 
	        ptst.setString(4, m.getMessage_text());
	        ptst.setBoolean(5, m.isMessage_deleted()); 
	        
	        int affectedRows = ptst.executeUpdate();
	        
	        if (affectedRows > 0) {
	            try (ResultSet generatedKeys = ptst.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    m.setId(generatedKeys.getInt(1));
	                }
	            }
	            return true;
	        }
	    }
	    return false;
	}

    @Override
    public Message find(int msgid) throws Exception {
        String query = "SELECT * FROM message WHERE id = ?";
        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query)) {
            
            ptst.setInt(1, msgid);
            try (ResultSet rs = ptst.executeQuery()) {
                if (rs.next()) {
                    ChaType type = ChaType.valueOf(rs.getString("chat_type").toUpperCase());
                    
                    Timestamp dbTime = rs.getTimestamp("send_time");
                    LocalDateTime localTime = (dbTime != null) ? dbTime.toLocalDateTime() : null;

                    return new Message(
                        rs.getInt("id"),
                        rs.getInt("chat_id"),rs.getInt("sender_id"),
                        type,
                        rs.getString("message_text"),
                        localTime,
                        rs.getBoolean("message_deleted")
                    );
                }
            }
        }
        return null;
    }


    @Override
    public boolean update(Message m) throws Exception {
        String query = "UPDATE message SET chat_id=?, chat_type=?, message_text=?, message_deleted=? WHERE id=?";
        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query)) {
            
            ptst.setInt(1, m.getChat_id());
            ptst.setString(2, m.getChat_type().name());
            ptst.setString(3, m.getMessage_text());
            ptst.setBoolean(4, m.isMessage_deleted());
            ptst.setInt(5, m.getId());
            
            return ptst.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int msgid) throws Exception {
        String query = "DELETE FROM message WHERE id = ?";
        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query)) {
            
            ptst.setInt(1, msgid);
            return ptst.executeUpdate() > 0;
        }
    }
    public List<Message> findAllMessagesByChatId(int chatid,ChaType chattype) throws SQLException {

        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM message WHERE chat_id = ? and chat_type =?";

        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query)) {

            ptst.setInt(1, chatid);
            ptst.setString(2, chattype.name().toLowerCase());
            ResultSet rs = ptst.executeQuery();

            while (rs.next()) {
                messages.add(
                    new Message(
                        rs.getInt("id"),
                        rs.getInt("chat_id"),
                        rs.getInt("sender_id"),
                        ChaType.valueOf(rs.getString("chat_type").toUpperCase()),
                        rs.getString("message_text"),
                        rs.getTimestamp("send_time").toLocalDateTime(),
                        rs.getBoolean("message_deleted")
                    )
                );
            }
        }
        return messages;
    }
    public boolean deleteforeveryone(int msg_id) throws SQLException {
        String query = "UPDATE message SET message_deleted = ? , message_text = ? WHERE id = ?";
        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query)) {
             
            ptst.setBoolean(1, true);
            ptst.setString(2, "This message has been deleted");
            ptst.setInt(3, msg_id);
            
            int rowsAffected = ptst.executeUpdate(); 
            
            return rowsAffected > 0;
        }
    }
    public List<Message> findMessages(int chatId, int viewerId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        // FIX: Using 'send_time' consistently throughout the query
        String query = "SELECT * FROM message m " +
                       "WHERE m.chat_id = ? " +
                       "AND NOT EXISTS ( " +
                       "    SELECT 1 FROM private_chat_block cb " + 
                       "    WHERE cb.chat_id = m.chat_id " +
                       "    AND cb.user_id = ? " + 
                       "    AND cb.status = 'BLOCKED' " +
                       "    AND m.send_time > cb.action_time " + 
                       "    AND NOT EXISTS ( " +
                       "        SELECT 1 FROM private_chat_block cu " +
                       "        WHERE cu.chat_id = cb.chat_id " +
                       "        AND cu.status = 'UNBLOCKED' " +
                       "        AND cu.action_time > cb.action_time " +
                       "        AND cu.action_time < m.send_time " +
                       "    ) " +
                       ") ORDER BY m.send_time ASC";

        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query)) {
            
            ptst.setInt(1, chatId);
            ptst.setInt(2, viewerId); 
            
            try (ResultSet rs = ptst.executeQuery()) {
                while (rs.next()) {
                    Message msg = new Message(); 
                    msg.setId(rs.getInt("id"));
                    msg.setChat_id(rs.getInt("chat_id"));
                    msg.setSender_id(rs.getInt("sender_id"));
                    
                    String type = rs.getString("chat_type");
                    if(type != null) {
                        msg.setChat_type(ChaType.valueOf(type.toUpperCase()));
                    }
                    
                    msg.setMessage_text(rs.getString("message_text")); 
                    msg.setMessage_deleted(rs.getBoolean("message_deleted"));
                    Timestamp ts = rs.getTimestamp("send_time");
                    if (ts != null) {
                        msg.setTime(ts.toLocalDateTime());
                    }

                    messages.add(msg);
                }
            }
        }
        return messages;
    }
    
    
    
}
