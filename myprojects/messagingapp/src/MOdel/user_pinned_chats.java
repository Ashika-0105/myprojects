package MOdel;

import Enum.ChaType;

public class user_pinned_chats {
private int user_id;
private int chat_id;
private ChaType chatype;
public user_pinned_chats(int user_id, int chat_id, ChaType chatype) {
	super();
	this.user_id = user_id;
	this.chat_id = chat_id;
	this.chatype = chatype;
}
public int getUser_id() {
	return user_id;
}
public void setUser_id(int user_id) {
	this.user_id = user_id;
}
public int getChat_id() {
	return chat_id;
}
public void setChat_id(int chat_id) {
	this.chat_id = chat_id;
}
public ChaType getChatype() {
	return chatype;
}
public void setChatype(ChaType chatype) {
	this.chatype = chatype;
}


}
