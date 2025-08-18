package my.iam_service.service.impl;

import my.iam_service.mapper.CommentMapper;
import my.iam_service.mapper.PostMapper;
import my.iam_service.model.constants.ApiErrorMessage;
import my.iam_service.model.dto.comment.CommentDTO;
import my.iam_service.model.dto.comment.CommentSearchDTO;
import my.iam_service.model.dto.comment.UserCommentDTO;
import my.iam_service.model.entity.Comment;
import my.iam_service.model.entity.Post;
import my.iam_service.model.entity.User;
import my.iam_service.model.exception.NotFoundException;
import my.iam_service.model.request.comment.CommentRequest;
import my.iam_service.model.request.comment.CommentSearchRequest;
import my.iam_service.model.request.comment.UpdateCommentRequest;
import my.iam_service.model.response.IamResponse;
import my.iam_service.model.response.PaginationResponse;
import my.iam_service.repository.CommentRepository;
import my.iam_service.repository.PostRepository;
import my.iam_service.repository.UserRepository;
import my.iam_service.repository.criteria.CommentSearchCriteria;
import my.iam_service.security.validation.AccessValidator;
import my.iam_service.service.CommentService;
import my.iam_service.utils.ApiUtils;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ApiUtils apiUtils;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final AccessValidator accessValidator;

    @Override
    @Transactional(readOnly = true)
    public IamResponse<CommentDTO> getCommentById(@NotNull Integer commentId) {
        Comment comment = commentRepository.findByIdAndDeletedFalse(commentId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.COMMENT_NOT_FOUND_BY_ID.getMessage(commentId)));

        return IamResponse.createSuccessful(commentMapper.toDto(comment));
    }

    @Override
    @Transactional
    public IamResponse<CommentDTO> createComment(@NotNull CommentRequest request) {
        Integer userId = apiUtils.getUserIdFromAuthentication();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId)));

        Post post = postRepository.findByIdAndDeletedFalse(request.getPostId())
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(request.getPostId())));

        Comment comment = commentMapper.createComment(request, user, post);
        comment = commentRepository.save(comment);
        post.incrementCommentsCount();
        postRepository.save(post);

        return IamResponse.createSuccessful(commentMapper.toDto(comment));
    }

    @Override
    @Transactional
    public IamResponse<CommentDTO> createRepliedComment(@NotNull CommentRequest request) {
        Integer userId = apiUtils.getUserIdFromAuthentication();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId)));


        Comment parentComment = commentRepository.findById(request.getRepliedId())
                 .orElseThrow(() -> new NotFoundException("Parent comment not found with id " + request.getRepliedId()));

        Comment comment = commentMapper.createRepliedComment(request, user, parentComment);
        comment = commentRepository.save(comment);
//        post.incrementCommentsCount();
//        postRepository.save(post);

        return IamResponse.createSuccessful(commentMapper.toDto(comment));
    }

    @Override
    @Transactional
    public IamResponse<CommentDTO> updateComment(@NotNull Integer commentId, @NotNull UpdateCommentRequest request) {
        Comment comment = commentRepository.findByIdAndDeletedFalse(commentId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.COMMENT_NOT_FOUND_BY_ID.getMessage(commentId)));

        accessValidator.validateAdminOrOwnerAccess(comment.getUser().getId());

        if (request.getPostId() != null) {
            Post post = postRepository.findByIdAndDeletedFalse(request.getPostId())
                    .orElseThrow(() -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(request.getPostId())));
            comment.setPost(post);
        }

        commentMapper.updateComment(comment, request);
        comment = commentRepository.save(comment);

        return IamResponse.createSuccessful(commentMapper.toDto(comment));
    }

    @Override
    @Transactional
    public void softDelete(@NotNull Integer commentId) {
        Comment comment = commentRepository.findByIdAndDeletedFalse(commentId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.COMMENT_NOT_FOUND_BY_ID.getMessage(commentId)));


        accessValidator.validateAdminOrOwnerAccess(comment.getUser().getId());

        comment.setDeleted(true);
        commentRepository.save(comment);

        Post post = comment.getPost();
        post.decrementCommentsCount();
        postRepository.save(post);

        IamResponse.createSuccessful(postMapper.toPostDTO(post));
    }

    @Override
    @Transactional(readOnly = true)
    public IamResponse<PaginationResponse<CommentSearchDTO>> findAllComments(Pageable pageable) {
        Page<CommentSearchDTO> comments = commentRepository.findAllByDeletedFalse(pageable)
                .map(commentMapper::toCommentSearchDTO);

        accessValidator.validateAdminAccess();

        PaginationResponse<CommentSearchDTO> paginationResponse = new PaginationResponse<>(
                comments.getContent(),
                new PaginationResponse.Pagination(
                        comments.getTotalElements(),
                        pageable.getPageSize(),
                        comments.getNumber() + 1,
                        comments.getTotalPages()
                )
        );

        return IamResponse.createSuccessful(paginationResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public IamResponse<PaginationResponse<CommentSearchDTO>> searchComments(@NotNull CommentSearchRequest request, Pageable pageable) {
        Specification<Comment> specification = new CommentSearchCriteria(request);

        Page<CommentSearchDTO> commentsPage = commentRepository.findAll(specification, pageable)
                .map(commentMapper::toCommentSearchDTO);

        PaginationResponse<CommentSearchDTO> response = PaginationResponse.<CommentSearchDTO>builder()
                .content(commentsPage.getContent())
                .pagination((PaginationResponse.Pagination.builder()
                        .total(commentsPage.getTotalElements())
                        .limit(pageable.getPageSize())
                        .page(commentsPage.getNumber() + 1)
                        .pages(commentsPage.getTotalPages())
                        .build()))
                .build();

        return IamResponse.createSuccessful(response);
    }

    @Transactional(readOnly = true)
    public IamResponse<LinkedList<UserCommentDTO>> findAllCommentsByUser(Integer userId) {
        //String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId)));

        LinkedList<Comment> comments = commentRepository.findAllByUserIdAndDeletedFalse(userId);
        LinkedList<UserCommentDTO> commentDto = commentMapper.toUserCommentDtoList(comments);
        return IamResponse.createSuccessful(commentDto);
    }

    @Transactional(readOnly = true)
    public IamResponse<LinkedList<UserCommentDTO>> getCommentsTreeByPostId(Integer postId) {
        // Находим только корневые комментарии
        LinkedList<Comment> rootComments = commentRepository
                .findByPostIdAndParentIsNullAndDeletedFalseOrderByCreatedAsc(postId);

        // Принудительно загружаем вложенные ответы
        rootComments.forEach(this::loadRepliesRecursively);

        // Маппим в DTO
        LinkedList<UserCommentDTO> dtoList = commentMapper.toUserCommentDtoList(rootComments);
        return IamResponse.createSuccessful(dtoList);
    }

    private void loadRepliesRecursively(Comment comment) {
        comment.getReplies().removeIf(Comment::getDeleted); // убираем удалённые
        comment.getReplies().forEach(this::loadRepliesRecursively);
    }
}
