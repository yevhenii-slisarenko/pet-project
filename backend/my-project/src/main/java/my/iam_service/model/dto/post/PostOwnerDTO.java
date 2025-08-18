package my.iam_service.model.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostOwnerDTO implements Serializable {

    private Integer id;
    private String username;
    private String email;

}
