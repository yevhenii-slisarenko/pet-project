import React from "react";
import styles from "./UserItem.module.css";
import { deleteUser, makeAdmin, unbanUser } from "../../utils/database-api";
import useHttp from "../../hooks/use-http";

const UserItem = ({ user, onUpdate, versionView }) => {
    const {
        sendHttpRequest: sendHttpRequestDeleteUser,
        statusDeleteUser,
        data,
    } = useHttp(deleteUser);
    console.log(versionView);
    const { sendHttpRequest: sendHttpRequestUnbanUser, statusUnbanUser } =
        useHttp(unbanUser);

    const { sendHttpRequest: sendHttpRequestMakeAdmin, statusAdmin } =
        useHttp(makeAdmin);

    const deleteUserHandler = (userId) => {
        sendHttpRequestDeleteUser(userId)
            .then(() => {
                onUpdate();
            })
            .catch((error) => {
                console.error("Error deleting user:", error);
            });
    };

    const makeAdminOrUnbanHandler = (userId) => {
        switch (versionView) {
            case "deleted": {
                sendHttpRequestUnbanUser(userId)
                    .then(() => {
                        onUpdate();
                    })
                    .catch((error) => {
                        console.error("Error unbanning user", error);
                    });
                break;
            }
            case "not deleted": {
                sendHttpRequestMakeAdmin(userId)
                    .then(() => {
                        onUpdate();
                    })
                    .catch((error) => {
                        console.error("Error changing role", error);
                    });
                break;
            }
        }
    };

    return (
        <tr>
            <td>{user.username}</td>
            <td>{user.roles[0].name}</td>
            <td>{user.email}</td>
            <td>{new Date(user.created).toLocaleDateString()}</td>
            <td>
                {versionView !== "deleted" ? (
                    <button
                        className={`${styles.actionBtn} ${styles.makeAdmin}`}
                        onClick={() => makeAdminOrUnbanHandler(user.id)}
                        disabled={
                            user.roles[0].name === "ADMIN" ||
                            user.roles[0].name === "SUPER_ADMIN"
                        }
                    >
                        Make Admin
                    </button>
                ) : (
                    <button
                        className={`${styles.actionBtn} ${styles.makeAdmin}`}
                        onClick={() => makeAdminOrUnbanHandler(user.id)}
                        disabled={user.role === "ADMIN"}
                    >
                        Unban
                    </button>
                )}
            </td>
            <td>
                {versionView == "not deleted" && (
                    <button
                        className={`${styles.actionBtn} ${styles.delete}`}
                        onClick={() => deleteUserHandler(user.id)}
                    >
                        Delete
                    </button>
                )}
            </td>
        </tr>
    );
};

export default UserItem;
