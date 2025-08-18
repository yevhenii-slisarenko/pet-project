package my.iam_service.service;

import my.iam_service.model.dto.post.PostDTO;
import my.iam_service.model.dto.post.PostSearchDTO;
import my.iam_service.model.request.post.NewPostRequest;
import my.iam_service.model.request.post.PostSearchRequest;
import my.iam_service.model.request.post.UpdatePostRequest;
import my.iam_service.model.response.IamResponse;
import my.iam_service.model.response.PaginationResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;

import java.util.LinkedList;

public interface PostService {

    IamResponse<PostDTO> getById(@NotNull Integer userId);

    IamResponse<PostDTO> createPost(@NotNull NewPostRequest request);

    IamResponse<PostDTO> updatePost(@NotNull Integer postId, @NotNull UpdatePostRequest request);

    void softDeletePost(Integer postId);

    IamResponse<PaginationResponse<PostSearchDTO>> findAllPosts(Pageable pageable);

    IamResponse<PaginationResponse<PostSearchDTO>> searchPosts(PostSearchRequest request, Pageable pageable);

    IamResponse<LinkedList<PostDTO>> findAllPostsByUserId(Integer userId);

    IamResponse<LinkedList<PostDTO>> findAllPostsByUserUsername(String username);
}
