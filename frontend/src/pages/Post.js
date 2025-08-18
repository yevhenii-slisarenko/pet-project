import React, { Fragment, useState, useEffect, useRef } from "react";
import useHttp from "../hooks/use-http";
import {
    addComment,
    getComments,
    getPost,
    deletePost,
} from "../utils/database-api";
import styles from "./Home.module.css";
import { useParams, useNavigate } from "react-router-dom";
import CommentList from "../components/comments/CommentList";
import PostList from "../components/posts/PostList";

const PostPage = (props) => {
    //const [isCreatePostVisible, setCreatePostVisible] = useState(false);
    const [isCreateCommentVisible, setCreateCommentVisible] = useState(false);
    const [newPost, setNewPost] = useState("");
    const [newComment, setNewComment] = useState("");
    const user = localStorage.getItem("name");
    const { postId } = useParams();
    const navigate = useNavigate();

    const {
        sendHttpRequest: getPostRequest,
        status: getPostStatus,
        data: loadedPost,
    } = useHttp(getPost);

    const {
        sendHttpRequest: sendHttpRequestGetComments,
        status,
        data: loadedComments = [],
    } = useHttp(getComments);

    const {
        sendHttpRequest: sendHttpRequestAddComment,
        status: statusAdd,
        data: addedComment,
    } = useHttp(addComment);

    const { sendHttpRequest: sendHttpRequestDeletePost } = useHttp(deletePost);

    useEffect(() => {
        sendHttpRequestGetComments(postId);
    }, [postId]);

    useEffect(() => {
        getPostRequest(postId);
    }, [postId]);

    const inputRef = useRef(null);

    useEffect(() => {
        if (statusAdd === "completed") {
            updateCommentsList();
        }
    }, [statusAdd]);

    // const toggleCreatePost = () => {
    //     setCreatePostVisible(!isCreatePostVisible);
    //     if (!isCreatePostVisible) {
    //         setNewPost("");
    //     }
    // };

    const toggleCreateComment = () => {
        setCreateCommentVisible(!isCreateCommentVisible);
        if (!isCreateCommentVisible) {
            setNewComment("");
        }
    };

    useEffect(() => {
        if (isCreateCommentVisible && inputRef.current) {
            inputRef.current.focus();
        }
    }, [isCreateCommentVisible]);

    const handleDeletePost = () => {
        //const post = { post_Id: postId };
        sendHttpRequestDeletePost(postId)
            .then(() => {
                navigate("/");
            })
            .catch((error) => {
                console.error("Error deleting post:", error);
            });
    };

    const handleCreateComment = () => {
        if (!user) {
            alert("You have to be authorized to create a comment");
            return;
        }
        const comment = {
            message: newComment,
            postId: postId,
        };

        sendHttpRequestAddComment(comment);
        setCreateCommentVisible(false);
        setNewComment("");
    };

    const updateCommentsList = () => {
        sendHttpRequestGetComments(postId);
    };

    return (
        <Fragment>
            {status === "completed" && getPostStatus === "completed" && (
                <div>
                    <div>
                        <PostList posts={[loadedPost]} />
                    </div>

                    <div className={styles.commentFormContainer}>
                        {isCreateCommentVisible ? (
                            <div className={styles.expandedForm}>
                                <textarea
                                    value={newComment}
                                    onChange={(e) =>
                                        setNewComment(e.target.value)
                                    }
                                    placeholder="Add a comment..."
                                    className={styles.commentInputExpanded}
                                    ref={inputRef}
                                />
                                <div className={styles.buttonContainer}>
                                    <button
                                        onClick={toggleCreateComment}
                                        className={styles.cancelButton}
                                    >
                                        Cancel
                                    </button>
                                    <button
                                        onClick={handleCreateComment}
                                        className={styles.submitButton}
                                    >
                                        Comment
                                    </button>
                                </div>
                            </div>
                        ) : (
                            <input
                                type="text"
                                placeholder="Add a comment"
                                className={styles.commentInput}
                                onClick={toggleCreateComment}
                            />
                        )}
                    </div>
                    {/* {isCreatePostVisible && (
                        <div className={styles.createPostBlock}>
                            <input
                                type="text"
                                placeholder="Write title"
                                value={newPost}
                                onChange={(e) => setNewPost(e.target.value)}
                                className={styles.inputField}
                            />
                            <button
                                className={styles.submitButton}
                                onClick={handleCreateComment}
                            >
                                Send
                            </button>
                            <button
                                className={styles.closeButton}
                                onClick={toggleCreatePost}
                            >
                                Close
                            </button>
                        </div>
                    )} */}

                    <CommentList
                        comments={loadedComments}
                        onUpdate={updateCommentsList}
                    />
                </div>
            )}
        </Fragment>
    );
};

export default PostPage;
