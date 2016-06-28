package com.example.hyon.loyce;

import android.location.Location;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

import android.os.Parcelable;
import android.os.Parcel;
/**
 * Created by Hyon on 2016-04-09.
 */
public class Building implements Parcelable/*객체를 인텐트로 보내기 위해서*/{

    String _name;
    int _floor;//전체 층(지하+지상)
    int _bFloor;//지하
    String _map[];
    double _longitude;
    double _latitude;
    Location _location;

    public Building(String name, int floor, int bFloor, String map[], double latitude, double longitude){
        _name = name;
        _floor = floor;
        _bFloor = bFloor;
        _map = map;
        _longitude = longitude;
        _latitude = latitude;
        _location = new Location("");
        _location.setLatitude(latitude);
        _location.setLongitude(longitude);
    }

    public Building(String name, int floor, int bFloor, double latitude, double longitude){
        _name = name;
        _floor = floor;
        _bFloor = bFloor;
        _map = new String[floor];
        _longitude = longitude;
        _latitude = latitude;
        _location = new Location("");
        _location.setLatitude(latitude);
        _location.setLongitude(longitude);
    }

    public void setBuilding(String map[]){
        _map = map;
    }

    public String get_name(){
        return _name;
    }
    public int get_floor(){
        return _floor;
    }
    public int get_bFloor(){
        return _bFloor;
    }
    public String[] get_map(){
        return _map;
    }
    public  double get_latitude(){
        return _latitude;
    }
    public double get_longitude(){
        return _longitude;
    }

    public boolean checkInside(Location currentLocation){//현재 위치 좌표를 받아서 이 건물이 반경 내에 있는지 없는지 확인
        float distanceInMeters =  currentLocation.distanceTo(_location);
        Log.e(get_name()," : "+distanceInMeters);
        if (distanceInMeters < 50000.0)//50미터 기준
            return true;
        else
            return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    ArrayList<String> tmpArrya;
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        /*
        *     String _name;
    int _floor;//전체 층(지하+지상)
    int _bFloor;//지하
    String _map[];
    double _longitude;
    double _latitude;
    Location _location;
        * */


        dest.writeString(_name);
        dest.writeInt(_floor);
        dest.writeInt(_bFloor);
        dest.writeStringArray(_map);
        dest.writeDouble(_longitude);
        dest.writeDouble(_latitude);
        dest.writeValue(_location);
    }




    private void readFromParcel(Parcel in){
        _name = in.readString();
        _floor = in.readInt();
        _bFloor = in.readInt();
        _map = in.createStringArray();
        _longitude = in.readDouble();
        _latitude = in.readDouble();
        _location = Location.class.cast(in.readValue(Location.class.getClassLoader()));
        //aParcelableClass = in.readParcelable(ParcelableClass.class.getClassLoader());
    }

    public Building(Parcel in) {
        readFromParcel(in);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Building createFromParcel(Parcel in) {
            return new Building(in);
        }

        public Building[] newArray(int size) {
            return new Building[size];
        }
    };
}
