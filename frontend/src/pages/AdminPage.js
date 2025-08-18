import React, { useState, useEffect } from "react";
import UsersList from "../components/users/UsersList";
import CommentsList from "../components/comments/CommentList";
import NotFound from "./NotFound";
import {
    getAllComments,
    getAllUsers,
    deleteUser,
    getAllDeletedUsers,
} from "../utils/database-api";
import useHttp from "../hooks/use-http";
import styles from "./AdminPage.module.css";
const AdminPage = () => {
    const versionView = "adminPage";
    const [selectedTab, setSelectedTab] = useState("none");
    const {
        sendHttpRequest: sendHttpRequestGetAllComments,
        status,
        data: loadedComments = [],
    } = useHttp(getAllComments);

    const {
        sendHttpRequest: sendHttpRequestGetAllUsers,
        status: statusUsers,
        data: loadedUsers = [],
    } = useHttp(getAllUsers);

    const {
        sendHttpRequest: sendHttpRequestGetAllDeletedUsers,
        status: statusDeletedUsers,
        data: loadedDeletedUsers = [],
    } = useHttp(getAllDeletedUsers);

    useEffect(() => {
        sendHttpRequestGetAllComments();
        sendHttpRequestGetAllUsers();
        sendHttpRequestGetAllDeletedUsers();
    }, []);

    const updateCommentsList = () => {
        sendHttpRequestGetAllComments();
    };
    const updateUsersList = () => {
        sendHttpRequestGetAllUsers();
        sendHttpRequestGetAllDeletedUsers();
    };
    const updateDeletedUsersList = () => {
        sendHttpRequestGetAllDeletedUsers();
        sendHttpRequestGetAllUsers();
    };

    if (localStorage.getItem("role") !== "ADMIN") {
        return <NotFound />;
    }
    return (
        <div>
            <div className={styles.buttonContainer}>
                <button
                    className={`${styles.tabButton} ${
                        selectedTab === "users" ? styles.active : ""
                    }`}
                    onClick={() => {
                        if (selectedTab == "users") {
                            setSelectedTab("none");
                        } else {
                            setSelectedTab("users");
                        }
                    }}
                >
                    Users
                </button>
                <button
                    className={`${styles.tabButton} ${
                        selectedTab === "comments" ? styles.active : ""
                    }`}
                    onClick={() => {
                        if (selectedTab == "comments") {
                            setSelectedTab("none");
                        } else {
                            setSelectedTab("comments");
                        }
                    }}
                >
                    Comments
                </button>
                <button
                    className={`${styles.tabButton} ${
                        selectedTab === "deleted users" ? styles.active : ""
                    }`}
                    onClick={() => {
                        if (selectedTab == "deleted users") {
                            setSelectedTab("none");
                        } else {
                            setSelectedTab("deleted users");
                        }
                    }}
                >
                    Deleted Users
                </button>
            </div>

            {selectedTab === "users" && statusUsers === "completed" && (
                <UsersList
                    users={loadedUsers}
                    onUpdate={updateUsersList}
                    versionView={"not deleted"}
                />
            )}

            {selectedTab === "deleted users" &&
                statusDeletedUsers === "completed" && (
                    <UsersList
                        users={loadedDeletedUsers}
                        onUpdate={updateDeletedUsersList}
                        versionView={"deleted"}
                    />
                )}

            {selectedTab === "comments" && status === "completed" && (
                <CommentsList
                    comments={loadedComments}
                    onUpdate={updateCommentsList}
                    versionView={versionView}
                />
            )}
        </div>
    );
};

export default AdminPage;
