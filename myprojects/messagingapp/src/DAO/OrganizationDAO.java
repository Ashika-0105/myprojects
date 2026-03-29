package DAO;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import MOdel.Organization;
import util.Mysqldb;

public class OrganizationDAO implements CommonDAO<Organization>{

	@Override
	public boolean create(Organization org) throws Exception {
		// TODO Auto-generated method stub
		 String query = "INSERT INTO organization (Name) VALUES (?)";
	        try(Connection con = Mysqldb.getConnection();
	        PreparedStatement ptst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
	        ptst.setString(1, org.getName());
	       

	        int affectedRows = ptst.executeUpdate();
	        ResultSet rs = ptst.getGeneratedKeys();
	        if (rs.next()) {
	            org.setId(rs.getInt(1));;
	        }

	        return affectedRows > 0;
	        }
	}

	@Override
	public Organization find(int id) throws Exception {
		// TODO Auto-generated method stub
		String query = "select * from organization where id = ?";
		try(Connection con = Mysqldb.getConnection();
			PreparedStatement ptst = con.prepareStatement(query)){
			
			ptst.setInt(1, id);
			ResultSet rs = ptst.executeQuery();
			while(rs.next()) {
				return new Organization(rs.getInt("id"),rs.getString("Name"));
			}
		}
		return null;
	}

	

	@Override
	public boolean delete(int id) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Organization entity) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
