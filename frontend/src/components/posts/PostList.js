import React from "react";
import PostItem from "./PostItem";
import styles from "./PostList.module.css";

const PostList = ({ posts }) => {
    if (!posts || posts.length === 0) {
        return <p className={styles.empty}>No posts found</p>;
    }

    return (
        <div className={styles.grid}>
            {posts
                .filter((post) => !post.isDeleted)
                .map((post) => (
                    <PostItem
                        key={post.id}
                        id={post.id}
                        content={post.content}
                        username={post.createdBy}
                        postDate={post.created}
                    />
                ))}
        </div>
    );
};

export default PostList;
