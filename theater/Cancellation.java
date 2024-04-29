package com.zoho.theater;

import com.zoho.theater.connection.ConnectionUtil;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Cancellation {
    static Scanner sc = new Scanner(System.in);

    private static void updateBookingSeat(ArrayList<Integer> bookingSeatIDs) throws Exception{
        for(Integer i:bookingSeatIDs){
            String q = "UPDATE BookingSeat SET status = 2 WHERE bookingseatID = "+i+";";
            ConnectionUtil.insertQuery(q);
        }
    }
    private static void updateShowSeat(ArrayList<Integer> bookingSeatID) throws Exception{
        for(Integer i:bookingSeatID){
            String q = "UPDATE ShowSeat AS SS JOIN BookingSeat AS BS ON SS.showseatID = BS.showseatID SET SS.status = 0 WHERE BS.bookingseatID = "+i+";";
            ConnectionUtil.insertQuery(q);
        }
    }
    private static void addCancellation(int bookingID, int noOfSeats, int amount, int showID) throws Exception{
        Timestamp datetime = new Timestamp(new Date().getTime());
        String query = "insert into Cancellation(BookingID,CancellationDate,NoOfSeatsCancelled,RefundedAmount,ShowID) values("+bookingID+",'"+ datetime +"',"+noOfSeats+","+amount+","+showID+")";
        ConnectionUtil.insertQuery(query);
    }
    public static void cancelSeats() throws Exception {
        System.out.println("Enter Booking ID: ");
        int bookingID  = sc.nextInt();
        showBookingDetails(bookingID);
        System.out.println("No of Seats to Cancel: ");
        int no_of_seats = sc.nextInt();
        System.out.println("Enter IDs: ");
        ArrayList<Integer> bookingSeatIdToCancel = new ArrayList<>();
        for(int i=0;i<no_of_seats;i++){
            bookingSeatIdToCancel.add(sc.nextInt());
        }
        int refunded_amount = getRefundAmount(bookingSeatIdToCancel);
        int showID = getShowID(bookingID);
        addCancellation(bookingID,no_of_seats,refunded_amount,showID);
        updateBookingSeat(bookingSeatIdToCancel);
        updateShowSeat(bookingSeatIdToCancel);
        System.out.println("Camcelled Successfully");
        System.out.println("Refunded Amount: "+refunded_amount);
    }

    private static int getShowID(int bookingID) throws Exception {
        String q = "Select showID from Booking where BookingID = "+bookingID+"";
        ResultSet r = ConnectionUtil.selectQuery(q);
        r.next();
        return r.getInt(1);
    }

    private static int getRefundAmount(ArrayList<Integer> bookingSeatIdToCancel) throws Exception {
        int refAmount = 0;
        for(Integer i:bookingSeatIdToCancel){
            String q = "SELECT Seat.Class FROM BookingSeat  INNER JOIN Booking ON Booking.BookingID = BookingSeat.BookingID INNER JOIN ShowSeat ON BookingSeat.ShowSeatID = ShowSeat.ShowSeatID INNER JOIN Seat ON ShowSeat.SeatID = Seat.SeatID where BookingSeat.BookingSeatID = "+i+" ";
            ResultSet r = ConnectionUtil.selectQuery(q);
            r.next();
            if(r.getInt(1)==2){
                refAmount += (150) - (150)*10/100;
            }
            else{
                refAmount += (200) - (200)*10/100;
            }
        }
        return refAmount;
    }

    private static void showBookingDetails(int bookingID) throws Exception {
        String q = "SELECT BookingSeat.BookingSeatID,Seat.SeatNumber,Seat.Class,BookingSeat.Status FROM BookingSeat  INNER JOIN Booking ON Booking.BookingID = BookingSeat.BookingID INNER JOIN ShowSeat ON BookingSeat.ShowSeatID = ShowSeat.ShowSeatID INNER JOIN Seat ON ShowSeat.SeatID = Seat.SeatID where Booking.BookingID = "+bookingID+" ";
        ResultSet r = ConnectionUtil.selectQuery(q);
        System.out.println("ID    SeatNo     Class     Status");
        System.out.println("---------------------------------");
        while (r.next()){
            System.out.println(r.getInt(1)+"      "+r.getInt(2)+"          "+r.getInt(3)+"          "+r.getString(4));
        }
    }

    public static void main(String[] args) throws Exception {
        cancelSeats();
    }
}
