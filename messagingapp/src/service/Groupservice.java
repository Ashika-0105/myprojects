package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import DAO.GroupChatDAO;
import DAO.Group_MembersDAO;
import DAO.MessageDeleteDAO;
import DAO.pinnned_chatsDAO;
import Enum.ChaType;
import Enum.GroupMemberRole;
import Logs.logsui;
import MOdel.User;
import MOdel.group_members;
import MOdel.groupchat;
import MOdel.user_pinned_chats;

public class Groupservice {
	
      GroupChatDAO groupchatdao = new GroupChatDAO();
      Groupmebersservice grpmembersservice = new Groupmebersservice();
      Group_MembersDAO grpmembers = new Group_MembersDAO();
  	  MessageDeleteDAO msgdltdao = new MessageDeleteDAO();
  	  pinnned_chatsDAO pinnedchats = new pinnned_chatsDAO();

  	public Map<String, List<groupchat>> viewallgrps(int user_id) throws Exception {
  	    // 1. Fetch all group chats the user is a member of
  	    List<groupchat> allGroups = groupchatdao.findallgroupchatbyuserid(user_id);
  	    
  	    
  	    List<user_pinned_chats> pinnedRecords = pinnedchats.findallpinnedByType(user_id, ChaType.GROUP);
  	    
  	    Set<Integer> pinnedIds = new HashSet<>();
  	    for (user_pinned_chats pc : pinnedRecords) {
  	        pinnedIds.add(pc.getChat_id()); 
  	    }

  	    List<groupchat> pinnedList = new ArrayList<>();
  	    List<groupchat> unpinnedList = new ArrayList<>();

  	    for (groupchat group : allGroups) {
  	        if (pinnedIds.contains(group.getId())) {
  	            pinnedList.add(group);
  	        } else {
  	            unpinnedList.add(group);
  	        }
  	    }

  	    Map<String, List<groupchat>> result = new HashMap<>();
  	    result.put("pinned", pinnedList);
  	    result.put("unpinned", unpinnedList);
  	    
  	    return result;
  	}

      public boolean createNewGroup(String groupName, int adminId, List<User> members) throws Exception {
    	    Connection con = null;
    	    try {
    	        con = util.Mysqldb.getConnection();
    	        con.setAutoCommit(false); 

    	        groupchat grpchat = new groupchat(groupName);
    	        groupchatdao.create(grpchat, con); 

    	        List<group_members> grpmembers = new ArrayList<>();
    	        grpmembers.add(new group_members(grpchat.getId(), adminId, GroupMemberRole.ADMIN));
    	        
    	        for (User m : members) {
    	            grpmembers.add(new group_members(grpchat.getId(), m.getId(), GroupMemberRole.MEMBER));
    	        }

    	        grpmembersservice.addmembers(grpmembers, con); 

    	        con.commit(); 
    	        return true;

    	    } catch (Exception e) {
    	        if (con != null) {
    	            con.rollback(); 
    	        }
    	        logsui.logError("Group creation failed, transaction rolled back", e);
    	        throw e;
    	    } finally {
    	        if (con != null) {
    	            con.close(); 
    	        }
    	    }
    	}
      public List<group_members> findgrpmembers(int grpid) throws Exception {
    	 return  grpmembers.findGroupMember(grpid);
      }

	  public boolean updaterole(group_members gm) throws Exception {
		// TODO Auto-generated method stub
		  return grpmembers.update(gm);
	  }
	  public boolean deletegrpchat(int grpchatid,int userid) throws Exception {
			return msgdltdao.deleteAllMessagesByChatId(grpchatid, userid);
			
		}
	  
	  public boolean ismember(int grpid , int userid) throws SQLException {
		  return grpmembers.ismember(grpid, userid);
	  }
	  public boolean addMembersToGroup(List<group_members> members) throws Exception {
		    if (members.isEmpty()) return false;
		    return grpmembers.addMembers(members);
		}
	  
	  public boolean pingroup(int userid,int chatid,ChaType chattype) throws Exception {
			user_pinned_chats pc = new user_pinned_chats(userid,chatid,chattype);
			return pinnedchats.create(pc);
			
		}

	 

		public boolean unpingroup(int userId, int groupId, ChaType type) throws Exception {
		    // Both use the same logic because they target the same table
		    return pinnedchats.unpin(userId, groupId, type);
		}
}
