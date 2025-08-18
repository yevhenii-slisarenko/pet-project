package my.iam_service.model.request.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostRequest implements Serializable {

    private String title;
    private String content;
    private Integer likes;
    private String image;

}
