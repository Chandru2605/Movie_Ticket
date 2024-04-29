package com.zoho.theater;

import com.zoho.theater.connection.ConnectionUtil;
import com.zoho.theater.theater.TheaterAPI;

import java.sql.ResultSet;
import java.util.Scanner;

public class RevenueReport {
    static Scanner sc=  new Scanner(System.in);
    public static void revenueReportByDate(int theaterID, String date) throws Exception {
        String q = "SELECT T.TheaterID,T.theaterName,T.location,SUM(B.NoOfSeatsBooked),DATE(B.bookingdate) AS BookingDate,SUM(B.amount) AS Amount FROM Booking B JOIN `Show` SH ON SH.showid = B.showid JOIN Screen SC ON SH.screenid = SC.screenid JOIN Theater T ON SC.theaterid = T.theaterid WHERE DATE(B.bookingdate) = '"+date+"' AND T.TheaterId = "+theaterID+" GROUP BY T.TheaterID, DATE(B.bookingdate);";
        ResultSet r = ConnectionUtil.selectQuery(q);

            System.out.println("ID   Theater    Location    NoOfSeatsBooked    Date    Amount");
            System.out.println("-------------------------------------------------------------");
            while (r.next()){
                System.out.print(r.getInt(1)+"    ");
                System.out.print(r.getString(2)+"        ");
                System.out.print(r.getString(3)+"     ");
                System.out.print(r.getInt(4)+"         ");
                System.out.print(r.getString(5)+"      ");
                System.out.println(r.getInt(6));
            }
    }
    private static void revenueReportByShowTime(int theaterID, String date) throws Exception {
        String q = "select T.Theatername,T.location,Date(B.BookingDate),SH.showtime,SUM(B.NoofSeatsBooked),sum(B.`amount`) from Booking B  JOIN `Show` SH ON B.showid = SH.showid JOIN Screen S ON S.ScreenID = SH.ScreenID JOIN Theater T ON T.TheaterID = S.TheaterID where T.TheaterID = "+theaterID+" and Date(B.BookingDate) = '"+date+"' group by SH.showtime,Date(B.BookingDate);";
        ResultSet r = ConnectionUtil.selectQuery(q);

        System.out.println("Theater    Location    Date    ShowTime    NoOfSeatsBooked    Amount");
        System.out.println("-------------------------------------------------------------");
        while (r.next()){
            System.out.print(r.getString(1)+"    ");
            System.out.print(r.getString(2)+"        ");
            System.out.print(r.getString(3)+"     ");
            System.out.print(r.getString(4)+"         ");
            System.out.print(r.getInt(5)+"      ");
            System.out.println(r.getInt(6));
        }
    }
    private static void revenueReportByScreen(int theaterID, String date) throws Exception {
        String q = "select T.Theatername,T.location,Date(B.BookingDate),S.ScreenNumber,SUM(B.NoofSeatsBooked),sum(B.`amount`) from Booking B  JOIN `Show` SH ON B.showid = SH.showid JOIN Screen S ON S.ScreenID = SH.ScreenID JOIN Theater T ON T.TheaterID = S.TheaterID where T.TheaterID in("+theaterID+")  and Date(B.BookingDate) = '"+date+"' group by S.ScreenNumber,Date(B.BookingDate),T.TheaterID;";
        ResultSet r = ConnectionUtil.selectQuery(q);

        System.out.println("Theater    Location    Date    Screen    NoOfSeatsBooked    Amount");
        System.out.println("---------------------------------------------------------------------");
        while (r.next()){
            System.out.print(r.getString(1)+"    ");
            System.out.print(r.getString(2)+"        ");
            System.out.print(r.getString(3)+"     ");
            System.out.print(r.getInt (4)+"         ");
            System.out.print(r.getInt(5)+"      ");
            System.out.println(r.getInt(6));
        }
    }
    public static void main(String[] args) throws Exception {
        TheaterAPI.getTheaterDetails();
        int theaterID = sc.nextInt();
        String date = "2024-04-29";
        revenueReportByDate(theaterID,date);
        revenueReportByShowTime(theaterID,date);
        revenueReportByScreen(theaterID,date);
    }

}
