package my.iam_service.repository;

import aj.org.objectweb.asm.commons.Remapper;
import my.iam_service.model.entity.Comment;
import my.iam_service.model.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.Page;
@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer>, JpaSpecificationExecutor<Comment> {

    Optional<Comment> findByIdAndDeletedFalse(Integer commentId);

    LinkedList<Comment> findAllByUserIdAndDeletedFalse(Integer userId);

    LinkedList<Comment> findByPostIdAndParentIsNullAndDeletedFalseOrderByCreatedAsc(Integer postId);

    Page<Comment> findAllByDeletedFalse(Pageable pageable);
}
