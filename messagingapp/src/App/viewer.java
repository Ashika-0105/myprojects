package App;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import Enum.*;
import Logs.logsui;
import MOdel.*;
import exceptions.UserNotFound;
import service.*;

public class viewer {
	private static Scanner Userinput = new Scanner(System.in);
	private static Userservice Userservice = new Userservice();
	private static Chatservice chatservice = new Chatservice();
	private static Groupservice grpservice = new Groupservice();
	private static MessageService msgservice = new MessageService();
	private static Groupmebersservice groupmebersservice = new Groupmebersservice();
	static User currentuser = null;

	public static void Start() {
		login();
	}

	// LEVEL 1: Authentication
	public static void login() {
		while (true) {
			System.out.println("\n=== Welcome to Chat Application ===");
			System.out.println("Enter your Email (or type 'exit' to quit):");
			String useremail = Userinput.nextLine();
			if (useremail.equalsIgnoreCase("exit"))
				break;

			System.out.println("Enter your Password:");
			String password = Userinput.nextLine();

			try {
				currentuser = Userservice.login(useremail, password);
				System.out.println("Welcome, " + currentuser.getName() + "!");

				if (currentuser.getRole() == Role.ADMIN) {
					admininfo();
				} else {
					userinfo();
				}
			} catch (UserNotFound e) {
				System.out.println("Error: " + e.getMessage());
			} catch (SQLException e) {
				logsui.logError("Login failed", e);
				System.out.println("Something went wrong. Try again later.");
			}
		}
	}

	// LEVEL 2: Dashboard-> 1st userinfo()
	private static void userinfo() {
		while (currentuser != null) {
			System.out.println("\n--- Level 2: User Dashboard ---");
			System.out.println(" 1) View All Chats And Groups ");
			System.out.println(" 2) View Folder ");
			System.out.println(" 3) Update Profile ");
			System.out.println(" 4) Logout ");

			int choice = Userinput.nextInt();
			Userinput.nextLine();

			switch (choice) {
			case 1 -> ViewAllchatsandgroups();
			case 3 -> updateProfile();
			case 4 -> {
				System.out.println("Logging out...");
				currentuser = null;
			}
			default -> System.out.println("Invalid choice.");
			}
		}
	}

