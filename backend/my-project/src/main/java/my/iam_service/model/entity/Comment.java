package my.iam_service.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
public class Comment {
    public static final String ID_FIELD = "id";
    public static final String MESSAGE_NAME_FIELD = "message";
    public static final String CREATED_BY_FIELD = "createdBy";
    public static final String DELETED_FIELD = "deleted";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @JsonIgnore
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updated = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean deleted = false;

    @Column(name = "created_by", length = 100)
    private String createdBy;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "replied_id") // в БД это поле
//    @JsonIgnoreProperties({"user", "postId", "created", "updated", "parent", "replies", "message"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replied_id")
    @JsonIgnore
    private Comment parent; // родительский комментарий

    @JsonProperty("parentId")
    public Integer getParentId() {
        return parent != null ? parent.getId() : null;
    }

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>(); // список ответов на этот комментарий


}
