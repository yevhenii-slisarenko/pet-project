package my.iam_service.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Setter
public class Post {
    public static final String ID_FIELD = "id";
    public static final String TITLE_NAME_FIELD = "title";
    public static final String CONTENT_NAME_FIELD = "content";
    public static final String LIKES_NAME_FIELD = "likes";
    public static final String DELETED_FIELD = "deleted";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updated = LocalDateTime.now();

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer likes = 0;

    @Column(nullable = false)
    private Boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_by")
    private String createdBy;

    @Column(length = 2048)
    private String image;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new LinkedList<>(); // Связь с комментариями

    @Column(name = "comments_count", nullable = false, columnDefinition = "integer default 0")
    private Integer commentsCount = 0;

    public void incrementCommentsCount() {
        this.commentsCount++;
    }

    public void decrementCommentsCount() {
        if (this.commentsCount > 0) {
            this.commentsCount--;
        }
    }
}