	// LEVEL 3: Conversation Lists
	private static void ViewAllchatsandgroups() {
		boolean backToLevel2 = false;
		while (!backToLevel2) {
			Map<String, List<Chat>> chatMap = new HashMap<>();
			Map<String, List<groupchat>> grpMap = new HashMap<>();

			System.out.println("\n--- Level 3: Your Conversations ---");
			try {
				chatMap = chatservice.viewallchats(currentuser.getId());
				List<Chat> pinnedChats = chatMap.get("pinned");
				List<Chat> unpinnedChats = chatMap.get("unpinned");

				System.out.println("[Private Chats]");
				System.out.println("  -- Pinned --");
				if (pinnedChats.isEmpty())
					System.out.println("    (None)");
				for (int i = 0; i < pinnedChats.size(); i++) {
					User u = Userservice.findbyid(
							pinnedChats.get(i).getUser1_id() == currentuser.getId() ? pinnedChats.get(i).getUser2_id()
									: pinnedChats.get(i).getUser1_id());
					System.out.println("    P" + (i + 1) + ") " + u.getEmail());
				}

				System.out.println("  -- Others --");
				for (int i = 0; i < unpinnedChats.size(); i++) {
					User u = Userservice.findbyid(unpinnedChats.get(i).getUser1_id() == currentuser.getId()
							? unpinnedChats.get(i).getUser2_id()
							: unpinnedChats.get(i).getUser1_id());
					System.out.println("    U" + (i + 1) + ") " + u.getEmail());
				}

				grpMap = grpservice.viewallgrps(currentuser.getId());
				List<groupchat> pinnedGrps = grpMap.get("pinned");
				List<groupchat> unpinnedGrps = grpMap.get("unpinned");

				System.out.println("\n[Group Chats]");
				System.out.println("  -- Pinned --");
				if (pinnedGrps.isEmpty())
					System.out.println("    (None)");
				for (int i = 0; i < pinnedGrps.size(); i++) {
					System.out.println("    GP" + (i + 1) + ") " + pinnedGrps.get(i).getName());
				}

				System.out.println("  -- Others --");
				for (int i = 0; i < unpinnedGrps.size(); i++) {
					System.out.println("    GU" + (i + 1) + ") " + unpinnedGrps.get(i).getName());
				}

			} catch (Exception e) {
				logsui.logError("Error loading lists", e);
			}

			System.out.println(
					"\nOptions: (1) View Private Chat | (2) View Group Chat | (3) Create Chat | (4) Create Group | (5) Pin Chat | (6) Unpin Chat | (7) Back");
			int choice = Userinput.nextInt();
			Userinput.nextLine();

			switch (choice) {
			case 1 -> {
				System.out.println("Enter Prefix and Number (e.g., P1 or U1):");
				String input = Userinput.nextLine().toUpperCase();
				try {
					char prefix = input.charAt(0);
					int index = Integer.parseInt(input.substring(1)) - 1;
					List<Chat> targetList = (prefix == 'P') ? chatMap.get("pinned") : chatMap.get("unpinned");

					if (index >= 0 && index < targetList.size()) {
						ViewChat(targetList.get(index));
					} else {
						System.out.println("Invalid selection.");
					}
				} catch (Exception e) {
					System.out.println("Use format P1 or U1.");
				}
			}
			case 2 -> {
				System.out.println("Enter Prefix and Number (e.g., GP1 or GU1):");
				String input = Userinput.nextLine().toUpperCase();
				try {
					String prefix = input.startsWith("GP") ? "GP" : "GU";
					int index = Integer.parseInt(input.substring(prefix.length())) - 1;
					List<groupchat> targetList = (prefix.equals("GP")) ? grpMap.get("pinned") : grpMap.get("unpinned");

					if (index >= 0 && index < targetList.size()) {
						viewgroupMessages(targetList.get(index));
					} else {
						System.out.println("Invalid selection.");
					}
				} catch (Exception e) {
					System.out.println("Use format GP1 or GU1.");
				}
			}
			case 3 -> createchat();
			case 4 -> creategroup();
			case 5 -> Pin(chatMap, grpMap);
			case 6 -> UnPin(chatMap, grpMap);
			case 7 -> backToLevel2 = true;
			default -> System.out.println("Invalid option.");
			}
		}
	}

	private static void UnPin(Map<String, List<Chat>> chatMap, Map<String, List<groupchat>> grpMap) {
		System.out.println("\n--- Unpin Conversation ---");
		System.out.println("Do you want to unpin: 1) Private Chat | 2) Group Chat | 3) Back");

		int choice = Userinput.nextInt();
		Userinput.nextLine();

		switch (choice) {
		case 1 -> {
			List<Chat> pinnedList = chatMap.get("pinned");
			if (pinnedList.isEmpty()) {
				System.out.println("You have no pinned private chats.");
				return;
			}
			System.out.print("Enter Private Chat Number to Unpin (e.g., P1): ");
			String input = Userinput.nextLine().toUpperCase();
			try {
				int index = Integer.parseInt(input.substring(1)) - 1;
				if (index >= 0 && index < pinnedList.size()) {
					Chat selected = pinnedList.get(index);
					boolean success = chatservice.unpinchat(currentuser.getId(), selected.getId(), ChaType.PRIVATE);
					if (success)
						System.out.println("Unpinned private chat successfully!");
				} else {
					System.out.println("Invalid number.");
				}
			} catch (Exception e) {
				System.out.println("Format error. Use 'P1'.");
			}
		}
		case 2 -> {
			List<groupchat> pinnedGrps = grpMap.get("pinned");
			if (pinnedGrps.isEmpty()) {
				System.out.println("You have no pinned groups.");
				return;
			}
			System.out.print("Enter Group Number to Unpin (e.g., GP1): ");
			String input = Userinput.nextLine().toUpperCase();
			try {
				int index = Integer.parseInt(input.substring(2)) - 1;
				if (index >= 0 && index < pinnedGrps.size()) {
					groupchat selected = pinnedGrps.get(index);
					boolean success = grpservice.unpingroup(currentuser.getId(), selected.getId(), ChaType.GROUP);
					if (success)
						System.out.println("Unpinned group successfully!");
				} else {
					System.out.println("Invalid number.");
				}
			} catch (Exception e) {
				System.out.println("Format error. Use 'GP1'.");
			}
		}
		case 3 -> {
			return;
		}
		default -> System.out.println("Invalid choice.");
		}
	}

