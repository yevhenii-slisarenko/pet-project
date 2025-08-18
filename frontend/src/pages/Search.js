import React, { Fragment, useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import PostList from "../components/posts/PostList";
import useHttp from "../hooks/use-http";
import { getPosts, addPost, getFilteredPosts } from "../utils/database-api";
import styles from "./Home.module.css";
import { useSelector, useDispatch } from "react-redux";
import { mainActions } from "../store/main-slice";

const Search = () => {
    const dispatchAction = useDispatch();
    const isCreatePostVisible = useSelector(
        (state) => state.main.isCreatePostVisible
    );
    const filter = useSelector((state) => state.main.filter);

    const [newPostTitle, setNewPostTitle] = useState("");
    const [isDisabled, setIsDisabled] = useState(true);
    const user = localStorage.getItem("name");
    const { searchText } = useParams();

    // const {
    //     sendHttpRequest: getPostsRequest,
    //     status,
    //     data: loadedPosts,
    // } = useHttp(getFilteredPosts);

    // const loadedPosts = data?.payload?.content ?? [];
    // console.log("loadedPosts");
    // console.log(loadedPosts);

    const {
        sendHttpRequest: getFilteredPostsRequest,
        status: statusFiltered,
        data: loadedFilteredPosts,
    } = useHttp(getFilteredPosts);

    const {
        sendHttpRequest: sendHttpRequestAddPost,
        status: statusAdd,
        data: post_id,
    } = useHttp(addPost);

    // useEffect(() => {
    //     if (!filter) {
    //         getPostsRequest();
    //     } else {
    //         getPostsRequest();
    //     }
    // }, []);

    const updatePostsList = () => {
        getFilteredPostsRequest();
    };

    useEffect(() => {
        if (searchText) {
            getFilteredPostsRequest(searchText);
            console.log("searchText : ", searchText);
        }
    }, [searchText]);

    useEffect(() => {
        setNewPostTitle("");
    }, [isCreatePostVisible]);

    const toggleCreatePost = () => {
        dispatchAction(mainActions.toggleCreatePostVisibility());
    };

    const handleCreatePost = async () => {
        const post = {
            content: newPostTitle,
            likes: 0,
            title: newPostTitle,
            //image: null,
            //username: user,
        };

        const authToken = localStorage.getItem("authToken");

        if (!authToken) {
            alert("You should be authorized to create post");
            return;
        }
        console.log("sending post");
        await sendHttpRequestAddPost(post)
            .then(() => {
                updatePostsList();
            })
            .catch((error) => {
                console.error("Error creating post:", error);
            });

        dispatchAction(mainActions.toggleCreatePostVisibility());
    };

    return (
        <Fragment>
            {isCreatePostVisible && (
                <div className={styles.createPostBlock}>
                    <input
                        type="text"
                        placeholder="Post title"
                        value={newPostTitle}
                        onChange={(e) => setNewPostTitle(e.target.value)}
                        className={styles.inputField}
                    />
                    <button
                        className={`btn ${
                            !newPostTitle ? "btn-light" : "btn-primary"
                        } ${styles.submitButton}`}
                        disabled={!newPostTitle}
                        onClick={handleCreatePost}
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
            )}

            {statusFiltered === "completed" &&
                loadedFilteredPosts.length !== 0 && (
                    <PostList posts={loadedFilteredPosts} />
                )}
        </Fragment>
    );
};

export default Search;
