import React, { Fragment, useState, useEffect, useContext } from "react";
import PostList from "../components/posts/PostList";
import useHttp from "../hooks/use-http";
import { getUserPostsByName } from "../utils/database-api";
import { useParams } from "react-router-dom";
import styles from "./Home.module.css";
import UserContext from "../context/UserContext";

const User = () => {
    const { username } = useParams();
    console.log(username);
    const {
        sendHttpRequest: getPostsRequest,
        status,
        data: loadedPosts,
    } = useHttp(getUserPostsByName);

    useEffect(() => {
        window.scrollTo(0, 0);
    }, []);

    useEffect(() => {
        getPostsRequest(username);
    }, []);

    return (
        <Fragment>
            <div
                style={{
                    display: "flex",
                    marginBottom: "-10px",
                    fontSize: "larger",
                    marginLeft: "400px",
                    marginBottom: "-50px",
                    marginTop: "30px",
                }}
            >
                <strong>{username}</strong>
            </div>
            {status === "completed" && loadedPosts.length !== 0 && (
                <PostList posts={loadedPosts} />
            )}
        </Fragment>
    );
};

export default User;
