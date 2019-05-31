package common.preconditions;

import com.stasdev.backend.entitys.ApplicationUser;
import common.ApiFunctions;
import common.PreCondition;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CreateUser implements PreCondition{

    private String userName;
    private ApiFunctions apiFunctions;

    public CreateUser(String userName, ApiFunctions apiFunctions) {
        this.apiFunctions = apiFunctions;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public CreateUser setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    @Override
    public void execute() {
        apiFunctions.createUserByAdmin(userName);
        apiFunctions.checkUserExists(userName);
    }

    @Override
    public void undo() {
        apiFunctions.authAdmin()
                .restClientWithErrorHandler()
                .delete("/users?username="+userName);
        apiFunctions.checkUserNotExists(userName);
    }
}
