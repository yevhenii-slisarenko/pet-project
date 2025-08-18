import React from "react";
import CommentItem from "./CommentItem";
import styles from "../comments/Comments.module.css";

const CommentList = ({ comments, onUpdate, versionView, level = 0 }) => {
    const handleCommentDelete = () => {
        onUpdate();
    };
    console.log(comments);
    if (!comments || comments.length === 0) {
        return <p className={styles.empty}>No comments found</p>;
    }
    return (
        <div>
            {comments
                .filter((c) => !c.isDeleted)
                .map((comment) => (
                    <CommentItem
                        key={comment.id}
                        id={comment.id}
                        content={comment.message}
                        username={comment.owner.username}
                        postDate={comment.updated}
                        level={level}
                        repliedTitle={comment.repliedTitle}
                        onUpdate={handleCommentDelete}
                        replies={comment.replies}
                        versionView={versionView}
                    />
                ))}
        </div>
    );
};

export default CommentList;
