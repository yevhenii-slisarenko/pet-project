import React, { Fragment, useState, useEffect, useContext } from "react";
import PostList from "../components/posts/PostList";
import useHttp from "../hooks/use-http";
import { getUserPostsByName } from "../utils/database-api";
import { useParams } from "react-router-dom";
import styles from "./Home.module.css";
import UserContext from "../context/UserContext";

const MyPosts = () => {
    const username = localStorage.getItem("name");
    const {
        sendHttpRequest: getPostsRequest,
        status,
        data: loadedPosts,
    } = useHttp(getUserPostsByName);

    useEffect(() => {
        console.log("user name :", username);
        if (username) getPostsRequest(username);
    }, []);

    if (!username) {
        return (
            <div
                style={{
                    fontSize: "1.1rem",
                    color: "#777",
                    padding: "10px",
                    marginLeft: "50px",
                    position: "relative",
                }}
            >
                Login to see your posts
            </div>
        );
    }

    return (
        <Fragment>
            <div
                style={{
                    display: "flex",
                    marginBottom: "-10px",
                    fontSize: "larger",
                    marginLeft: "60px",
                    marginBottom: "-20px",
                    marginTop: "30px",
                }}
            >
                <strong>My Posts :</strong>
            </div>
            {status === "completed" && loadedPosts.length !== 0 && (
                <PostList posts={loadedPosts} />
            )}
        </Fragment>
    );
};

export default MyPosts;
