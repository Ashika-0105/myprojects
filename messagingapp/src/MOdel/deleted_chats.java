package MOdel;

import java.time.LocalDateTime;

public class deleted_chats {
private int chat_id;
private int user_id;
private LocalDateTime deleted_time;
public deleted_chats(int chat_id, int user_id) {
	super();
	this.chat_id = chat_id;
	this.user_id = user_id;
}

public deleted_chats(int chat_id, int user_id, LocalDateTime deleted_time) {
	super();
	this.chat_id = chat_id;
	this.user_id = user_id;
	this.deleted_time = deleted_time;
}

public int getChat_id() {
	return chat_id;
}
public void setChat_id(int chat_id) {
	this.chat_id = chat_id;
}
public int getUser_id() {
	return user_id;
}
public void setUser_id(int user_id) {
	this.user_id = user_id;
}
public LocalDateTime getDeleted_time() {
	return deleted_time;
}
public void setDeleted_time(LocalDateTime deleted_time) {
	this.deleted_time = deleted_time;
}

}
