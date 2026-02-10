package MOdel;

import java.security.SecureRandom;

import Enum.Role;

public class User {
private int id;
private String name;
private String email;
private String password;
private Role role;
private int org_id;
public User(int id, String name, String email, String password, Role role, int org_id) {
	super();
	this.id = id;
	this.name = name;
	this.email = email;
	this.password = password;
	this.role = role;
	this.org_id = org_id;
}
public User(String name, String email, Role role, int org_id) {
	super();
	this.name = name;
	this.email = email;
	this.password = passwordgenerator();
	this.role = role;
	this.org_id = org_id;
}
private String passwordgenerator() {
	// TODO Auto-generated method stub
    final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
    SecureRandom random = new SecureRandom();
    StringBuilder password = new StringBuilder(8);

    for (int i = 0; i < 9; i++) {
        int index = random.nextInt(CHAR_POOL.length());
        password.append(CHAR_POOL.charAt(index));
    }
    return password.toString();
}
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getEmail() {
	return email;
}
public void setEmail(String email) {
	this.email = email;
}
public String getPassword() {
	return password;
}
public void setPassword(String password) {
	this.password = password;
}
public Role getRole() {
	return role;
}
public void setRole(Role role) {
	this.role = role;
}
public int getOrg_id() {
	return org_id;
}
public void setOrg_id(int org_id) {
	this.org_id = org_id;
}


}
