package my.iam_service.model.request.user;

import my.iam_service.model.enums.UserSortField;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserSearchRequest implements Serializable {

    private String username;
    private String email;

    private Boolean deleted;
    private String keyword;
    private UserSortField sortField;

}
