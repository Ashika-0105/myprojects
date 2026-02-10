package service;

import java.sql.Connection;
import java.util.List;

import DAO.GroupChatDAO;
import DAO.Group_MembersDAO;
import DAO.pinnned_chatsDAO;
import Enum.ChaType;
import Enum.GroupMemberRole;
import MOdel.User;
import MOdel.group_members;
import MOdel.user_pinned_chats;

public class Groupmebersservice {
      
	Group_MembersDAO grpmebersdao = new Group_MembersDAO();
	public boolean addmembers(List<group_members> grpmembers, Connection con) throws Exception {
	    for(group_members gm : grpmembers) {
	        if (!grpmebersdao.create(gm, con)) {
	            return false; 
	        }
	    }
	    return true;
	}
	public group_members findrole(int userid , int groupid) throws Exception {
		return grpmebersdao.findrole(userid, groupid);
	}
	public boolean adminleft(int groupid) throws Exception {
		// TODO Auto-generated method stub
		return grpmebersdao.promoteNextAdmin(groupid);
		
	}
	
	
}