	private static void Pin(Map<String, List<Chat>> chatMap, Map<String, List<groupchat>> grpMap) {
		System.out.println("\n--- Pin Conversation ---");
		System.out.println("Do you want to pin: 1) Private Chat | 2) Group Chat | 3) Back");

		int pinChoice = Userinput.nextInt();
		Userinput.nextLine();

		switch (pinChoice) {
		case 1 -> {
			System.out.print("Enter Private Chat Number (e.g., P1 or U1): ");
			String input = Userinput.nextLine().toUpperCase();
			try {
				char prefix = input.charAt(0);
				int index = Integer.parseInt(input.substring(1)) - 1;

				List<Chat> list = (prefix == 'P') ? chatMap.get("pinned") : chatMap.get("unpinned");

				if (index >= 0 && index < list.size()) {
					Chat selected = list.get(index);
					chatservice.pinchat(currentuser.getId(), selected.getId(), ChaType.PRIVATE);
					System.out.println("Pinned successfully!");
				} else {
					System.out.println("Invalid number.");
				}
			} catch (Exception e) {
				System.out.println("Format error. Use 'P1' or 'U1'.");
			}
		}
		case 2 -> {
			System.out.print("Enter Group Number (e.g., GP1 or GU1): ");
			String input = Userinput.nextLine().toUpperCase();
			try {
				String prefix = input.startsWith("GP") ? "GP" : "GU";
				int index = Integer.parseInt(input.substring(prefix.length())) - 1;

				List<groupchat> list = (prefix.equals("GP")) ? grpMap.get("pinned") : grpMap.get("unpinned");

				if (index >= 0 && index < list.size()) {
					groupchat selected = list.get(index);
					grpservice.pingroup(currentuser.getId(), selected.getId(), ChaType.GROUP);
					System.out.println("Group pinned successfully!");
				} else {
					System.out.println("Invalid number.");
				}
			} catch (Exception e) {
				System.out.println("Format error. Use 'GP1' or 'GU1'.");
			}
		}
		case 3 -> {
			return;
		}
		default -> System.out.println("Invalid choice.");
		}
	}

	// LEVEL 4: Private Message View
	static boolean chatwasdeleted = false;

	private static void ViewChat(Chat chat) {
		chatwasdeleted = false;
		boolean backToLevel3 = false;
		while (!backToLevel3) {
			if (chatwasdeleted) {
				backToLevel3 = true;
			}
			System.out.println("\n--------- Messages ---------");
			try {
				List<Message> messages = msgservice.findallmessage(chat.getId(), ChaType.PRIVATE, currentuser.getId());
				if (messages.size() == 0) {
					System.out.println("No messages in this chat");
				} else {
					for (int i = 0; i < messages.size(); i++) {
						System.out.println((i + 1) + ") " + messages.get(i).getMessage_text());
					}

				}

				boolean b = chatservice.isblocked(chat, currentuser.getId());

				if (b) {
					// --- BLOCKED FLOW ---
					System.out.println("\n(This chat is blocked)");
					System.out.println("1) View Chat Info");
					System.out.println("2) Back");
					System.out.print("Enter Option: ");

					int choice = Userinput.nextInt();
					Userinput.nextLine();

					if (choice == 1) {
						if (viewchatinfo(chat, true))
							break;
					} else if (choice == 2) {
						backToLevel3 = true;
					} else {
						System.out.println("Invalid choice.");
					}
				}

				else {
					if (msginfo(chat, false))
						break;
				}
				if (chatwasdeleted)
					break;

			} catch (Exception e) {
				e.printStackTrace();
//                backToLevel3 = true; 
			}
		}

	}

