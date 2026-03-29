package service;

import java.sql.SQLException;
import java.util.List;

import DAO.UserDAO;
import Enum.Role;
import MOdel.User;
import exceptions.UserNotFound;

public class Userservice {
     
	UserDAO userdao = new UserDAO();
	public User login(String email,String password) throws UserNotFound, SQLException {
		
			User u = userdao.findbyemail(email);
			if(u==null) {
				throw new UserNotFound();			
		    } 
			else if(u.getEmail().equals(email) && u.getPassword().equals(password)) {
				return u;
			}
			else {
				throw new UserNotFound("Invalid username or password");	
			}
	}
	public void adduser(String name,String email,Role role,int orgid) throws Exception {
		User u = new User(name,email,role,orgid);
		userdao.create(u);
   }
	public boolean isUser(String email) throws SQLException {
			User u = userdao.findbyemail(email);
			if(u!=null) {
				return true;
			}
			
		return false;
		
	}
	
	public User findbyid(int userid) throws Exception {
		return userdao.find(userid);
	}
	public List<User> allusers(int orgid) throws SQLException{
		return userdao.findall(orgid);
		
	}
	public boolean deleteUser(int userid) throws Exception {
		// TODO Auto-generated method stub
		return userdao.delete(userid);
	}
	public void updateProfile(User u) throws Exception {
		// TODO Auto-generated method stub
		userdao.update(u);
	}
	public User findbyemail(String email) throws Exception {
		return userdao.findbyemail(email);
	}
}
