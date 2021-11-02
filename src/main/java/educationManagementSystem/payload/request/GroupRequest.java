package educationManagementSystem.payload.request;

import javax.validation.constraints.NotNull;

public class GroupRequest {

    @NotNull
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
