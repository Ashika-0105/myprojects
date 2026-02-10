package service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import DAO.BlockchatDAO;
import DAO.ChatDAO;
import DAO.Deleted_ChatsDAO;
import DAO.MessageDeleteDAO;
import DAO.pinnned_chatsDAO;
import Enum.BlockedStatus;
import Enum.ChaType;
import MOdel.Chat;
import MOdel.Message;
import MOdel.deleted_chats;
import MOdel.private_chat_block;
import MOdel.user_pinned_chats;

public class Chatservice {
	ChatDAO chatdao = new ChatDAO();
	BlockchatDAO blockchatdao = new BlockchatDAO();
	Deleted_ChatsDAO deletedchatdao = new Deleted_ChatsDAO();
	MessageService msgservice = new MessageService();
	MessageDeleteDAO msgdltdao = new MessageDeleteDAO();
	pinnned_chatsDAO pinnedchats = new pinnned_chatsDAO();
	public Map<String, List<Chat>> viewallchats(int userid) throws Exception {
	    List<user_pinned_chats> pinnedRecords = pinnedchats.findallpinnedByType(userid,ChaType.PRIVATE);
	    List<Chat> allChats = chatdao.findallchats(userid);
	    
	    Set<Integer> pinnedIds = new HashSet<>();
	    for (user_pinned_chats pc : pinnedRecords) {
	        pinnedIds.add(pc.getChat_id());
	    }

	    List<Chat> pinnedList = new ArrayList<>();
	    List<Chat> unpinnedList = new ArrayList<>();

	    for (Chat chat : allChats) {
	        if (pinnedIds.contains(chat.getId())) {
	            pinnedList.add(chat);
	        } else {
	            unpinnedList.add(chat);
	        }
	    }

	    Map<String, List<Chat>> result = new HashMap<>();
	    result.put("pinned", pinnedList);
	    result.put("unpinned", unpinnedList);
	    
	    return result;
	}

	public boolean createNewChat(int user1, int user2) throws Exception {
		Chat newChat = new Chat(user1, user2);
		return chatdao.create(newChat);
	}

	public boolean ischat(int user2, int currentuserid) throws SQLException {

		return chatdao.findchat(currentuserid, user2) == null ? false : true;
	}

	public boolean blockchat(int chatid, int userid) throws Exception {
		private_chat_block blockchat = new private_chat_block(chatid, userid,BlockedStatus.BLOCKED);
		return blockchatdao.create(blockchat);

	}
	
	public boolean deletechat(int chatid,int userid) throws Exception {
		return msgdltdao.deleteAllMessagesByChatId(chatid, userid);
		
	}
	
    
	public List<deleted_chats> isdeletedchat(int chatid,int userid) throws SQLException  {
		return deletedchatdao.isChatDeletedByUser(chatid, userid);
	}
	public boolean isblocked(Chat c, int userid) throws Exception {
	    List<private_chat_block> blockchat = blockchatdao.findbychatidnaduserid(c.getId(), userid);
	    
	    return blockchat.stream()
	        .max(Comparator.comparing(private_chat_block::getAction_time))
	        .map(chat -> chat.getStatus() == BlockedStatus.BLOCKED) 
	        .orElse(false); 
	}

	public boolean unblockchat(int chatid, int userid) throws Exception {
		// TODO Auto-generated method stub
		private_chat_block blockchat = new private_chat_block(chatid, userid,BlockedStatus.UNBLOCKED);
		return blockchatdao.create(blockchat);
	}
	
	public boolean pinchat(int userid,int chatid,ChaType chattype) throws Exception {
		user_pinned_chats pc = new user_pinned_chats(userid,chatid,chattype);
		return pinnedchats.create(pc);
		
	}

	public boolean unpinchat(int userId, int chatId, ChaType type) throws Exception {
	    // Simply pass the parameters to the DAO
	    return pinnedchats.unpin(userId, chatId, type);
	}

	
}
