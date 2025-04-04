package com.github.gabrieltisoinatel;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        int roomNumber = Integer.parseInt(schedule.getRoom().split(" ")[1]);
    
        // Verifica se a sala está no intervalo válido (1 a 30)
        if (roomNumber < 1 || roomNumber > 30) {
            throw new IllegalArgumentException("Número da sala inválido: " + roomNumber);
        }
    
        // Descobre o prédio com base na sala (divisão de 5 em 5 salas por prédio)
        int buildingForRoom = (roomNumber - 1) / 5 + 1;
        String buildingToKeep = String.valueOf(buildingForRoom);
    
        List<String> buildings = new ArrayList<>(Arrays.asList(schedule.getBuilding()));
    
        buildings.removeIf(b -> !b.equals(buildingToKeep));
    
        schedule.setBuilding(buildings.toArray(new String[0]));
    }
    
}
