package my.iam_service.model.request.post;

import my.iam_service.model.enums.PostSortField;
import lombok.Data;

import java.io.Serializable;

@Data
public class PostSearchRequest implements Serializable {

    private String title;
    private String content;
    private Integer likes;
    private String image;

    private Boolean deleted;
    private String keyword;
    private PostSortField sortField;

}
