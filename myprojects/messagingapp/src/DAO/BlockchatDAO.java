package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Enum.BlockedStatus;
import MOdel.private_chat_block;
import util.Mysqldb;

/*describe private_chat_block; 
+-------------+-----------------------------+------+-----+-------------------+-------------------+
| Field       | Type                        | Null | Key | Default           | Extra             |
+-------------+-----------------------------+------+-----+-------------------+-------------------+
| id          | int                         | NO   | PRI | NULL              | auto_increment    |
| chat_id     | int                         | YES  | MUL | NULL              |                   |
| user_id     | int                         | YES  | MUL | NULL              |                   |
| action_time | timestamp                   | YES  |     | CURRENT_TIMESTAMP | DEFAULT_GENERATED |
| status      | enum('BLOCKED','UNBLOCKED') | NO   |     | BLOCKED           |                   |
+-------------+-----------------------------+------+-----+-------------------+-------------------+*/

public class BlockchatDAO implements CommonDAO<private_chat_block> {

    @Override
    public boolean create(private_chat_block entity) throws Exception {
        String query = "INSERT INTO private_chat_block (chat_id, user_id,status) VALUES (?, ?, ?)";
        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ptst.setInt(1, entity.getChat_id());
            ptst.setInt(2, entity.getUser_id());
            ptst.setString(3, entity.getStatus().name().toLowerCase());
            int affectedRows = ptst.executeUpdate();
            
            try (ResultSet rs = ptst.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getInt(1));
                }
            }
            return affectedRows > 0;
        }
    }

    @Override
    public private_chat_block find(int chatid) throws Exception {
        String query = "SELECT * FROM private_chat_block WHERE chat_id = ?";
        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query)) {
            ptst.setInt(1, chatid);
            try (ResultSet rs = ptst.executeQuery()) {
                if (rs.next()) {
                    return new private_chat_block(
                        rs.getInt("id"),
                        rs.getInt("chat_id"),
                        rs.getInt("user_id"),
                        rs.getTimestamp("action_time").toLocalDateTime(),
                        BlockedStatus.valueOf(rs.getString("status").toUpperCase())
                    );
                }
            }
        }
        return null;
    }

    @Override
    public boolean update(private_chat_block entity) throws Exception {
        String query = "UPDATE private_chat_block SET chat_id = ?, user_id = ? WHERE id = ?";
        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query)) {
            ptst.setInt(1, entity.getChat_id());
            ptst.setInt(2, entity.getUser_id());
            ptst.setInt(3, entity.getId());
            
            int affectedRows = ptst.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean delete(int id) throws Exception {
        String query = "DELETE FROM private_chat_block WHERE id = ?";
        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query)) {
            ptst.setInt(1, id);
            return ptst.executeUpdate() > 0;
        }
    }

   
    public List<private_chat_block> findbychatidnaduserid(int chatId, int userId) throws Exception {
    	List<private_chat_block> chats = new ArrayList<>();
        String query = "SELECT * FROM private_chat_block WHERE chat_id = ? AND user_id = ?";
        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ptst = con.prepareStatement(query)) {
            ptst.setInt(1, chatId);
            ptst.setInt(2, userId);
           
            try (ResultSet rs = ptst.executeQuery()) {
                while (rs.next()) {
                	chats.add (new private_chat_block(
                        rs.getInt("id"),
                        rs.getInt("chat_id"),
                        rs.getInt("user_id"),
                        rs.getTimestamp("action_time").toLocalDateTime(),
                        BlockedStatus.valueOf(rs.getString("status").toUpperCase())
                    ));
                }
            }
        }
        return chats;
    }
    
}