package my.iam_service.model.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {

    @NotBlank(message = "Username cannot be empty")
    @Size(max = 30)
    private String username;

    @NotBlank(message = "Password cannot be empty")
    @Size(max = 50)
    private String password;

    @NotBlank(message = "Email cannot be empty")
    @Size(max = 50)
    private String email;

}
