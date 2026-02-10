package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Enum.ChaType;
import MOdel.user_pinned_chats;
import util.Mysqldb;

public class lockedchatsDAO implements CommonDAO<user_pinned_chats> {

    @Override
    public boolean create(user_pinned_chats entity) throws Exception {
        String query = "INSERT INTO user_pinned_chats (user_id, chat_id, chat_type) VALUES (?, ?, ?)";
        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query)) {
            ptst.setInt(1, entity.getUser_id());
            ptst.setInt(2, entity.getChat_id());
            ptst.setString(3, (entity.getChatype()).name()); 
            
            return ptst.executeUpdate() > 0;
        }
    }

    @Override
    public user_pinned_chats find(int id) throws Exception {
		return null;
        
    }

    @Override
    public boolean update(user_pinned_chats entity) throws Exception {
        String query = "UPDATE user_pinned_chats SET chat_type = ? WHERE user_id = ? AND chat_id = ?";
        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query)) {
            ptst.setString(1, (entity.getChatype()).name());
            ptst.setInt(2, entity.getUser_id());
            ptst.setInt(3, entity.getChat_id());
            
            return ptst.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws Exception {
        String query = "DELETE FROM user_pinned_chats WHERE user_id = ?";
        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query)) {
            ptst.setInt(1, id);
            return ptst.executeUpdate() > 0;
        }
    }

    public boolean unpinChat(int userId, int chatId) throws Exception {
        String query = "DELETE FROM user_pinned_chats WHERE user_id = ? AND chat_id = ?";
        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query)) {
            ptst.setInt(1, userId);
            ptst.setInt(2, chatId);
            return ptst.executeUpdate() > 0;
        }
    }

    public List<user_pinned_chats> findAllByUserId(int userId) throws Exception {
        List<user_pinned_chats> pins = new ArrayList<>(); 
        String query = "SELECT * FROM user_pinned_chats WHERE user_id = ?";
        
        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query)) {
            ptst.setInt(1, userId);
            
            try (ResultSet rs = ptst.executeQuery()) { 
                while (rs.next()) {
                    String typeStr = rs.getString("chat_type");
                    ChaType type = ChaType.valueOf(typeStr.toUpperCase());
                    
                    pins.add(new user_pinned_chats(
                        rs.getInt("user_id"),
                        rs.getInt("chat_id"),
                        type
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); 
            throw e;
        }
        return pins;
    }
}