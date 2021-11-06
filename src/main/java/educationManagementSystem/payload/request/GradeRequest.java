package educationManagementSystem.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class GradeRequest {

    @NotBlank
    String grade;

    @NotNull
    Integer student;

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Integer getStudent() {
        return student;
    }

    public void setStudent(Integer student) {
        this.student = student;
    }
}
