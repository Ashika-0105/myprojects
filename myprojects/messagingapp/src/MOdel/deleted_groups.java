package MOdel;

public class deleted_groups {
	private int group_id;
	private int user_id;
	public deleted_groups(int group_id, int user_id) {
		super();
		this.group_id = group_id;
		this.user_id = user_id;
	}
	public int getChat_id() {
		return group_id;
	}
	public void setChat_id(int chat_id) {
		this.group_id = chat_id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
}
