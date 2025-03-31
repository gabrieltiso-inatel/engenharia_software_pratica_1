package com.github.gabrieltisoinatel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TeacherScheduleRepositoryTest {
    @Mock
    private TeacherScheduleDataSource dataSource;
    
    @InjectMocks
    private TeacherScheduleRepository repository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testParseSchedules() {
        String schedules = """
        [
            {
                "name": "Soned",
                "horarioDeAtendimento": "Segunda a Sexta, das 10h às 11:40h",
                "periodo": "Integral",
                "sala": "Sala 5",
                "predio": [
                    "1",
                    "2",
                    "3",
                    "4",
                    "5"
                ]
            },
            {
                "name": "Renzo",
                "horarioDeAtendimento": "Terça, das 19:30h às 21:10h",
                "periodo": "Noturno",
                "sala": "Sala 10",
                "predio": [
                    "1",
                    "2",
                    "3",
                    "4",
                    "5"
                ]
            }
        ]
        """;

        when(dataSource.getAllTeacherSchedules()).thenReturn(schedules);

        TeacherSchedule[] parsedSchedules = repository.parseSchedules(schedules);
        assertEquals(2, parsedSchedules.length);

        assertEquals("Soned", parsedSchedules[0].getName());
        assertEquals("Renzo", parsedSchedules[1].getName());
    }

    @Test
    public void testParseSchedulesEmpty() {
        String schedules = "[]";

        when(dataSource.getAllTeacherSchedules()).thenReturn(schedules);

        TeacherSchedule[] parsedSchedules = repository.parseSchedules(schedules);
        assertEquals(0, parsedSchedules.length);
    }
    
    @Test
    public void testParseSingleSchedule() {
        String schedule = """
        {
            "name": "Chris",
            "horarioDeAtendimento": "Quinta, das 14h às 16h",
            "periodo": "Vespertino",
            "sala": "Sala 3",
            "predio": ["1", "3"]
        }
        """;

        when(dataSource.getTeacherSchedule("Chris")).thenReturn(schedule);
        
        TeacherSchedule parsedSchedule = repository.parseSchedule(schedule);
        assertEquals("Chris", parsedSchedule.getName());
    }

    @Test
    public void testParseSingleScheduleEmpty() {
        String schedule = "{}";

        when(dataSource.getTeacherSchedule("Chris")).thenReturn(schedule);
        
        TeacherSchedule parsedSchedule = repository.parseSchedule(schedule);
        assertEquals(null, parsedSchedule.getName());
    }

    @Test
    public void testGetTeacherSchedule() {
        String schedule = """
        {
            "name": "Chris",
            "horarioDeAtendimento": "Quinta, das 14h às 16h",
            "periodo": "Vespertino",
            "sala": "Sala 3",
            "predio": ["1", "2", "3", "4", "5"]
        }
        """;

        when(dataSource.getTeacherSchedule("Chris")).thenReturn(schedule);
        
        TeacherSchedule parsedSchedule = repository.getTeacherSchedule("Chris");
        assertEquals(parsedSchedule.getBuilding(), new String[]{"1"});
    }

    @Test
    public void testGetTeacherSchedule_ThrowException() {
        String schedule = """
        {
            "name": "Chris",
            "horarioDeAtendimento": "Quinta, das 14h às 16h",
            "periodo": "Vespertino",
            "sala": "Sala 3",
            "predio": ["1", "2", "3", "4", "5"]
        }
        """;

        when(dataSource.getTeacherSchedule("Chris")).thenReturn(schedule);
        
        try {
            repository.getTeacherSchedule("NonExistentTeacher");
        } catch (IllegalArgumentException e) {
            assertEquals("Schedule not found for teacher: NonExistentTeacher", e.getMessage());
        }
    }

    @Test 
    public void testStripUnnecessaryBuildings() {
        String schedule = """
        {
            "name": "Chris",
            "horarioDeAtendimento": "Quinta, das 14h às 16h",
            "periodo": "Vespertino",
            "sala": "Sala 3",
            "predio": ["1", "2", "3", "4", "5"]
        }
        """;

        when(dataSource.getTeacherSchedule("Chris")).thenReturn(schedule);
        
        TeacherSchedule parsedSchedule = repository.getTeacherSchedule("Chris");
        repository.stripUnnecessaryBuildings(parsedSchedule);
        
        String[] expectedBuildings = {"1"};
        assertEquals(expectedBuildings, parsedSchedule.getBuilding());
    }

    @Test
    public void testStripUnnecessaryBuildings_AnotherInterval() {
        String schedule = """
        {
            "name": "Chris",
            "horarioDeAtendimento": "Quinta, das 14h às 16h",
            "periodo": "Vespertino",
            "sala": "Sala 6",
            "predio": ["1", "2", "3", "4", "5"]
        }
        """;

        when(dataSource.getTeacherSchedule("Chris")).thenReturn(schedule);
        
        TeacherSchedule parsedSchedule = repository.getTeacherSchedule("Chris");
        repository.stripUnnecessaryBuildings(parsedSchedule);
        
        String[] expectedBuildings = {"2"};
        assertEquals(expectedBuildings, parsedSchedule.getBuilding());
    }

    @Test 
    public void testStripUnnecessaryBuildings_InvalidRoom() {
        String schedule = """
        {
            "name": "Chris",
            "horarioDeAtendimento": "Quinta, das 14h às 16h",
            "periodo": "Vespertino",
            "sala": "Sala 30",
            "predio": ["1", "2", "3", "4", "5"]
        }
        """;

        when(dataSource.getTeacherSchedule("Chris")).thenReturn(schedule);
        
        TeacherSchedule parsedSchedule = repository.getTeacherSchedule("Chris");

        assertThrows(IllegalArgumentException.class, () -> {
            repository.stripUnnecessaryBuildings(parsedSchedule);
        });
    }

    @Test
public void testJsonStructure() {
    String jsonResponse = """
    {
        "nomeDoProfessor": "Chris",
        "horarioDeAtendimento": "Quinta, das 14h às 16h",
        "periodo": "Vespertino",
        "sala": "Sala 3",
        "predio": [
            "1",
            "2",
            "3",
            "4",
            "6"
        ]
    }
    """;

    // Parse the JSON response
    Gson gson = new Gson();
    JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

    // Assert the presence of required fields
    assertEquals("Chris", jsonObject.get("nomeDoProfessor").getAsString());
    assertEquals("Quinta, das 14h às 16h", jsonObject.get("horarioDeAtendimento").getAsString());
    assertEquals("Vespertino", jsonObject.get("periodo").getAsString());
    assertEquals("Sala 3", jsonObject.get("sala").getAsString());

    // Assert the "predio" array
    JsonArray predioArray = jsonObject.getAsJsonArray("predio");
    assertEquals(5, predioArray.size());
    assertEquals("1", predioArray.get(0).getAsString());
    assertEquals("2", predioArray.get(1).getAsString());
    assertEquals("3", predioArray.get(2).getAsString());
    assertEquals("4", predioArray.get(3).getAsString());
    assertEquals("6", predioArray.get(4).getAsString());
    }

    
}