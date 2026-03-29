package MOdel;

import Enum.ChaType;

public class locked_chats {
private int chat_id;
private ChaType chattype;
private int user_id;
private String lock_pin;
public locked_chats(int chat_id, ChaType chattype, int user_id, String lock_pin) {
	super();
	this.chat_id = chat_id;
	this.chattype = chattype;
	this.user_id = user_id;
	this.lock_pin = lock_pin;
}
public int getChat_id() {
	return chat_id;
}
public void setChat_id(int chat_id) {
	this.chat_id = chat_id;
}
public ChaType getChattype() {
	return chattype;
}
public void setChattype(ChaType chattype) {
	this.chattype = chattype;
}
public int getUser_id() {
	return user_id;
}
public void setUser_id(int user_id) {
	this.user_id = user_id;
}
public String getLock_pin() {
	return lock_pin;
}
public void setLock_pin(String lock_pin) {
	this.lock_pin = lock_pin;
}


}
