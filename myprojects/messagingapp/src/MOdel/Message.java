package MOdel;

import java.time.LocalDateTime;

import Enum.ChaType;

public class Message {
	private int id;
	private int chat_id;
	private int sender_id;
	private ChaType chat_type;
	private String message_text;
	private LocalDateTime time;
	private boolean message_deleted;

	public Message(int chat_id, int sender_id,ChaType chat_type, String message_text) {
		this.chat_id = chat_id;
		this.sender_id =  sender_id;
		this.chat_type = chat_type;
		this.message_text = message_text;
		this.message_deleted = false;
	}

	public Message(int id, int chat_id,int sender_id, ChaType chat_type, String message_text, LocalDateTime time,
			boolean message_deleted) {
		this.id = id;
		this.chat_id = chat_id;
		this.sender_id =  sender_id;
		this.chat_type = chat_type;
		this.message_text = message_text;
		this.time = time;
		this.message_deleted = message_deleted;
	}
    public Message() {
    	
    }
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getChat_id() {
		return chat_id;
	}

	public void setChat_id(int chat_id) {
		this.chat_id = chat_id;
	}

	public int getSender_id() {
		return sender_id;
	}

	public void setSender_id(int sender_id) {
		this.sender_id = sender_id;
	}

	public ChaType getChat_type() {
		return chat_type;
	}

	public void setChat_type(ChaType chat_type) {
		this.chat_type = chat_type;
	}

	public String getMessage_text() {
		return message_text;
	}

	public void setMessage_text(String message_text) {
		this.message_text = message_text;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public boolean isMessage_deleted() {
		return message_deleted;
	}

	public void setMessage_deleted(boolean message_deleted) {
		this.message_deleted = message_deleted;
	}
	
	
}