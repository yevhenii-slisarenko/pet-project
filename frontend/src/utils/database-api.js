const DATABASE_ROOT_DOMAIN = "https://spring-boot-production-d8cd.up.railway.app";

export async function getPosts() {
    const response = await fetch(`${DATABASE_ROOT_DOMAIN}/posts/all`);
    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Posts fetching error.");
    }

    const convertedPosts = data.payload?.content ?? [];

    return convertedPosts;
}

export async function getPost(postId) {
    const response = await fetch(`${DATABASE_ROOT_DOMAIN}/posts/${postId}`);
    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Post fetching error.");
    }

    return data.payload;
}

// export async function getUserPosts(userId) {
//     const response = await fetch(
//         `${DATABASE_ROOT_DOMAIN}/posts/user/${userId}`
//     );
//     const data = await response.json();

//     if (!response.ok) {
//         throw new Error(data.message || "Posts fetching error.");
//     }

//     const convertedPosts = [];

//     for (const key in data) {
//         const post = {
//             id: key,
//             ...data[key],
//         };

//         convertedPosts.push(post);
//     }

//     return convertedPosts;
// }

export async function getUserPostsByName(username) {
    const response = await fetch(
        `${DATABASE_ROOT_DOMAIN}/posts/username/${username}`
    );
    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Posts fetching error.");
    }

    return data?.payload ?? [];
}

export async function getFilteredPosts(searchText) {
    const response = await fetch(`${DATABASE_ROOT_DOMAIN}/posts/search`, {
        method: "POST",
        body: JSON.stringify({ content: searchText }),
        headers: {
            "Content-Type": "application/json",
        },
    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Posts fetching error.");
    }

    return data?.payload?.content ?? [];
}

export async function deletePost(postId) {
    const token = localStorage.getItem("authToken");

    const response = await fetch(`${DATABASE_ROOT_DOMAIN}/posts/${postId}`, {
        method: "DELETE",
        body: JSON.stringify(postId),
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
    });
    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.text || "Post deleting error.");
    }
    return data;
}

export async function addPost(postData) {
    const token = localStorage.getItem("authToken");

    const response = await fetch(`${DATABASE_ROOT_DOMAIN}/posts/create`, {
        method: "POST",
        body: JSON.stringify(postData),
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
    });
    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Post adding error.");
    }

    return data;
}

export async function addUser(UserData) {
    const response = await fetch(`${DATABASE_ROOT_DOMAIN}/auth/register`, {
        method: "POST",
        body: JSON.stringify(UserData),
        headers: {
            "Content-Type": "application/json",
        },
    });
    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Post adding error.");
    }
}

export async function deleteUser(userId) {
    const token = localStorage.getItem("authToken");
    const response = await fetch(`${DATABASE_ROOT_DOMAIN}/users/${userId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
    });
    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Post adding error.");
    }
}

export async function makeAdmin(userId) {
    const token = localStorage.getItem("authToken");
    const response = await fetch(
        `${DATABASE_ROOT_DOMAIN}/users/make+admin/${userId}`,
        {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        }
    );
    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Post adding error.");
    }
}

export async function login(UserData) {
    const response = await fetch(`${DATABASE_ROOT_DOMAIN}/auth/login`, {
        method: "POST",
        body: JSON.stringify(UserData),
        headers: {
            "Content-Type": "application/json",
        },
    });
    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Logging error.");
    }
    return data?.payload;
}

export async function getUserComments(username) {
    console.log(username);
    const response = await fetch(
        `${DATABASE_ROOT_DOMAIN}/comments/users/${username}`
    );
    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Posts fetching error.");
    }

    const convertedComments = [];

    for (const key in data) {
        const comment = {
            id: key,
            ...data[key],
        };

        convertedComments.push(comment);
    }

    return convertedComments;
}

export async function getId(login) {
    const response = await fetch(`${DATABASE_ROOT_DOMAIN}/username/${login}`);
    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Posts fetching error.");
    }

    return data;
}

// export async function addPost(PostData) {
//     const token = localStorage.getItem("authToken");
//     console.log(localStorage.getItem("authToken"));
//     console.log(PostData);
//     const response = await fetch(`${DATABASE_ROOT_DOMAIN}/posts`, {
//         method: "POST",
//         body: JSON.stringify(PostData),
//         headers: {
//             "Content-Type": "application/json",
//             Authorization: `Bearer ${token}`,
//         },
//     });
//     const data = await response.json();

//     if (!response.ok) {
//         throw new Error(data.message || "Post adding error.");
//     }

//     return data;
// }

export async function addComment(CommentData) {
    const token = localStorage.getItem("authToken");
    console.log(CommentData);
    console.log(token);
    const response = await fetch(`${DATABASE_ROOT_DOMAIN}/comments/create`, {
        method: "POST",
        body: JSON.stringify(CommentData),
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
    });
    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.text || "Comment adding error.");
    }
    return data;
}

export async function addRepliedComment(CommentData) {
    const token = localStorage.getItem("authToken");
    console.log(CommentData);
    console.log(token);
    const response = await fetch(
        `${DATABASE_ROOT_DOMAIN}/comments/create/replied`,
        {
            method: "POST",
            body: JSON.stringify(CommentData),
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        }
    );
    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.text || "Comment adding error.");
    }
    return data;
}

export async function getComments(postId) {
    const response = await fetch(
        `${DATABASE_ROOT_DOMAIN}/comments/tree/${postId}`
    );

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Comments fetching error.");
    }

    return data?.payload ?? [];
}

export async function getAllComments() {
    const token = localStorage.getItem("authToken");
    const response = await fetch(`${DATABASE_ROOT_DOMAIN}/comments/all`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
    });

    // const response = await fetch(
    //         `${DATABASE_ROOT_DOMAIN}/comments/${CommentData.comment_Id}`
    //     );

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Comments fetching error.");
    }

    return data?.payload?.content ?? [];
}

// export async function getRepliedComments(commentId) {
//     const response = await fetch(
//         `${DATABASE_ROOT_DOMAIN}/comments/replied/${commentId}`
//     );

//     const data = await response.json();

//     if (!response.ok) {
//         throw new Error(data.message || "Comments fetching error.");
//     }

//     const convertedComments = [];

//     for (const key in data) {
//         const comment = {
//             id: key,
//             ...data[key],
//         };

//         convertedComments.push(comment);
//     }

//     return convertedComments;
// }

export async function deleteComment(CommentData) {
    const token = localStorage.getItem("authToken");

    const response = await fetch(
        `${DATABASE_ROOT_DOMAIN}/comments/${CommentData.comment_Id}`,
        {
            method: "DELETE",
            body: JSON.stringify(CommentData),
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        }
    );
    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.text || "Comment adding error.");
    }
    return data;
}

export async function getAllUsers() {
    const token = localStorage.getItem("authToken");
    const response = await fetch(`${DATABASE_ROOT_DOMAIN}/users/all`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Comments fetching error.");
    }

    return data?.payload?.content ?? [];
}

export async function getAllDeletedUsers() {
    const token = localStorage.getItem("authToken");
    const response = await fetch(`${DATABASE_ROOT_DOMAIN}/users/all/deleted`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Comments fetching error.");
    }

    return data?.payload?.content ?? [];
}

export async function unbanUser(userId) {
    const token = localStorage.getItem("authToken");
    const response = await fetch(
        `${DATABASE_ROOT_DOMAIN}/users/unban/${userId}`,
        {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Unban user error.");
    }
}
