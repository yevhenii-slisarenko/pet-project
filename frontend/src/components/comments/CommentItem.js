import React, { useState, useEffect } from "react";
import useHttp from "../../hooks/use-http";
import { addRepliedComment, deleteComment } from "../../utils/database-api";
import { Link } from "react-router-dom";
import styles from "./Comment.module.css";
import { FaEllipsisV, FaArrowRight } from "react-icons/fa";
import { MdReply } from "react-icons/md";
import CommentList from "./CommentList";

const CommentItem = (props) => {
    const {
        id,
        content,
        username,
        postDate,
        level = 0,
        repliedTitle,
        onUpdate,
        replies = [],
        versionView,
    } = props;

    const [isDeleteFormVisible, setDeleteFormVisible] = useState(false);
    const [isReplyFormVisible, setReplyFormVisible] = useState(false);
    const [newComment, setNewComment] = useState("");
    const name = localStorage.getItem("name");
    const formattedDate = new Date(postDate).toLocaleString();
    // const {
    //     sendHttpRequest: sendHttpRequestGetRepliedComments,
    //     status: statusGet,
    //     data: comments,
    // } = useHttp(getRepliedComments);
    const {
        sendHttpRequest: sendHttpRequestAddRepliedComment,
        status: statusAdd,
        data: addedComment,
    } = useHttp(addRepliedComment);
    const {
        sendHttpRequest: sendHttpRequestDeleteComment,
        status: statusDelete,
        data: deletedComment,
    } = useHttp(deleteComment);

    // useEffect(() => {
    //     sendHttpRequestGetRepliedComments(id);
    // }, []);

    // useEffect(() => {
    //     sendHttpRequestGetRepliedComments(id);
    // }, [statusAdd]);

    const lvl = level > 6 ? 6 : level;
    const dynamicStyle = {
        marginLeft: `${(lvl + 1) * 40}px`,
        maxWidth: `${800 - (lvl + 1) * 40}px`,
        marginTop: "-20px",
    };

    const deleteCommentHandler = () => {
        const comment = {
            comment_Id: id,
        };
        sendHttpRequestDeleteComment(comment)
            .then(() => {
                props.onUpdate();
            })
            .catch((error) => {
                console.error("Error deleting comment:", error);
            });
    };

    const toggleDeleteForm = () => {
        setDeleteFormVisible((prev) => !prev);
    };

    const toggleReplyButton = () => {
        setReplyFormVisible((prev) => !prev);
        setNewComment(" ");
    };

    const handleCreateComment = () => {
        if (!name) {
            alert("You have to be authorized to create a comment");
            return;
        }
        const comment = {
            message: newComment,
            repliedId: id,
            //replied_title: content,
            //username: name,
        };
        console.log("comment");
        console.log(comment);

        sendHttpRequestAddRepliedComment(comment)
            .then(() => {
                onUpdate();
            })
            .catch((error) => {
                console.error("Error deleting comment:", error);
            });
        setNewComment(" ");
        setReplyFormVisible(false);
    };

    const updateCommentsList = () => {
        onUpdate();
    };
    console.log("versionView", props.versionView);
    return (
        <div>
            <ul className={styles.list}>
                <div className={styles.card} style={dynamicStyle}>
                    <li className={styles.item}>
                        {repliedTitle && (
                            <div
                                style={{
                                    color: "grey",
                                    fontSize: "small",
                                    marginBottom: "10px",
                                    overflowWrap: "anywhere",
                                }}
                            >
                                {" "}
                                <MdReply
                                    size={15}
                                    style={{ transform: "scaleX(-1)" }}
                                />{" "}
                                {repliedTitle}{" "}
                                <hr
                                    style={{
                                        marginTop: "5px",
                                        marginRight: "50px",
                                    }}
                                ></hr>
                            </div>
                        )}
                        <div className={styles.header}>
                            <Link
                                to={`/user/${username}`}
                                className={styles.link}
                            >
                                <span className={styles.author}>
                                    {username}
                                </span>
                            </Link>
                            <span className={styles.dot}>•</span>
                            <span className={styles.date}>{formattedDate}</span>
                            {
                                <div className={styles.deleteButtonContainer}>
                                    <button
                                        className={styles.deleteButton}
                                        onClick={toggleDeleteForm}
                                    >
                                        <FaEllipsisV size={20} />
                                    </button>

                                    {isDeleteFormVisible && (
                                        <div>
                                            {username === name ||
                                            localStorage.getItem("role") ===
                                                "ADMIN" ? (
                                                <div
                                                    className={
                                                        styles.deleteConfirm
                                                    }
                                                >
                                                    <button
                                                        className={
                                                            styles.deletePostButton
                                                        }
                                                        onClick={
                                                            deleteCommentHandler
                                                        }
                                                    >
                                                        Delete Comment
                                                    </button>
                                                    <button
                                                        className={
                                                            styles.cancelDeleteButton
                                                        }
                                                        onClick={
                                                            toggleDeleteForm
                                                        }
                                                    >
                                                        Cancel
                                                    </button>
                                                </div>
                                            ) : (
                                                <div
                                                    className={
                                                        styles.deleteConfirm
                                                    }
                                                    style={{ bottom: "0px" }}
                                                >
                                                    <button
                                                        className={
                                                            styles.cancelDeleteButton
                                                        }
                                                        onClick={
                                                            toggleDeleteForm
                                                        }
                                                    >
                                                        Cancel
                                                    </button>
                                                </div>
                                            )}
                                        </div>
                                    )}
                                </div>
                            }
                        </div>

                        <div className={styles.content}>
                            <p>{content}</p>
                        </div>
                        {props.versionView !== "adminPage" && (
                            <button
                                className={styles.replyButton}
                                onClick={toggleReplyButton}
                            >
                                Reply
                            </button>
                        )}
                    </li>
                </div>
            </ul>
            <div
                className={styles.replyFormContainer}
                style={{
                    marginLeft: dynamicStyle.marginLeft,
                    maxWidth: Math.min(800 - level * 40, 600),
                }}
            >
                {isReplyFormVisible && (
                    <div className={styles.expandedForm}>
                        <textarea
                            value={newComment}
                            onChange={(e) => setNewComment(e.target.value)}
                            placeholder="Add a comment..."
                            className={styles.commentInputExpanded}
                        />
                        <div className={styles.buttonContainer}>
                            <button
                                onClick={toggleReplyButton}
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
                )}
            </div>
            <div>
                {replies.length > 0 && (
                    <CommentList
                        comments={replies}
                        onUpdate={updateCommentsList}
                        level={level + 1}
                    />
                )}
            </div>
        </div>
    );
};

export default CommentItem;
