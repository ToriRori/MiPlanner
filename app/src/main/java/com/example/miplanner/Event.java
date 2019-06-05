package com.example.miplanner;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Event implements Parcelable {

    private int id;
    private String name;
    private String description;
    private String dateStart;
    private String dateEnd;
    private String location;
    private String repeat;
    private String endRepeat;

    private Event(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.dateStart = in.readString();
        this.dateEnd = in.readString();
        this.description = in.readString();
        this.location = in.readString();
        this.repeat = in.readString();
    }

    public Event(int id, String name, String dateStart, String dateEnd)
    {
        this.id = id;
        this.name = name;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }

    public Event(int id, String name, String dateStart, String dateEnd, String description)
    {
        this.id = id;
        this.name = name;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.description = description;
    }

    public Event(int id, String name, String dateStart, String dateEnd, String description, String location)
    {
        this.id = id;
        this.name = name;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.description = description;
        this.location = location;
    }

    public Event(int id, String name, String dateStart, String dateEnd, String description, String location, String repeat)
    {
        this.id = id;
        this.name = name;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.description = description;
        this.location = location;
        this.repeat = repeat;
    }

    public Event(int id, String name, String dateStart, String dateEnd, String description, String location, String repeat, String end_repeat)
    {
        this.id = id;
        this.name = name;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.description = description;
        this.location = location;
        this.repeat = repeat;
        this.endRepeat = end_repeat;
    }


    public int getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDateStart() { return dateStart; }

    public String getDateEnd() { return dateEnd; }

    public String getDescription() {  return description;  }

    public String getLocation () {  return location;  }

    public String getRepeat () {  return repeat;  }

    public String getEndRepeat () {  return endRepeat;   }

    public void setName(String new_name) {
        name = new_name;
    }

    public void setLocation(String location) {  this.location = location;  }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(dateStart);
        dest.writeString(dateEnd);
        dest.writeString(description);
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {

        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
