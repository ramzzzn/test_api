package ru.stepup.testapi;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.given;

@Slf4j
public class StudentApi {
    private final String BASE_URL = "http://localhost:8080";
    private final String STUDENTS_ENDPOINT = "/student";
    private final Set<Integer> studentsIdsToDelete = new HashSet<>();

    public ValidatableResponse sendPostStudentReq(StudentDto student) {
        return given()
                .log().all()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(student)
                .post(STUDENTS_ENDPOINT).then();
    }

    public int createStudent(StudentDto student) {
        int id = sendPostStudentReq(student).statusCode(201)
                .extract().as(Integer.class);
        studentsIdsToDelete.add(id);
        return id;
    }


    public void createOrUpdateStudentWithId(StudentDto student) {
        sendPostStudentReq(student).statusCode(201);
        studentsIdsToDelete.add(student.getId());
    }

    public ValidatableResponse sendGetStudentReq(int id) {
        return given()
                .log().all()
                .baseUri(BASE_URL)
                .get(STUDENTS_ENDPOINT + "/" + id)
                .then();
    }

    public StudentDto getStudentById(int id) {
        return sendGetStudentReq(id).statusCode(200)
                .extract().as(StudentDto.class);
    }

    public ValidatableResponse sendGetTopStudentReq() {
        return given()
                .log().all()
                .baseUri(BASE_URL)
                .get("topStudent")
                .then().statusCode(200);
    }

    public List<StudentDto> getTopStudent() {
        return sendGetTopStudentReq().extract().jsonPath().getList(".", StudentDto.class);
    }

    public ValidatableResponse sendDeleteStudentReq(int id) {
        return given()
                .log().all()
                .baseUri(BASE_URL)
                .delete(STUDENTS_ENDPOINT + "/" + id)
                .then();
    }

    public void deleteStudentById(Set<Integer> idList) {
        for (int id : idList) {
            sendDeleteStudentReq(id).statusCode(200);
        }
    }

    public void deleteStudents() {
        deleteStudentById(studentsIdsToDelete);
        studentsIdsToDelete.clear();
    }
}
