package MOdel;

import Enum.GroupMemberRole;

public class group_members {

    private int group_id;
    private int user_id;
    private GroupMemberRole role;

    public group_members(int group_id, int user_id, GroupMemberRole role) {
        this.group_id = group_id;
        this.user_id = user_id;
        this.role = role;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public GroupMemberRole getRole() {
        return role;
    }

    public void setRole(GroupMemberRole role) {
        this.role = role;
    }
}
