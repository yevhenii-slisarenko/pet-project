package my.iam_service.model.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.iam_service.model.dto.post.PostOwnerDTO;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCommentDTO {
    private Integer id;
    private String message;
    private PostOwnerDTO owner;
    private Integer postId;
    private String repliedTitle;
    private List<UserCommentDTO> replies;
    private LocalDateTime created;
    private LocalDateTime updated;
    private Integer parentId;
}

