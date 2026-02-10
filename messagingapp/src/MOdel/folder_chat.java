package MOdel;

import Enum.ChaType;

public class folder_chat {
private int folder_id;
private int chat_id;
private ChaType chatype;
public folder_chat(int folder_id, int chat_id, ChaType chatype) {
	super();
	this.folder_id = folder_id;
	this.chat_id = chat_id;
	this.chatype = chatype;
}
public int getFolder_id() {
	return folder_id;
}
public void setFolder_id(int folder_id) {
	this.folder_id = folder_id;
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
