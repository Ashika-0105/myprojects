package MOdel;

import java.time.LocalDateTime;

public class folder {
private int id;
private int user_id;
private String name;
private LocalDateTime  created_time ;
public folder(int user_id, String name) {
	super();
	this.user_id = user_id;
	this.name = name;
}
public folder(int id, int user_id, String name, LocalDateTime created_time) {
	super();
	this.id = id;
	this.user_id = user_id;
	this.name = name;
	this.created_time = created_time;
}
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public int getUser_id() {
	return user_id;
}
public void setUser_id(int user_id) {
	this.user_id = user_id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public LocalDateTime getCreated_time() {
	return created_time;
}
public void setCreated_time(LocalDateTime created_time) {
	this.created_time = created_time;
}

}