	// LEVEL 5: Action Menu
	private static boolean msginfo(Chat chat, boolean b) {
		if (b) {
			return true;
		}
		System.out.println("\n--- Level 5: Actions ---");
		System.out.println(" 1) Send Message \n 2) Delete Message \n 3) View Chat Info \n 4) Back");
		int choice = Userinput.nextInt();
		Userinput.nextLine();

		switch (choice) {
		case 1 -> sendmessage(chat);
		case 2 -> deletemsg(chat.getId(), ChaType.PRIVATE);
		case 3 -> {
			if (viewchatinfo(chat, false))
				return true;
		}
		case 4 -> {
			return true;
		}
		default -> System.out.println("Invalid choice.");
		}
		return false;
	}

	// --- METHODS ---

	private static void sendmessage(Chat chat) {
		System.out.print("Enter Message: ");
		String text = Userinput.nextLine();
		if (!text.isEmpty()) {
			try {
				Message msg = new Message(chat.getId(), currentuser.getId(), ChaType.PRIVATE, text);
				if (msgservice.sendmessage(msg))
					System.out.println("Sent!");
			} catch (Exception e) {
				logsui.logError("Send failed", e);
			}
		}
	}

	private static boolean viewchatinfo(Chat c, boolean isblocked) {
		int otherId = (c.getUser1_id() == currentuser.getId()) ? c.getUser2_id() : c.getUser1_id();

		try {
			User u = Userservice.findbyid(otherId);
			System.out.println("\n--- Chat Info ---");
			System.out.println("Name: " + u.getName());
			System.out.println("Email: " + u.getEmail());

			if (!isblocked) {
				System.out.println("Options: 1) Block Chat | 2) Delete Chat | 3) Back");
				int choice = Userinput.nextInt();
				Userinput.nextLine();

				if (choice == 1) {
					if (chatservice.blockchat(c.getId(), currentuser.getId())) {
						System.out.println("Chat is blocked.");
					} else {
						System.out.println("Something went wrong.");
					}
				} else if (choice == 2) {
					DeleteChat(c);
					chatwasdeleted = true;
					return true;
				} else {
					return true;
				}
			} else {
				System.out.println("Options: 1) Unblock Chat | 2) Delete Chat | 3) Back");
				int choice = Userinput.nextInt();
				Userinput.nextLine();

				if (choice == 1) {
					if (chatservice.unblockchat(c.getId(), currentuser.getId())) {
						System.out.println("Chat is unblocked.");
					} else {
						System.out.println("Something went wrong.");
					}
				} else if (choice == 2) {
					DeleteChat(c);
					chatwasdeleted = true;
					return true;

				} else {
					System.out.println("Something went wrong.");
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println("Error loading info.");
			logsui.logError("View chat info failed", e);
		}
		return false;

	}

	private static void deletemsg(int chatid, ChaType chattype) {
		try {
			List<Message> messages = msgservice.findallmessage(chatid, chattype, currentuser.getId());
			for (int i = 0; i < messages.size(); i++) {
				System.out.println((i + 1) + ") " + messages.get(i).getMessage_text());
			}

			System.out.print("Enter number to delete: ");
			int idx = Userinput.nextInt();
			Userinput.nextLine();

			if (idx > 0 && idx <= messages.size()) {
				Message m = messages.get(idx - 1);
				MessageDelete deleteType = null;

				if ((m.getSender_id() == currentuser.getId()) && (!(m.isMessage_deleted()))) {

					System.out.println("1) Delete for Me | 2) Delete for Everyone  3| Back");
					int typeChoice = Userinput.nextInt();
					Userinput.nextLine();

					if (typeChoice == 1) {
						deleteType = MessageDelete.DELETEFORME;
					} else if (typeChoice == 2) {
						deleteType = MessageDelete.DELETEFOREVERYONE;
					} else if (typeChoice == 3) {
						System.out.println(" ok have ur msg enjoy reading");
					}

				} else {
					System.out.println("Delete for me .  (1 for Yes / 2 for No)");
					int proceed = Userinput.nextInt();
					Userinput.nextLine();
					if (proceed == 1) {
						deleteType = MessageDelete.DELETEFORME;
					}
				}

				if (deleteType != null) {
					boolean b = msgservice.deletemessage(m, currentuser.getId(), deleteType);
					System.out.println("message" + m.getMessage_text());
					if (b) {
						System.out.println("Message deleted successfully.");
					}
				}
			} else {
				System.out.println("Invalid selection.");
			}
		} catch (Exception e) {
			logsui.logError("Delete failed", e);
		}
	}

	private static void DeleteChat(Chat c) {
		try {
			if (chatservice.deletechat(c.getId(), currentuser.getId())) {
				System.out.println("Chat deleted successfully.");
			}
		} catch (Exception e) {
			logsui.logError("Chat delete failed", e);
		}
	}

	private static void createchat() {
		System.out.print("Enter email to chat with: ");
		String email = Userinput.nextLine();
		try {
			User receiver = Userservice.findbyemail(email);
			if (receiver != null && receiver.getId() != currentuser.getId()) {
				if (!chatservice.ischat(receiver.getId(), currentuser.getId())) {
					chatservice.createNewChat(currentuser.getId(), receiver.getId());
					System.out.println("Chat created!");
				} else
					System.out.println("Chat already exists.");
			} else
				System.out.println("User not found or invalid.");
		} catch (Exception e) {
			logsui.logError("Create chat error", e);
		}
	}

	private static void creategroup() {
		System.out.print("Enter group name: ");
		String name = Userinput.nextLine();
		List<User> members = new ArrayList<>();
		while (true) {
			System.out.print("Enter member email (or 'done'): ");
			String email = Userinput.nextLine();
			if (email.equalsIgnoreCase("done"))
				break;
			try {
				User u = Userservice.findbyemail(email);
				if (u != null)
					members.add(u);
				else
					System.out.println("User not found.");
			} catch (Exception e) {
			}
		}
		try {
			if (grpservice.createNewGroup(name, currentuser.getId(), members))
				System.out.println("Group created!");
		} catch (Exception e) {
			logsui.logError("Group creation failed", e);
		}
	}

	private static void viewgroupMessages(groupchat g) {
		boolean back = false;
		while (!back) {
			try {
				System.out.println("\n--- Group: " + g.getName() + " ---");
				List<Message> messages = msgservice.findallmessage(g.getId(), ChaType.GROUP, currentuser.getId());
				for (int i = 0; i < messages.size(); i++) {
					System.out.println((i + 1) + ") " + messages.get(i).getMessage_text());
				}
				System.out.println("1) Send Message | 2)Delete Message | 3) View Group Info 4) Back");
				int choice = Userinput.nextInt();
				Userinput.nextLine();
				if (choice == 1) {
					System.out.print("Enter Message: ");
					String text = Userinput.nextLine();
					msgservice.sendmessage(new Message(g.getId(), currentuser.getId(), ChaType.GROUP, text));
				} else if (choice == 2) {
					deletemsg(g.getId(), ChaType.GROUP);
				} else if (choice == 3) {
					viewgrpinfo(g);
				} else
					back = true;
			} catch (Exception e) {
				back = true;
			}
		}
	}

	private static void viewgrpinfo(groupchat g) {
		boolean stayInInfo = true;
		while (stayInInfo) {
			System.out.println("\n ------ Group Info: " + g.getName() + " ------ ");
			try {
				List<group_members> grpmembers = grpservice.findgrpmembers(g.getId());
				group_members currentmember = null;

				System.out.println(" ------ Group Members ------ ");
				for (int i = 0; i < grpmembers.size(); i++) {
					group_members gm = grpmembers.get(i);
					if (gm.getRole() == GroupMemberRole.LEFT || gm.getRole() == GroupMemberRole.REMOVED)
						continue;

					User u = Userservice.findbyid(gm.getUser_id());
					if (u.getId() == currentuser.getId()) {
						currentmember = gm;
					}

					String displayName = (u.getId() == currentuser.getId()) ? "You" : u.getEmail();
					System.out.println((i + 1) + ") " + displayName + " - [" + gm.getRole() + "]");
				}

				System.out.println("\n ------ Enter Options ------ ");
				System.out.println(" 1) Exit Group\n 2) Delete Group Chat\n 3) Back");

				boolean isAdmin = (currentmember != null && currentmember.getRole() == GroupMemberRole.ADMIN);
				if (isAdmin) {
					System.out.println(" 4) Add Member\n 5) Remove Member\n 6) Promote Member");
				}

				int choice = Userinput.nextInt();
				Userinput.nextLine();

				switch (choice) {
				case 1 -> {
					if (isAdmin) {
						boolean otherAdminExists = false;
						for (group_members gm : grpmembers) {
							if (gm.getRole() == GroupMemberRole.ADMIN && gm.getUser_id() != currentuser.getId()) {
								otherAdminExists = true;
								break;
							}
						}

						if (!otherAdminExists) {
							System.out.println("Appointing oldest member as new Admin...");
							groupmebersservice.adminleft(g.getId());
						}
					}
					updaterole(currentmember, GroupMemberRole.LEFT);
					System.out.println("You have left the group.");
					stayInInfo = false;
				}
				case 2 -> {
					if (grpservice.deletegrpchat(g.getId(), currentuser.getId())) {
						System.out.println("Chat history cleared for you.");
					}
				}
				case 3 -> stayInInfo = false;
				case 4 -> {
					if (isAdmin)
						addmember(g);
				}
				case 5 -> {
					if (isAdmin) {
						System.out.println("Enter Email to remove:");
						String email = Userinput.nextLine();
						User targetUser = Userservice.findbyemail(email);
						if (targetUser != null) {
							group_members targetMember = groupmebersservice.findrole(targetUser.getId(), g.getId());
							if (targetMember != null) {
								updaterole(targetMember, GroupMemberRole.REMOVED);
								System.out.println(targetUser.getEmail() + " removed.");
							} else
								System.out.println("User not in group.");
						} else
							System.out.println("User not found.");
					}
				}
				case 6 -> {
					if (isAdmin) {
						System.out.println("Enter Email to Promote/Demote:");
						String email = Userinput.nextLine();
						User targetUser = Userservice.findbyemail(email);

						if (targetUser != null) {
							group_members targetMember = groupmebersservice.findrole(targetUser.getId(), g.getId());

							if (targetMember != null) {
								GroupMemberRole newRole;
								if (targetMember.getRole() == GroupMemberRole.ADMIN) {
									newRole = GroupMemberRole.MEMBER;
								} else {
									newRole = GroupMemberRole.ADMIN;
								}

								System.out.println("Change " + targetUser.getEmail() + " to " + newRole + "? (y/n)");
								if (Userinput.nextLine().equalsIgnoreCase("y")) {
									updaterole(targetMember, newRole);
									System.out.println("Role updated successfully.");
								}
							} else {
								System.out.println("User not in group.");
							}
						}
					}
				}
				}
			} catch (Exception e) {
				e.printStackTrace();
				stayInInfo = false;
			}
		}
	}

	private static void addmember(groupchat g) {
		List<group_members> membersToAdd = new ArrayList<>();
		boolean entering = true;

		while (entering) {
			System.out.println("Enter email to add (or type 'done' to save and finish):");
			String email = Userinput.nextLine();

			if (email.equalsIgnoreCase("done")) {
				entering = false;
				break;
			}

			try {
				User u = Userservice.findbyemail(email);
				if (u != null) {
					if (grpservice.ismember(g.getId(), u.getId())) {
						System.out.println("User is already in this group.");
					} else {
						System.out.println("Select Role: 1) Admin | 2) Member");
						int roleChoice = Userinput.nextInt();
						Userinput.nextLine();
						GroupMemberRole role;
						if (roleChoice == 1) {
							role = GroupMemberRole.ADMIN;
						} else {
							role = GroupMemberRole.MEMBER;
						}

						membersToAdd.add(new group_members(g.getId(), u.getId(), role));
						System.out.println("Added " + u.getEmail() + " to the pending list.");
					}
				} else {
					System.out.println("No user found with that email.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (!membersToAdd.isEmpty()) {
			try {
				if (grpservice.addMembersToGroup(membersToAdd)) {
					System.out.println("Successfully added " + membersToAdd.size() + " members!");
				}
			} catch (Exception e) {
				System.out.println("Failed to save members.");
			}
		}
	}

	private static boolean updaterole(group_members gm, GroupMemberRole gr) {
		try {
			gm.setRole(gr);
			return grpservice.updaterole(gm);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static void admininfo() {
		while (currentuser != null && currentuser.getRole() == Role.ADMIN) {
			System.out.println("\n--- Level 2: Admin Menu ---");
			System.out.println(" 1) Add User ");
			System.out.println(" 2) View All Users ");
			System.out.println(" 3) Update Profile ");
			System.out.println(" 4) Logout ");

			int choice = Userinput.nextInt();
			Userinput.nextLine();

			switch (choice) {
			case 1 -> {
				adduser();
			}
			case 2 -> {
				viewAllUsers();
			}
			case 3 -> {
				updateProfile();
			}
			case 4 -> {
				System.out.println(" Bye  tata adminey varata");
				currentuser = null;
			}
			default -> System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	private static void updateProfile() {
		System.out.println("Update Profile - Enter name (space to skip):");
		String name = Userinput.nextLine();
		System.out.println("Enter password (space to skip):");
		String pass = Userinput.nextLine();
		name = name.equals(" ") ? currentuser.getName() : name;
		pass = pass.equals(" ") ? currentuser.getPassword() : pass;
		try {
			Userservice.updateProfile(new User(currentuser.getId(), name, currentuser.getEmail(), pass,
					currentuser.getRole(), currentuser.getOrg_id()));
			System.out.println("Profile updated!");
		} catch (Exception e) {
			logsui.logError("Update failed", e);
		}
	}

	private static void viewAllUsers() {
		boolean backToMenu = false;
		while (!backToMenu) {
			try {
				List<User> users = Userservice.allusers(currentuser.getOrg_id());
				System.out.println("\n--- Level 3: All Users in Organization ---");

				if (users == null || users.isEmpty()) {
					System.out.println("No users found.");
					return;
				}

				for (int i = 0; i < users.size(); i++) {
					System.out.println((i + 1) + ") " + users.get(i).getEmail());
				}

				backToMenu = viewalluserinfo(users);

			} catch (SQLException e) {
				logsui.logError("Error fetching users", e);
				backToMenu = true;
			}
		}
	}

	private static boolean viewalluserinfo(List<User> users) {
		System.out.println("\n Options: 1) View Details | 2) Delete User | 3) Back to Admin Menu");
		int choice = Userinput.nextInt();
		Userinput.nextLine();

		switch (choice) {
		case 1 -> {
			System.out.print("Enter user number to view: ");
			int idx = Userinput.nextInt();
			Userinput.nextLine();
			if (idx > 0 && idx <= users.size()) {
				User u = users.get(idx - 1);
				System.out.println("\n--- User Details ---");
				System.out.println(" Name : " + u.getName());
				System.out.println(" Email: " + u.getEmail());
				System.out.println(" Role : " + u.getRole());
			} else {
				System.out.println("Invalid number.");
			}
			return false;
		}
		case 2 -> {
			System.out.print("Enter user number to delete: ");
			int idx = Userinput.nextInt();
			Userinput.nextLine();
			if (idx > 0 && idx <= users.size()) {
				try {
					Userservice.deleteUser(users.get(idx - 1).getId());
					System.out.println("User deleted successfully.");
				} catch (Exception e) {
					System.out.println("Delete failed.");
				}
			}
			return false;
		}
		case 3 -> {
			return true;
		}
		default -> {
			System.out.println("Invalid option.");
			return false;
		}
		}
	}

	private static void adduser() {
		System.out.print("Username: ");
		String name = Userinput.nextLine();
		System.out.print("Email: ");
		String email = Userinput.nextLine();
		System.out.println("Role: 1) Admin | 2) User");

		int r = Userinput.nextInt();
		Userinput.nextLine();

		Role role;
		if (r == 1) {
			role = Role.ADMIN;
		} else {
			role = Role.USER;
		}

		try {
			Userservice.adduser(name, email, role, currentuser.getOrg_id());
			System.out.println("User added!");
		} catch (Exception e) {
			logsui.logError("Add user failed", e);
			System.out.println("Something went wrong.");
		}
	}

	public static void main(String[] args) {
		Start();
	}
}