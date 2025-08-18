package my.iam_service.controller;

import my.iam_service.model.constants.ApiLogMessage;
import my.iam_service.model.dto.post.PostDTO;
import my.iam_service.model.dto.post.PostSearchDTO;
import my.iam_service.model.request.post.NewPostRequest;
import my.iam_service.model.request.post.PostSearchRequest;
import my.iam_service.model.request.post.UpdatePostRequest;
import my.iam_service.model.response.IamResponse;
import my.iam_service.model.response.PaginationResponse;
import my.iam_service.service.PostService;
import my.iam_service.utils.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("${end.points.posts}")
public class PostController {
    private final PostService postService;

    @GetMapping("/{id}")
    @Operation(summary = "Get Post by ID", description = "Retrieves a post by its unique identifier")
    public ResponseEntity<IamResponse<PostDTO>> getPostById(
            @PathVariable(name = "id") Integer postId) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        IamResponse<PostDTO> response = postService.getById(postId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("${end.points.create}")
    @Operation(summary = "Create a new Post", description = "Adds a new post to the system")
    public ResponseEntity<IamResponse<PostDTO>> createPost(
            @RequestBody @Valid NewPostRequest request) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        IamResponse<PostDTO> response = postService.createPost(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("${end.points.id}")
    @Operation(summary = "Update a Post", description = "Updates an existing post by its ID")
    public ResponseEntity<IamResponse<PostDTO>> updatePostById(
            @PathVariable(name = "id") Integer postId,
            @RequestBody @Valid UpdatePostRequest request) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        IamResponse<PostDTO> updatedPost = postService.updatePost(postId, request);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Post", description = "Marks a post as deleted without removing it from the database")
    public ResponseEntity<Void> softDeletePostById(
            @PathVariable(name = "id") Integer postId) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        postService.softDeletePost(postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("${end.points.all}")
    @Operation(summary = "Get all Posts", description = "Retrieves a paginated list of all posts")
    public ResponseEntity<IamResponse<PaginationResponse<PostSearchDTO>>> getAllPosts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        Pageable pageable = PageRequest.of(page, limit, Sort.by("id").descending());
        IamResponse<PaginationResponse<PostSearchDTO>> response = postService.findAllPosts(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("${end.points.search}")
    @Operation(summary = "Search Posts", description = "Searches for posts based on filters and pagination")
    public ResponseEntity<IamResponse<PaginationResponse<PostSearchDTO>>> searchPosts(
            @RequestBody @Valid PostSearchRequest request,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        Pageable pageable = PageRequest.of(page, limit);
        IamResponse<PaginationResponse<PostSearchDTO>> response = postService.searchPosts(request, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{id}")
    @Operation(summary = "Get all posts by user Id", description = "Get all posts by user id")
    public ResponseEntity<IamResponse<LinkedList<PostDTO>>> getAllPostsByUserId(@PathVariable(name = "id") Integer userId) {

        IamResponse<LinkedList<PostDTO>> response = postService.findAllPostsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get all posts by user name", description = "Get all posts by user name")
    public ResponseEntity<IamResponse<LinkedList<PostDTO>>> getAllPostsByUserName(@PathVariable(name = "username") String username) {

        IamResponse<LinkedList<PostDTO>> response = postService.findAllPostsByUserUsername(username);
        return ResponseEntity.ok(response);
    }
}
