package my.iam_service.model.dto.comment;

import my.iam_service.model.dto.post.PostOwnerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentSearchDTO implements Serializable {

    private Integer id;
    private String message;
    private PostOwnerDTO owner;
    private String repliedTitle;
    private Integer postId;
    private LocalDateTime created;
    private LocalDateTime updated;
    private Boolean isDeleted;

}
