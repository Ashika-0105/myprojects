package MOdel;

import java.time.LocalDateTime;

import Enum.Status;

public class message_reader {
private int message_id;
private int user_id;
private Status status;
private LocalDateTime time;
public message_reader(int message_id, int user_id, Status status, LocalDateTime time) {
	super();
	this.message_id = message_id;
	this.user_id = user_id;
	this.status = status;
	this.time = time;
}
public int getMessage_id() {
	return message_id;
}
public void setMessage_id(int message_id) {
	this.message_id = message_id;
}
public int getUser_id() {
	return user_id;
}
public void setUser_id(int user_id) {
	this.user_id = user_id;
}

public Status getStatus() {
	return status;
}
public void setStatus(Status status) {
	this.status = status;
}
public LocalDateTime getTime() {
	return time;
}
public void setTime(LocalDateTime time) {
	this.time = time;
}

}
