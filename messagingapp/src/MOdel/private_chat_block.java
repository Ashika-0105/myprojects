package MOdel;

import java.time.LocalDateTime;

import Enum.BlockedStatus;


/*describe private_chat_block; 
+-------------+-----------------------------+------+-----+-------------------+-------------------+
| Field       | Type                        | Null | Key | Default           | Extra             |
+-------------+-----------------------------+------+-----+-------------------+-------------------+
| id          | int                         | NO   | PRI | NULL              | auto_increment    |
| chat_id     | int                         | YES  | MUL | NULL              |                   |
| user_id     | int                         | YES  | MUL | NULL              |                   |
| action_time | timestamp                   | YES  |     | CURRENT_TIMESTAMP | DEFAULT_GENERATED |
| status      | enum('BLOCKED','UNBLOCKED') | NO   |     | BLOCKED           |                   |
+-------------+-----------------------------+------+-----+-------------------+-------------------+*/


public class private_chat_block {
private int id;
private int chat_id; 
private int user_id;
private LocalDateTime action_time;
private BlockedStatus status;


public private_chat_block(int chat_id, int user_id, BlockedStatus status) {
	super();
	this.chat_id = chat_id;
	this.user_id = user_id;
	this.status = status;
}

public private_chat_block(int id, int chat_id, int user_id, LocalDateTime action_time, BlockedStatus status) {
	super();
	this.id = id;
	this.chat_id = chat_id;
	this.user_id = user_id;
	this.action_time = action_time;
	this.status = status;
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

public int getUser_id() {
	return user_id;
}

public void setUser_id(int user_id) {
	this.user_id = user_id;
}

public LocalDateTime getAction_time() {
	return action_time;
}

public void setAction_time(LocalDateTime action_time) {
	this.action_time = action_time;
}

public BlockedStatus getStatus() {
	return status;
}

public void setStatus(BlockedStatus status) {
	this.status = status;
}




}
