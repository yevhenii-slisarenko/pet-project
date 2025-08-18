package my.iam_service.service.impl;

import my.iam_service.mapper.PostMapper;
import my.iam_service.model.constants.ApiErrorMessage;
import my.iam_service.model.dto.post.PostDTO;
import my.iam_service.model.dto.post.PostSearchDTO;
import my.iam_service.model.entity.Post;
import my.iam_service.model.entity.User;
import my.iam_service.model.exception.DataExistException;
import my.iam_service.model.exception.NotFoundException;
import my.iam_service.model.request.post.NewPostRequest;
import my.iam_service.model.request.post.PostSearchRequest;
import my.iam_service.model.request.post.UpdatePostRequest;
import my.iam_service.model.response.IamResponse;
import my.iam_service.model.response.PaginationResponse;
import my.iam_service.repository.PostRepository;
import my.iam_service.repository.UserRepository;
import my.iam_service.repository.criteria.PostSearchCriteria;
import my.iam_service.security.validation.AccessValidator;
import my.iam_service.service.PostService;
import my.iam_service.utils.ApiUtils;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final AccessValidator accessValidator;
    private final ApiUtils apiUtils;

    @Override
    @Transactional(readOnly = true)
    public IamResponse<PostDTO> getById(@NotNull Integer postId) {
        Post post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(postId)));

        return IamResponse.createSuccessful(postMapper.toPostDTO(post));
    }

    @Override
    @Transactional
    public IamResponse<PostDTO> createPost(@NotNull NewPostRequest postRequest) {
//        if (postRepository.existsByTitle(postRequest.getTitle())) {
//            throw new DataExistException(ApiErrorMessage.POST_ALREADY_EXISTS.getMessage(postRequest.getTitle()));
//        }

        Integer userId = apiUtils.getUserIdFromAuthentication();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(ApiErrorMessage.USERNAME_NOT_FOUND.getMessage(userId)));

        Post post = postMapper.createPost(postRequest, user, user.getUsername());
        post = postRepository.save(post);


        return IamResponse.createSuccessful(postMapper.toPostDTO(post));
    }

    @Override
    @Transactional
    public IamResponse<PostDTO> updatePost(@NotNull Integer postId, @NotNull UpdatePostRequest request) {
        Post post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(postId)));

        accessValidator.validateAdminOrOwnerAccess(post.getUser().getId());

        if (!post.getTitle().equals(request.getTitle()) && postRepository.existsByTitle(request.getTitle())) {
            throw new DataExistException(ApiErrorMessage.POST_ALREADY_EXISTS.getMessage(request.getTitle()));
        }

        postMapper.updatePost(post, request);
        post = postRepository.save(post);


        return IamResponse.createSuccessful(postMapper.toPostDTO(post));

    }

    @Override
    @Transactional
    public void softDeletePost(Integer postId) {
        Post post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(postId)));

        accessValidator.validateAdminOrOwnerAccess(post.getUser().getId());

        post.setDeleted(true);
        postRepository.save(post);

    }

    @Override
    @Transactional(readOnly = true)
    public IamResponse<PaginationResponse<PostSearchDTO>> findAllPosts(Pageable pageable) {
        Page<PostSearchDTO> posts = postRepository.findAll(pageable)
                .map(postMapper::toPostSearchDTO);

        PaginationResponse<PostSearchDTO> paginationResponse = new PaginationResponse<>(
                posts.getContent(),
                new PaginationResponse.Pagination(
                        posts.getTotalElements(),
                        pageable.getPageSize(),
                        posts.getNumber() + 1,
                        posts.getTotalPages()
                )
        );

        return IamResponse.createSuccessful(paginationResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public IamResponse<PaginationResponse<PostSearchDTO>> searchPosts(PostSearchRequest request, Pageable pageable) {
        Specification<Post> specification = new PostSearchCriteria(request);

        Page<PostSearchDTO> postsPage = postRepository.findAll(specification, pageable)
                .map(postMapper::toPostSearchDTO);

        PaginationResponse<PostSearchDTO> response = PaginationResponse.<PostSearchDTO>builder()
                .content(postsPage.getContent())
                .pagination(PaginationResponse.Pagination.builder()
                        .total(postsPage.getTotalElements())
                        .limit(pageable.getPageSize())
                        .page(postsPage.getNumber() + 1)
                        .pages(postsPage.getTotalPages())
                        .build())
                .build();

        return IamResponse.createSuccessful(response);
    }



    @Transactional(readOnly = true)
    public IamResponse<LinkedList<PostDTO>> findAllPostsByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId)));

        LinkedList<Post> posts = postRepository.findAllByUserIdAndDeletedFalse(userId);
        LinkedList<PostDTO> postDto = postMapper.toDtoList(posts);
        return IamResponse.createSuccessful(postDto);
    }

    @Transactional(readOnly = true)
    public IamResponse<LinkedList<PostDTO>> findAllPostsByUserUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(username)));

        LinkedList<Post> posts = postRepository.findAllByUserUsernameAndDeletedFalse(username);
        LinkedList<PostDTO> postDto = postMapper.toDtoList(posts);
        return IamResponse.createSuccessful(postDto);
    }
}
