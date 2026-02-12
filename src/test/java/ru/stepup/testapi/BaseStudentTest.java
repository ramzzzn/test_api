package ru.stepup.testapi;

import org.junit.jupiter.api.AfterEach;

public class BaseStudentTest {

    protected static final int NOT_EXIST_ID = -1;
    protected static final StudentApi studentApi = new StudentApi();

    @AfterEach
    public void deleteAllStudents() {
        studentApi.deleteStudents();
    }
}