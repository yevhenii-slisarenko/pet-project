package my.iam_service.model.request.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest {


    private Integer postId;

    @NotBlank(message = "Content cannot be empty")
    private String message;

    private Integer repliedId;
}
