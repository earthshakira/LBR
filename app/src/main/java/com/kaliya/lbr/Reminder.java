package com.kaliya.lbr;


public class Reminder {

    private int _id;
    private String _taskname;
    private String _location;
    private String _date;
    private String _time;

    public Reminder() {}

    public Reminder(int _id,String _taskname, String _location, String _date, String _time) {
        this._id = _id;
        this._taskname = _taskname;
        this._location = _location;
        this._date = _date;
        this._time = _time;
    }

    public Reminder( String _taskname, String _location, String _date, String _time ) {
        this._taskname = _taskname;
        this._location = _location;
        this._date = _date;
        this._time = _time;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void set_taskname(String _taskname) {
        this._taskname = _taskname;
    }

    public void set_location(String _location) {
        this._location = _location;
    }

    public void set_date(String _date) {
        this._date = _date;
    }

    public void set_time(String _time) {
        this._time = _time;
    }

    public int get_id() {
        return _id;
    }

    public String get_taskname() {
        return _taskname;
    }

    public String get_location() {
        return _location;
    }

    public String get_date() {
        return _date;
    }

    public String get_time() {
        return _time;
    }
}
