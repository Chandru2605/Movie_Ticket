package com.zoho.theater.show;

import com.zoho.theater.InvalidException;
import com.zoho.theater.connection.ConnectionUtil;
import com.zoho.theater.movie.MovieAPI;
import com.zoho.theater.screen.ScreenAPI;
import com.zoho.theater.theater.TheaterAPI;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class ShowAPI {
    static Scanner sc = new Scanner(System.in);
    private static void addShows(int scr_id,String date,int mrng,int aftr,int night) throws Exception{
        String query1 = "insert into theater.Show(ScreenID,MovieID,ShowTime,Date) values("+scr_id+",'"+mrng+"',1,'"+date+"')";
        String query2 = "insert into theater.Show(ScreenID,MovieID,ShowTime,Date) values("+scr_id+",'"+aftr+"',2,'"+date+"')";
        String query3 = "insert into theater.Show(ScreenID,MovieID,ShowTime,Date) values("+scr_id+",'"+night+"',3,'"+date+"')";
        ConnectionUtil.insertQuery(query1);
        String getMrngShowID = "Select ShowID from theater.Show where ScreenID ="+scr_id+" and ShowTime = 1 and date = '"+date+"'";
        ResultSet rs1 = ConnectionUtil.selectQuery(getMrngShowID);
        rs1.next();
        int mrng_shw_id = rs1.getInt(1);
        addShowSeat(scr_id,mrng_shw_id);
        ConnectionUtil.insertQuery(query2);
        String getAftrShowID = "Select ShowID from theater.Show where ScreenID ="+scr_id+" and ShowTime = 2 and date = '"+date+"'";
        ResultSet rs2 = ConnectionUtil.selectQuery(getAftrShowID);
        rs2.next();
        int aftr_shw_id = rs2.getInt(1);
        addShowSeat(scr_id,aftr_shw_id);
        ConnectionUtil.insertQuery(query3);
        String getNightShowID = "Select ShowID from theater.Show where ScreenID ="+scr_id+" and ShowTime = 3 and date = '"+date+"'";
        ResultSet rs3 = ConnectionUtil.selectQuery(getNightShowID);
        rs3.next();
        int night_shw_id = rs3.getInt(1);
        addShowSeat(scr_id,night_shw_id);
    }

    public static void add_Shows() throws Exception, InvalidException {
        System.out.println("Enter Date:[yyyy-mm-dd]");
        String date = sc.next();
        TheaterAPI.getTheaterDetails();
        System.out.println("Enter Theater ID:  ");
        int theaterID = sc.nextInt();
        TheaterAPI.checkTheater(theaterID);
        String q = "Select ScreenNumber from Screen where TheaterID = "+theaterID+" ";;
        ResultSet r = ConnectionUtil.selectQuery(q);
        ArrayList<String> screenNames = new ArrayList<>();
        while (r.next()){
            screenNames.add(r.getString(1));
        }
        if(screenNames.size()==0){
            throw new InvalidException("No Screens Available");
        }
            System.out.println("Select Screen Number : " + screenNames);
            int screenNumber = sc.nextInt();
            String query = "Select ScreenID from Screen where ScreenNumber = '" + screenNumber + "' and TheaterID = "+theaterID+" ";
            ResultSet rs = ConnectionUtil.selectQuery(query);
            rs.next();
            int scrId = rs.getInt(1);
            if (ScreenAPI.getScreenBookedStatus(scrId, date)) {
                throw new InvalidException("Already Updated");
            }
            MovieAPI.showMovieDetails();
                System.out.println("Morning Movie ID: ");
                int mrng_mov = sc.nextInt();
                System.out.println("Afternoon Movie ID: ");
                int aftr_mov = sc.nextInt();
                System.out.println("Night Movie ID: ");
                int night_mov = sc.nextInt();
                addShows(scrId, date, mrng_mov, aftr_mov, night_mov);
                System.out.println("Shows updated successfully");

    }
    private static void addShowSeat(int scr_id,int shw_id) throws Exception{
        String getSeatID = "Select SeatID from Seat where ScreenID="+scr_id+" ";
        ResultSet r = ConnectionUtil.selectQuery(getSeatID);
        while (r.next()){
            int seatId = r.getInt(1);
            String q3 = "insert into theater.ShowSeat(ShowID,SeatID) values("+shw_id+","+seatId+")";
            ConnectionUtil.insertQuery(q3);
        }
    }


    public static void main(String[] args) throws Exception, InvalidException {
        add_Shows();

    }
}