package my.iam_service.service;

import my.iam_service.model.dto.comment.CommentDTO;
import my.iam_service.model.dto.comment.CommentSearchDTO;
import my.iam_service.model.dto.comment.UserCommentDTO;
import my.iam_service.model.request.comment.CommentRequest;
import my.iam_service.model.request.comment.CommentSearchRequest;
import my.iam_service.model.request.comment.UpdateCommentRequest;
import my.iam_service.model.response.IamResponse;
import my.iam_service.model.response.PaginationResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;

import java.util.LinkedList;

public interface CommentService {

    IamResponse<CommentDTO> getCommentById(@NotNull Integer commentId);

    IamResponse<CommentDTO> createComment(@NotNull CommentRequest request);

    IamResponse<CommentDTO> updateComment(@NotNull Integer commentId, @NotNull UpdateCommentRequest request);

    void softDelete(@NotNull Integer commentId);

    IamResponse<PaginationResponse<CommentSearchDTO>> findAllComments(Pageable pageable);

    IamResponse<PaginationResponse<CommentSearchDTO>> searchComments(@NotNull CommentSearchRequest request, Pageable pageable);

    IamResponse<LinkedList<UserCommentDTO>> findAllCommentsByUser(Integer userId);

    IamResponse<LinkedList<UserCommentDTO>> getCommentsTreeByPostId(Integer postId);

    IamResponse<CommentDTO> createRepliedComment(@NotNull CommentRequest request);
}
