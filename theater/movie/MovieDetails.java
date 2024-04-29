package com.zoho.theater.movie;

public class MovieDetails {
    public int ShowID;
    public String MovieName;
    public String Date;
    public int ScreenNumber;
    public String ShowTime;
    public String TheaterName;
    public String Location;
    public MovieDetails(int shwID, String mName, String date, int scrNumber, String showTime, String tName, String location){
        this.ShowID = shwID;
        this.MovieName = mName;
        this.Date = date;
        this.ScreenNumber = scrNumber;
        this.ShowTime =showTime ;
        this.TheaterName = tName;
        this.Location =location;
    }
}
