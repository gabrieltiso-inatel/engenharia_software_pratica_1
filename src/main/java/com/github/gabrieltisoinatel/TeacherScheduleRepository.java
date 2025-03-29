package com.github.gabrieltisoinatel;

import com.google.gson.Gson;

public class TeacherScheduleRepository {
    private TeacherScheduleDataSource dataSource;

    public TeacherScheduleRepository(TeacherScheduleDataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected TeacherSchedule parseSchedule(String scheduleResponse) {
        Gson gson = new Gson();
        return gson.fromJson(scheduleResponse, TeacherSchedule.class);
    }

    protected TeacherSchedule[] parseSchedules(String schedulesResponse) {
        Gson gson = new Gson();
        return gson.fromJson(schedulesResponse, TeacherSchedule[].class);
    }

    public TeacherSchedule getTeacherSchedule(String teacherName) {
        String scheduleResponse = dataSource.getTeacherSchedule(teacherName);
        TeacherSchedule schedule = parseSchedule(scheduleResponse);

        if (schedule == null) {
            throw new IllegalArgumentException("Schedule not found for teacher: " + teacherName);
        }

        // stripUnnecessaryBuildings(schedule);

        return schedule;
    }

    protected void stripUnnecessaryBuildings(TeacherSchedule schedule) {
        
    }
}
