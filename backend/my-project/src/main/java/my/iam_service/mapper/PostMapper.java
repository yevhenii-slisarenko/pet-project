package my.iam_service.mapper;

import my.iam_service.model.dto.post.PostDTO;
import my.iam_service.model.dto.post.PostSearchDTO;
import my.iam_service.model.entity.Post;
import my.iam_service.model.entity.User;
import my.iam_service.model.request.post.NewPostRequest;
import my.iam_service.model.request.post.UpdatePostRequest;
import org.hibernate.type.descriptor.DateTimeUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.LinkedList;
import java.util.Objects;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {DateTimeUtils.class, Objects.class}
)
public interface PostMapper {

    @Mapping(source = "image", target = "image")
    @Mapping(source = "commentsCount", target = "commentsCount")
    @Mapping(source = "deleted", target = "deleted")
    PostDTO toPostDTO(Post post);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(source = "user", target = "user")
    @Mapping(source = "createdBy", target = "createdBy")
    Post createPost(NewPostRequest newPostRequest, User user, String createdBy);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", expression = "java(java.time.LocalDateTime.now())")
    void updatePost(@MappingTarget Post post, UpdatePostRequest request);

    @Mapping(source = "deleted", target = "isDeleted")
    @Mapping(target = "createdBy", source = "user.username")
    @Mapping(source = "image", target = "image")
    PostSearchDTO toPostSearchDTO(Post post);

    LinkedList<PostDTO> toDtoList(LinkedList<Post> posts);
}
