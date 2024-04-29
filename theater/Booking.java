package com.zoho.theater;

import com.zoho.theater.connection.ConnectionUtil;
import com.zoho.theater.movie.MovieDetails;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

public class Booking {
    static Scanner sc = new Scanner(System.in);
    public static void showAvailableSeatsToBook(int showID) throws Exception {
        String query = "SELECT ShowSeat.ShowSeatID,Seat.seatNumber,Seat.class,ShowSeat.status FROM ShowSeat INNER JOIN Seat ON ShowSeat.seatID = Seat.seatID WHERE ShowSeat.showID = "+showID+";";
        ResultSet rs = ConnectionUtil.selectQuery(query);
        System.out.println("ID   SeatNumber   Class    Status");
        System.out.println("---------------------------------");
        while (rs.next()){
            System.out.print(rs.getInt(1)+"       ");
            System.out.print(rs.getInt(2)+"        ");
            System.out.print(rs.getInt(3)+"    ");
            if(rs.getInt(4)==0){
                System.out.println("Availabele");
            }
            else{
                System.out.println("Booked");
            }
        }
    }
    private static ArrayList<MovieDetails> getMovies() throws Exception {
        String q = "SELECT  S.SHowID,M.title AS movie_title,S.Date,SC.ScreenNumber,S.ShowTime,T.TheaterName,T.Location FROM Movie M INNER JOIN `Show` S ON M.MovieID = S.movieID INNER JOIN Screen SC ON S.screenID = SC.screenID INNER JOIN Theater T ON SC.theaterID = T.theaterID order by M.title;";
        ArrayList<MovieDetails> movies = new ArrayList<>();
        ResultSet rs = ConnectionUtil.selectQuery(q);
        while (rs.next()){
            int shwID = rs.getInt(1);
            String name = rs.getString(2);
            String date = rs.getString(3);
            int scrNum = rs.getInt(4);
            String shwTime = rs.getString(5);
            String tname = rs.getString(6);
            String loc = rs.getString(7);
            MovieDetails md = new MovieDetails(shwID,name,date,scrNum,shwTime,tname,loc);
            movies.add(md);
        }
        return movies;
    }

    private static void setBooking(ArrayList<Integer> showSeatIDs,int showID) throws Exception {
        int amount = getAmount(showSeatIDs);
        Timestamp date = new Timestamp(new Date().getTime());
        System.out.println(date);
        String query1 = "insert into Booking(NoOfSeatsBooked,BookingDate,Amount,ShowID) values("+showSeatIDs.size()+",'"+date+"',"+amount+","+showID+")";
        ConnectionUtil.insertQuery(query1);
        int bookingID = getLatestBookingID();
        for(Integer i:showSeatIDs){
            addBookingSeatEntry(bookingID,i);
        }
    }

    private static int getAmount(ArrayList<Integer> showSeatIDs) throws Exception {
        String q = "SELECT Seat.class FROM ShowSeat INNER JOIN Seat ON ShowSeat.seatID = Seat.seatID WHERE ShowSeat.showseatid in ";
        StringBuffer ids = new StringBuffer("(");
        int a = 0;
        for(Integer i:showSeatIDs){
            if(a==1){
                ids.append(",");
            }
            ids.append(i);
            a = 1;
        }
        ids.append(")");
        String query = q + ids;
        int amount = 0;
        ResultSet rs = ConnectionUtil.selectQuery(query);
        while (rs.next()){
            if(rs.getInt(1)==2){
                amount+=150;
            }
            else{
                amount+=200;
            }
        }
        return amount;
    }

    private static void addBookingSeatEntry(int bookingID, Integer i) throws Exception {
        String query = "insert into BookingSeat(BookingID,ShowSeatID,Status) values("+bookingID+","+i+",1)";
        ConnectionUtil.insertQuery(query);
        setShowSeat(i);
    }
    private static void setShowSeat(int shw_seat_id)  throws Exception {
        String q2 = "Update ShowSeat set Status = 1 where ShowSeatID = "+shw_seat_id+" ";
        ConnectionUtil.insertQuery(q2);
    }
    private static int getLatestBookingID() throws Exception {
        String q = "Select * from Booking ORDER BY BookingID DESC LIMIT 1";
        ResultSet rs = ConnectionUtil.selectQuery(q);
        rs.next();
        int bookingID = rs.getInt(1);
        return bookingID;
    }
    private static void showMovies(ArrayList<MovieDetails> m) {
        System.out.println("ID   Title       Date      Screen     ShowTime   Theater    Location");
        System.out.println("---------------------------------------------------------------");
        for (int i=0;i<m.size();i++){
            System.out.println(m.get(i).ShowID +"   "+m.get(i).MovieName +"     "+m.get(i).Date +"     "+m.get(i).ScreenNumber +"     "+m.get(i).ShowTime +"     "+m.get(i).TheaterName +"     "+m.get(i).Location +" ");
        }
    }
    public static void bookTickets() throws Exception {
       ArrayList<MovieDetails> movies = getMovies();
       showMovies(movies);
       System.out.println("Show ID: ");
       int show_id = sc.nextInt();
       showAvailableSeatsToBook(show_id);
        System.out.println("Enter number of tickets of book: ");
        int n = sc.nextInt();
        System.out.println("Enter IDs: ");
        ArrayList<Integer> showSeatIDs = new ArrayList<>();
        for(int i=0;i<n;i++){
            showSeatIDs.add( sc.nextInt());
        }
        setBooking(showSeatIDs,show_id);
        printDetails(showSeatIDs);
    }

    private static void printDetails(ArrayList<Integer> showSeatIDs) throws Exception {
        System.out.println("Ticket Booked Successfully");
        String q = "Select M.title,T.Theatername,T.location,S.SHowTime,SR.ScreenNumber FROM ShowSeat SS JOIN `Show` S on S.showid = SS.showID JOIN Movie M on S.movieID = M.movieID JOIN Screen SR on SR.screenid = S.screenID JOIN theater T on T.TheaterID = SR.theaterID where SS.Showseatid = "+showSeatIDs.get(0)+";";
        ResultSet r = ConnectionUtil.selectQuery(q);
        r.next();
        System.out.println("Booking ID: "+getLatestBookingID());
        System.out.println("Movie Name: "+r.getString(1));
        System.out.println("Theater: "+r.getString(2));
        System.out.println("Location: "+r.getString(3));
        System.out.println("ShowTime: "+r.getString(4));
        System.out.println("Screen: "+r.getInt(5));
        System.out.println("Seat Numbers: "+getSeatNumbers(showSeatIDs));
    }

    private static ArrayList<Integer> getSeatNumbers(ArrayList<Integer> showSeatIDs) throws Exception {
        ArrayList<Integer> seatNos = new ArrayList<>();
        for(Integer i:showSeatIDs){
            String q = "Select S.SeatNumber FROM ShowSeat SS JOIN Seat S ON SS.seatID = s.seatID where SS.Showseatid = "+i+";";
            ResultSet r = ConnectionUtil.selectQuery(q);
            r.next();
            seatNos.add(r.getInt(1));
        }
        return seatNos;
    }

    public static void main(String[] args) throws Exception {
        bookTickets();
    }

}
