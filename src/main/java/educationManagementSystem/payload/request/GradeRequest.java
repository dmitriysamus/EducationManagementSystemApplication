package educationManagementSystem.payload.request;

import javax.validation.constraints.NotBlank;

public class GradeRequest {

    @NotBlank
    String grade;

    @NotBlank
    Integer studentId;

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }
}
