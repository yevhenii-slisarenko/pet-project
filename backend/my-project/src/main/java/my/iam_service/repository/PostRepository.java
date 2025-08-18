package my.iam_service.repository;

import my.iam_service.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer>, JpaSpecificationExecutor<Post> {

    boolean existsByTitle(String title);

    Optional<Post> findByIdAndDeletedFalse (Integer id);

    LinkedList<Post> findAllByUserIdAndDeletedFalse(Integer userId);

    LinkedList<Post> findAllByUserUsername(String username);

    LinkedList<Post> findAllByUserUsernameAndDeletedFalse(String username);
}
