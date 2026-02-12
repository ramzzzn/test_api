package ru.stepup.testapi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

public class StudentTests extends BaseStudentTest {

    @Test
    @DisplayName("1. Получение информации о студенте по ID")
    public void testGetStudentById() {
        StudentDto student = new StudentDto()
                .setName("Peter Parker")
                .setMarks(List.of(5, 4, 5));
        int createdStudentId = studentApi.createStudent(student);
        StudentDto createdStudent = studentApi.getStudentById(createdStudentId);
        assertThat("Имя студента не совпадает", createdStudent.getName(), equalTo(student.getName()));
        assertThat("Список оценок студента не совпадает", createdStudent.getMarks(), equalTo(student.getMarks()));
    }

    @Test
    @DisplayName("2. Получение информации о студенте по несуществующему ID")
    public void testGetStudentByNonExistingId() {
        studentApi.sendGetStudentReq(NOT_EXIST_ID).statusCode(404);
    }

    @Test
    @DisplayName("3. Создание нового студента c предзаполненным Id")
    public void testCreateStudent() {
        StudentDto student = new StudentDto()
                .setId(1)
                .setName("Harry Osborn")
                .setMarks(List.of(4, 4, 4));
        studentApi.createOrUpdateStudentWithId(student);
        StudentDto createdStudent = studentApi.getStudentById(student.getId());
        assertThat("Id студента не совпадает", createdStudent.getId(), equalTo(student.getId()));
        assertThat("Имя студента не совпадает", createdStudent.getName(), equalTo(student.getName()));
        assertThat("Список оценок студента не совпадает", createdStudent.getMarks(), equalTo(student.getMarks()));
    }

    @Test
    @DisplayName("4. Обновление информации о студенте по Id")
    public void testUpdateStudentById() {
        StudentDto student = new StudentDto()
                .setId(3)
                .setName("Norman Osborn")
                .setMarks(List.of(5, 5, 5));
        studentApi.createOrUpdateStudentWithId(student);
        student.setName("Green Hoblin").setMarks(List.of(2, 2, 2));
        studentApi.createOrUpdateStudentWithId(student);
        StudentDto updatedStudent = studentApi.getStudentById(student.getId());
        assertThat("Id студента не совпадает", updatedStudent.getId(), equalTo(student.getId()));
        assertThat("Имя студента не совпадает", updatedStudent.getName(), equalTo(student.getName()));
        assertThat("Список оценок студента не совпадает", updatedStudent.getMarks(), equalTo(student.getMarks()));
    }

    @Test
    @DisplayName("5. Создание студента с указанием его Id")
    public void testCreateStudentWithId() {
        StudentDto student = new StudentDto()
                .setName("Mary Jane Watson")
                .setMarks(List.of(5, 5, 5));
        int createdStudentId = studentApi.createStudent(student);
        StudentDto createdStudent = studentApi.getStudentById(createdStudentId);
        assertThat("Имя студента не совпадает", createdStudent.getName(), equalTo(student.getName()));
        assertThat("Список оценок студента не совпадает", createdStudent.getMarks(), equalTo(student.getMarks()));
    }

    @Test
    @DisplayName("6. Создание студента без указания имени")
    public void testCreateStudentWithoutName() {
        StudentDto student = new StudentDto()
                .setId(6)
                .setMarks(List.of(4, 5, 5));
        studentApi.sendPostStudentReq(student).statusCode(400);
    }

    @Test
    @DisplayName("7. Удаление существующего студента по id")
    public void testDeleteExistingStudentById() {
        StudentDto student = new StudentDto()
                .setId(7)
                .setName("Eddie Brook")
                .setMarks(List.of(4, 3, 4));
        studentApi.sendPostStudentReq(student).statusCode(201);
        studentApi.deleteStudentById(Set.of(student.getId()));
        studentApi.sendGetStudentReq(student.getId()).statusCode(404);
    }

    @Test
    @DisplayName("8. Удаление несуществующего студента по id")
    public void testDeleteNonExistingStudentById() {
        studentApi.sendDeleteStudentReq(NOT_EXIST_ID).statusCode(404);
    }

    @Test
    @DisplayName("9. Получение лучшего студента с пустой базой студентов")
    public void testGetTopStudentWithEmptyDatabase() {
        String responseBody = studentApi.sendGetTopStudentReq().extract().body().asString();
        assertThat("Тело ответа не пустое", responseBody, equalTo(""));
    }

    @Test
    @DisplayName("10. Получение лучшего студента с базой студентов без оценок")
    public void testGetTopStudentWithDatabaseWithoutMarks() {
        StudentDto student = new StudentDto()
                .setId(7)
                .setName("Eddie Brook");
        studentApi.createOrUpdateStudentWithId(student);
        String responseBody = studentApi.sendGetTopStudentReq().extract().body().asString();
        assertThat("Тело ответа не пустое", responseBody, equalTo(""));
    }

