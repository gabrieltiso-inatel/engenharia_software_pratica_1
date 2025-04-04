package com.github.gabrieltisoinatel;

public class TeacherSchedule {

    private String name;
    private String officeHours;
    private String period;
    private String room;
    private String[] building;

    public TeacherSchedule(String name, String officeHours, String period, String room, String[] building) {
        this.name = name;
        this.officeHours = officeHours;
        this.period = period;
        this.room = room;
        this.building = building;
    }

    public String getName() {
        return name;
    }

    public String getOfficeHours() {
        return officeHours;
    }

    public String getPeriod() {
        return period;
    }

    public String getRoom() {
        return room;
    }

    public String[] getBuilding() {
        return building;
    }

    public void setBuilding(String[] building) {
        this.building = building;
    }

}
