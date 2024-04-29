package com.zoho.theater;

import com.zoho.theater.connection.ConnectionUtil;

import java.sql.ResultSet;
import java.util.Scanner;

public class UserAPI {
    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) throws Exception, InvalidException {

        System.out.println("Welcome to Movie Ticket Booking System");
        System.out.println("1.New user\n2.Old user");
        int userChoice = sc.nextInt();
        switch (userChoice){
            case 1:
            {
                registerUser();
            }
            break;
            case 2:
            {

            }
            default:
            {
                System.out.println("Wrong Choice");
            }
            break;
        }
    }

    private static void registerUser() throws Exception, InvalidException {
        System.out.println("Registration: ");
        System.out.println("Enter Email: ");
        String email = sc.next();
        boolean emailExists = checkEmailExists(email);
        if(emailExists){
            throw new InvalidException("User already exists");
        }
        System.out.println("Set Password: ");
        String pass = sc.next();
        System.out.println("Confirm Password: ");
        String c_pass = sc.next();
        if(pass.equals(c_pass)){
            String q = "insert into user(userID,password) values('"+email+"','"+pass+"')";
            int r = ConnectionUtil.insertQuery(q);
            System.out.println(r);
        }
        else{
            System.out.println("Password Mismatch");
        }
    }

    private static boolean checkEmailExists(String email) throws Exception {
        String q = "select * from user where userid = '"+email+"';";
        ResultSet r = ConnectionUtil.selectQuery(q);
        if(r.next()){
            return true;
        }
        else{
            return false;
        }
    }
}
