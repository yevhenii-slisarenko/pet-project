import React, { useState } from "react";
import { Link } from "react-router-dom";
import styles from "./PostItem.module.css";
import { deletePost } from "../../utils/database-api";
import useHttp from "../../hooks/use-http";
import { FaEllipsisH } from "react-icons/fa";
import { useNavigate } from "react-router-dom";

const PostItem = ({ id, content, username, postDate }) => {
    const [isDeleteFormVisible, setDeleteFormVisible] = useState(false);
    const navigate = useNavigate();
    const name = localStorage.getItem("name");
    const formattedDate = new Date(postDate).toLocaleString();

    const { sendHttpRequest: sendHttpRequestDeletePost } = useHttp(deletePost);

    const handleDeletePost = () => {
        // const post = {
        //     post_Id: id,
        // };
        //console.log("id", id);
        sendHttpRequestDeletePost(id)
            .then(() => {
                navigate("/");
                window.location.reload();
            })
            .catch((error) => {
                console.error("Error deleting post:", error);
            });
    };

    const toggleDeleteForm = () => {
        setDeleteFormVisible((prev) => !prev);
    };

    return (
        <div>
            <hr className={styles.hrStyle} />
            <div className={styles.card}>
                <div className={styles.cardHeader}>
                    <div>
                        <Link to={`/user/${username}`} className={styles.link}>
                            <span className={styles.author}>{username}</span>
                        </Link>
                        <span className={styles.dot}>•</span>
                        <span className={styles.date}>{formattedDate}</span>
                    </div>
                    <div className={styles.deleteButtonContainer}>
                        <button
                            className={styles.deleteButton}
                            onClick={toggleDeleteForm}
                        >
                            <FaEllipsisH size={20} />
                        </button>

                        {isDeleteFormVisible && (
                            <div>
                                {username === name ||
                                localStorage.getItem("role") ===
                                    "ADMINdfghdfghfdghdgfh" ? (
                                    <div className={styles.deleteConfirm}>
                                        <button
                                            className={styles.deletePostButton}
                                            onClick={handleDeletePost}
                                        >
                                            Delete Post
                                        </button>
                                        <button
                                            className={
                                                styles.cancelDeleteButton
                                            }
                                            onClick={toggleDeleteForm}
                                        >
                                            Cancel
                                        </button>
                                    </div>
                                ) : (
                                    <div
                                        className={styles.deleteConfirm}
                                        style={{ bottom: "20px" }}
                                    >
                                        <button
                                            className={
                                                styles.cancelDeleteButton
                                            }
                                            onClick={toggleDeleteForm}
                                        >
                                            Cancel
                                        </button>
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                </div>
                <Link to={`/post/${id}`} className={styles.link}>
                    <div className={styles.content}>
                        <p>{content}</p>
                    </div>
                </Link>
            </div>
        </div>
    );
};

export default PostItem;
