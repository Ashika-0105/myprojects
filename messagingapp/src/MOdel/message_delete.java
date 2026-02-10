package MOdel;

public class message_delete {

    private int message_id;
    private int user_id;

    public message_delete(int message_id, int user_id) {
        this.message_id = message_id;
        this.user_id = user_id;
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
}
