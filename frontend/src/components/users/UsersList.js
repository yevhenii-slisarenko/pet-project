import React from "react";
import UserItem from "./UserItem";
import styles from "./UsersList.module.css";

const UsersList = ({ users, onUpdate, versionView }) => {
    // if (status === "pending") {
    //     return <p className={styles.loading}>Loading users...</p>;
    // }

    if (!users || users.length === 0) {
        return <p className={styles.empty}>No users found</p>;
    }

    return (
        <div className={styles.tableWrapper}>
            <table className={styles.list}>
                <thead>
                    <tr>
                        <th>Username</th>
                        <th>Role</th>
                        <th>Email</th>
                        <th>Joined</th>
                        {versionView !== "deleted" ? (
                            <th>Make Admin</th>
                        ) : (
                            <th>Unban</th>
                        )}

                        {versionView == "not deleted" && <th>Delete</th>}
                    </tr>
                </thead>
                <tbody>
                    {users.map((user) => (
                        <UserItem
                            key={user.id}
                            user={user}
                            onUpdate={onUpdate}
                            versionView={versionView}
                        />
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default UsersList;
