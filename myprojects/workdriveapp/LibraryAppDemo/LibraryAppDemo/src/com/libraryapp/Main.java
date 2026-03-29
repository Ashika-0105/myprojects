package com.libraryapp;

import java.util.List;
import java.util.Scanner;

import com.libraryapp.dao.BookDAO;
import com.libraryapp.dao.BookDAOImpl;
import com.libraryapp.model.Book;
import com.libraryapp.model.Borrower;
import com.libraryapp.service.BookService;

public class Main {
	public static final Scanner USER_INPUT = new Scanner(System.in);
	public static BookDAO bookdao = new BookDAOImpl();
	public static BookService bookservice= new BookService();

	public static void main(String[] args) {

		while (true) {
			System.out.println("1. View books\n 2. Borrow book\n 3. Return book\n 4. Exit\n");
			System.out.println("Enter input");
			int choice = USER_INPUT.nextInt();

			switch (choice) {
			case 1:
				break;

			case 2:
				try {
					List<Book> list = bookdao.findAll();
					int i = 0;

					for (Book b : list) {

						i++;
						System.out.println(i + b.getTitle());

					}
					
					System.out.println("Choos one book");
					int selectedBookIndex = USER_INPUT.nextInt();
					USER_INPUT.nextLine();
					System.out.println("Enter your name:");
					String userName= USER_INPUT.nextLine();
					int id= list.get(selectedBookIndex-1).getBookId();
					
					Borrower borrower= new Borrower();
					borrower.setBookId(id);
					borrower.setBorrowerName(userName);
					
					
					bookservice.borrow(borrower);
					
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}
}