    @Test
    @DisplayName("11.1 Получение лучшего студента с максимальной средней оценкой")
    public void testGetTopStudent() {
        StudentDto expectedTopStudent = new StudentDto()
                .setId(1)
                .setName("Peter Parker")
                .setMarks(List.of(5, 5, 5));
        StudentDto student2 = new StudentDto()
                .setId(2)
                .setName("Harry Osborn")
                .setMarks(List.of(4, 5, 4));
        StudentDto student3 = new StudentDto()
                .setId(3)
                .setName("Eddie Brook")
                .setMarks(List.of(4, 4, 3));
        List.of(expectedTopStudent, student2, student3).forEach(studentApi::createOrUpdateStudentWithId);
        List<StudentDto> topStudentListFromResp = studentApi.getTopStudent();
        assertThat("Тело ответа пустое", topStudentListFromResp, notNullValue());
        assertThat("Размер списка студентов в ответе не равен 1", topStudentListFromResp, hasSize(1));
        StudentDto topStudent = topStudentListFromResp.get(0);
        assertThat("Id студента из ответа не совпадает с id лучшего студента",
                topStudent.getId(), equalTo(expectedTopStudent.getId()));
        assertThat("Имя студента из ответа не совпадает с именем лучшего студента",
                topStudent.getName(), equalTo(expectedTopStudent.getName()));
        assertThat("Оценки студента из ответа не совпадают с оценками лучшего студента",
                topStudent.getMarks(), equalTo(expectedTopStudent.getMarks()));
    }

    @Test
    @DisplayName("11.2 Получение лучшего студента с максимальной средней оценкой и максимальным количеством оценок")
    public void testGetTopStudentWithMaxCountOfMarks() {
        StudentDto expectedTopStudent = new StudentDto()
                .setId(1)
                .setName("Otto Octavius")
                .setMarks(List.of(5, 5, 5, 5));
        StudentDto student2 = new StudentDto()
                .setId(2)
                .setName("Norman Osborn")
                .setMarks(List.of(5, 5, 5));
        StudentDto student3 = new StudentDto()
                .setId(3)
                .setName("J. Jonah Jameson")
                .setMarks(List.of(5, 5));
        List.of(expectedTopStudent, student2, student3).forEach(studentApi::createOrUpdateStudentWithId);
        List<StudentDto> topStudentListFromResp = studentApi.getTopStudent();
        assertThat("Тело ответа пустое", topStudentListFromResp, notNullValue());
        assertThat("Размер списка студентов в ответе не равен 1", topStudentListFromResp, hasSize(1));
        StudentDto topStudent = topStudentListFromResp.get(0);
        assertThat("Id студента из ответа не совпадает с id лучшего студента",
                topStudent.getId(), equalTo(expectedTopStudent.getId()));
        assertThat("Имя студента из ответа не совпадает с именем лучшего студента",
                topStudent.getName(), equalTo(expectedTopStudent.getName()));
        assertThat("Оценки студента из ответа не совпадают с оценками лучшего студента",
                topStudent.getMarks(), equalTo(expectedTopStudent.getMarks()));
    }

    @Test
    @DisplayName("12. Получение списка лучших студентов с максимальной средной оценкой")
    public void testGetListOfTopStudents() {
        StudentDto topStudent = new StudentDto()
                .setId(1)
                .setName("Peter Parker")
                .setMarks(List.of(5, 5, 5));
        StudentDto topStudent2 = new StudentDto()
                .setId(2)
                .setName("Harry Osborn")
                .setMarks(List.of(5, 5, 5));
        StudentDto student3 = new StudentDto()
                .setId(3)
                .setName("Eddie Brook")
                .setMarks(List.of(4, 4, 3));
        List<StudentDto> expectedTopStudents = List.of(topStudent, topStudent2);
        expectedTopStudents.forEach(studentApi::createOrUpdateStudentWithId);
        studentApi.createOrUpdateStudentWithId(student3);
        List<StudentDto> topStudentsFromResp = studentApi.getTopStudent();
        assertThat("Тело ответа пустое", topStudentsFromResp, notNullValue());
        assertThat("Полученное количество студентов не равно ожидаемому", topStudentsFromResp.size(), equalTo(expectedTopStudents.size()));
        topStudentsFromResp.forEach(topStudentFromResp -> expectedTopStudents.forEach(expectedTopStudent -> {
            boolean matches = expectedTopStudents.stream()
                    .anyMatch(expectedStudent ->
                            expectedStudent.getId().equals(topStudentFromResp.getId()) &&
                                    expectedStudent.getName().equals(topStudentFromResp.getName()) &&
                                    expectedStudent.getMarks().equals(topStudentFromResp.getMarks())
                    );
            assertThat("Студент не найден среди ожидаемых", matches, equalTo(true));
        }));
    }
}