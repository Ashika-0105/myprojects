package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Enum.ChaType; 
import MOdel.user_pinned_chats;
import util.Mysqldb;

public class pinnned_chatsDAO implements CommonDAO<user_pinned_chats> {

    @Override
    public boolean create(user_pinned_chats pc) throws Exception {
        String query = "INSERT INTO user_pinned_chats (user_id, chat_id, chat_type) VALUES (?, ?, ?)";
        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query)) {
            ptst.setInt(1, pc.getUser_id());
            ptst.setInt(2, pc.getChat_id());
            ptst.setString(3, pc.getChatype().name()); 
            
            return ptst.executeUpdate() > 0;
        }
    }

    @Override
    public user_pinned_chats find(int id) throws Exception {
        String query = "SELECT * FROM user_pinned_chats WHERE user_id = ?";
        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query)) {
            ptst.setInt(1, id);
            try (ResultSet rs = ptst.executeQuery()) {
                while (rs.next()) {
                    return new user_pinned_chats(
                        rs.getInt("user_id"),
                        rs.getInt("chat_id"),
                        ChaType.valueOf(rs.getString("chat_type"))
                    );
                }
            }
        }
        return null;
    }

    @Override
    public boolean update(user_pinned_chats pc) throws Exception {
        String query = "UPDATE user_pinned_chats SET chat_type = ? WHERE user_id = ? AND chat_id = ?";
        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query)) {
            ptst.setString(1, pc.getChatype().name());
            ptst.setInt(2, pc.getUser_id());
            ptst.setInt(3, pc.getChat_id());
            
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
    
    public boolean unpin(int userId, int chatId, ChaType type) throws Exception {
        String query = "DELETE FROM user_pinned_chats WHERE user_id = ? AND chat_id = ? AND chat_type = ?";
        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setInt(2, chatId);
            ps.setString(3, type.name());
            
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteSpecificPin(int userId, int chatId) throws Exception {
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
                    pins.add(new user_pinned_chats(
                        rs.getInt("user_id"),
                        rs.getInt("chat_id"),
                        ChaType.valueOf(rs.getString("chat_type"))
                    ));
                }
            }
        }
        return pins;
    }
   
    public List<user_pinned_chats> findallpinnedByType(int userId, ChaType type) throws Exception {
        List<user_pinned_chats> pinnedList = new ArrayList<>();
        String query = "SELECT * FROM user_pinned_chats WHERE user_id = ? AND chat_type = ?";
        
        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query)) {
            
            ptst.setInt(1, userId);
            ptst.setString(2, type.name()); 
            
            try (ResultSet rs = ptst.executeQuery()) {
                while (rs.next()) {
                    pinnedList.add(new user_pinned_chats(
                        rs.getInt("user_id"),
                        rs.getInt("chat_id"),
                        ChaType.valueOf(rs.getString("chat_type").toUpperCase())
                    ));
                }
            }
        }
        return pinnedList;
    }

   
}