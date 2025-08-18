package my.iam_service.model.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostSearchDTO implements Serializable {

    private Integer id;
    private String title;
    private String content;
    private Integer likes;
    private String image;
    private LocalDateTime created;
    private Boolean isDeleted;
    private String createdBy;
    private Integer commentsCount;

}
