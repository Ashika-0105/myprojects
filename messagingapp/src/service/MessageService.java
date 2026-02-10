package service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import DAO.BlockchatDAO;
import DAO.MessageDAO;
import DAO.MessageDeleteDAO;
import Enum.ChaType;
import Enum.MessageDelete;
import Logs.logsui;
import MOdel.Message;
import MOdel.message_delete;
import MOdel.private_chat_block;

public class MessageService {
	BlockchatDAO blockchatdao = new BlockchatDAO();
	MessageDAO msgdao = new MessageDAO();
	MessageDeleteDAO msgdltdao = new MessageDeleteDAO();
	public List<Message> findallmessage(int chatid, ChaType chattype, int userid) throws Exception {
	    List<Message> messages = msgdao.findMessages(chatid, userid);
	    Set<Integer> deletedids = msgdltdao.deltedmsgids(userid, chatid);
	    
	    List<Message> filteredmessages = new ArrayList<>();
	    
	    for(Message m : messages) {
	        if (!deletedids.contains(m.getId())) {
	            filteredmessages.add(m);
	        }
	    }
	    
	    return filteredmessages;
	}
	 public boolean sendmessage(Message msg) throws Exception {
		  
		 return msgdao.create(msg);
	  }
	 public boolean deletemessage(Message msg,int user_id ,MessageDelete msgdlt ) throws Exception {
		// TODO Auto-generated method stub
		if(msgdlt == MessageDelete.DELETEFORME) {
			message_delete meesagesgdelete = new message_delete(msg.getId(),user_id);
			return msgdltdao.create(meesagesgdelete);
		}
		else {
			return msgdao.deleteforeveryone(msg.getId());
		}
	 }
	 
	 public boolean ismsgdelete(int currentuserid,int msgid) throws SQLException {
		return  msgdltdao.ismsgdelete(currentuserid, msgid);
	 }
}
