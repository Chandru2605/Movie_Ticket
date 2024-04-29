package com.zoho.theater.screen;

import com.zoho.theater.InvalidException;
import com.zoho.theater.connection.ConnectionUtil;
import com.zoho.theater.theater.TheaterAPI;

import java.sql.ResultSet;
import java.util.Scanner;

public class ScreenAPI {
    static Scanner sc = new Scanner(System.in);
    private static int getScreenID(int screenNumber,int ticketId) throws Exception{
        String query = "Select ScreenID from Screen where ScreenNumber = "+screenNumber+" and TheaterID = "+ticketId+" ";
        ResultSet rs = ConnectionUtil.selectQuery(query);
        if(rs.next()){
            return (rs.getInt(1));
        }
        else{
            return -1;
        }
    }
    private static void addSeats(int screenId,int noOfSeats,int noOfSecondClassSeats,int noOfFirstClassSeats) throws Exception{
        for(int i=1;i<=noOfSecondClassSeats;i++){
            String query = "insert into Seat(ScreenID,SeatNumber,Class) values("+screenId+","+i+",2)";
            ConnectionUtil.insertQuery(query);
        }
        for(int i=noOfSecondClassSeats+1;i<=noOfSeats;i++){
            String query = "insert into Seat(ScreenID,SeatNumber,Class) values("+screenId+","+i+",1)";
            ConnectionUtil.insertQuery(query);
        }
    }
    public static boolean getScreenBookedStatus(int scrId, String date) throws Exception{
        String q = "Select * from theater.Show where ScreenID = "+scrId+" and Date = '"+date+"' ";;
        ResultSet r = ConnectionUtil.selectQuery(q);
        if(r.next()){
            return true;
        }
        else{
            return false;
        }
    }
    private static int addScreen(int screenNumber,int theaterID) throws Exception{
        String query = "insert into Screen(ScreenNumber,TheaterID) values('"+screenNumber+"',"+theaterID+")";
        ConnectionUtil.insertQuery(query);
        return getScreenID(screenNumber,theaterID);
    }
    public static void add_Screen() throws Exception,InvalidException {
        TheaterAPI.getTheaterDetails();
        System.out.println("Enter Theater ID: ");
        int theaterID = sc.nextInt();
        if(!TheaterAPI.checkTheater(theaterID)){
            throw new InvalidException("Invalid Theater ID");
        }
        TheaterAPI.showScreensInTheater(theaterID);
            System.out.println("Screen number to add: ");
            int screenNumber = sc.nextInt();
            String query = "Select ScreenID from Screen where ScreenNumber = '" + screenNumber + "' and TheaterID = " + theaterID + " ";
            ResultSet rs = ConnectionUtil.selectQuery(query);
            if (rs.next()) {
                throw new InvalidException("Theater already have this screen");
            }
                System.out.println("No of Seats: ");
                int noOfSeats = sc.nextInt();
                System.out.println("No of Second Class Seats: ");
                int noOfSecondClassSeats = sc.nextInt();
                System.out.println("No of First Class Seats: ");
                int noOfFirstClassSeats = sc.nextInt();
                int scr_id = addScreen(screenNumber, theaterID);
                addSeats(scr_id, noOfSeats, noOfSecondClassSeats,noOfFirstClassSeats);
                System.out.println("Screen " + screenNumber + " added successfully for Theater ");
    }

    public static void main(String[] args) throws InvalidException, Exception {
        add_Screen();
    }
}
