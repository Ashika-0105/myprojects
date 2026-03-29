package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import Enum.Status;
import MOdel.message_reader;
import util.Mysqldb;

public class MessageReaderDAO implements CommonDAO<message_reader> {

    @Override
    public boolean create(message_reader mr) throws Exception {

        String query = "INSERT INTO message_reader " +
                       "(message_id, user_id, status) VALUES (?, ?, ?)";

        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, mr.getMessage_id());
            ps.setInt(2, mr.getUser_id());
            ps.setString(3, mr.getStatus().name().toLowerCase());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public message_reader find(int messageId) throws Exception {

        String query = "SELECT * FROM message_reader WHERE message_id = ?";
        message_reader mr = null;

        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, messageId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                mr = new message_reader(
                        rs.getInt("message_id"),
                        rs.getInt("user_id"),
                        Status.valueOf(rs.getString("status").toUpperCase()),
                        rs.getTimestamp("status_time").toLocalDateTime()
                );
            }
        }
        return mr;
    }

    @Override
    public boolean update(message_reader mr) throws Exception {

        String query = "UPDATE message_reader SET status = ?, status_time = CURRENT_TIMESTAMP " +
                       "WHERE message_id = ? AND user_id = ?";

        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, mr.getStatus().name().toLowerCase());
            ps.setInt(2, mr.getMessage_id());
            ps.setInt(3, mr.getUser_id());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int messageId) throws Exception {

        String query = "DELETE FROM message_reader WHERE message_id = ?";

        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, messageId);
            return ps.executeUpdate() > 0;
        }
    }

    public message_reader find(int messageId, int userId) throws Exception {

        String query = "SELECT * FROM message_reader WHERE message_id = ? AND user_id = ?";
        message_reader mr = null;

        try (Connection con = Mysqldb.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, messageId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                mr = new message_reader(
                        rs.getInt("message_id"),
                        rs.getInt("user_id"),
                        Status.valueOf(rs.getString("status").toUpperCase()),
                        rs.getTimestamp("status_time").toLocalDateTime()
                );
            }
        }
        return mr;
    }
}
