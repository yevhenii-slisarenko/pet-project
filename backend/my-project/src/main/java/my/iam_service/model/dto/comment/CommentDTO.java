package my.iam_service.model.dto.comment;

import my.iam_service.model.dto.post.PostOwnerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.iam_service.model.entity.Comment;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO implements Serializable {

    private Integer id;
    private String message;
    private PostOwnerDTO owner;
    private Integer postId;
    private LocalDateTime created;
    private LocalDateTime updated;
    //private Comment parent;
    //private List<CommentDTO> replies;
    private Integer parentId;
}
