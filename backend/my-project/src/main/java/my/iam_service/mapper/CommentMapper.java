package my.iam_service.mapper;

import my.iam_service.model.dto.comment.CommentDTO;
import my.iam_service.model.dto.comment.CommentSearchDTO;
import my.iam_service.model.dto.comment.UserCommentDTO;
import my.iam_service.model.entity.Comment;
import my.iam_service.model.entity.Post;
import my.iam_service.model.entity.User;
import my.iam_service.model.request.comment.CommentRequest;
import my.iam_service.model.request.comment.UpdateCommentRequest;
import org.hibernate.type.descriptor.DateTimeUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.LinkedList;
import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {DateTimeUtils.class, Object.class}
)
public interface CommentMapper {

    @Mapping(source = "user.id", target = "owner.id")
    @Mapping(source = "user.username", target = "owner.username")
    @Mapping(source = "user.email", target = "owner.email")
    @Mapping(source = "post.id", target = "postId")
    //@Mapping(target = "replies", expression = "java(toDtoList(new List<>(comment.getReplies())))")
    @Mapping(source = "parent.id", target = "parentId")
    //@Mapping(target = "replies", source = "comment.replies")
    CommentDTO toDto(Comment comment);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "post", source = "post")
    @Mapping(target = "createdBy", source = "user.email")
    Comment createComment(CommentRequest commentRequest, User user, Post post);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "createdBy", source = "user.email")
    @Mapping(target = "parent", source = "parentComment")
    @Mapping(target = "message", source = "commentRequest.message")
    @Mapping(target = "post", source = "parentComment.post")
    Comment createRepliedComment(CommentRequest commentRequest, User user, Comment parentComment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    void updateComment(@MappingTarget Comment comment, UpdateCommentRequest commentRequest);

    @Mapping(source = "user.id", target = "owner.id")
    @Mapping(source = "user.username", target = "owner.username")
    @Mapping(source = "user.email", target = "owner.email")
    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "deleted", target = "isDeleted")
    @Mapping(source = "parent.message", target = "repliedTitle")
    CommentSearchDTO toCommentSearchDTO(Comment comment);

    //LinkedList<CommentDTO> toDtoList(LinkedList<Comment> comments);

    default LinkedList<CommentDTO> toDtoList(LinkedList<Comment> comments) {
        LinkedList<CommentDTO> dtoList = new LinkedList<>();
        if (comments != null) {
            for (Comment c : comments) {
                dtoList.add(toDto(c));
            }
        }
        return dtoList;
    }

    @Mapping(source = "user.id", target = "owner.id")
    @Mapping(source = "user.username", target = "owner.username")
    @Mapping(source = "user.email", target = "owner.email")
    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "parent.message", target = "repliedTitle")
    @Mapping(target = "replies", source = "replies")
    UserCommentDTO toUserCommentDto(Comment comment);

    default LinkedList<UserCommentDTO> toUserCommentDtoList(LinkedList<Comment> comments) {
        LinkedList<UserCommentDTO> dtoList = new LinkedList<>();
        if (comments != null) {
            for (Comment c : comments) {
                dtoList.add(toUserCommentDto(c));
            }
        }
        return dtoList;
    }
}
