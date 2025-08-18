import React, { useState, useContext, useEffect } from "react";
import styles from "./Login.module.css";
import { addUser, login } from "../utils/database-api";
import useHttp from "../hooks/use-http";
import UserContext from "../context/UserContext";
import { useNavigate } from "react-router-dom";

const Login = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const navigate = useNavigate();
    const { sendHttpRequest, status, data } = useHttp(addUser);
    const {
        sendHttpRequest: sendHttpRequestLog,
        status: statusLog,
        data: dataLog,
    } = useHttp(login);

    useEffect(() => {
        if (status === "completed") {
            alert(
                `Confirm your registration by email ${username}, then you can login`
            );
        }
    }, [status]);
    useEffect(() => {
        if (statusLog === "completed" && dataLog) {
            localStorage.setItem("authToken", dataLog.token);
            localStorage.setItem("name", username.split("@")[0]);
            localStorage.setItem("role", dataLog.roles[0].name);
            navigate("/");
            window.location.reload();
            // console.log("dataLog", dataLog);
        } else if (statusLog === "completed") {
            alert("Wrong email or password");
        }
    }, [statusLog]);

    const handleLogin = async (event) => {
        event.preventDefault();
        const user = {
            email: username,
            username: username.split("@")[0],
            password: password,
        };
        console.log("going fetch");

        try {
            await sendHttpRequestLog(user);
        } catch (error) {
            alert("Invalid email or password");
        }
    };

    const handleRegister = async (event) => {
        event.preventDefault();
        const user = {
            username: username,
            email: username,
            password: password,
            confirmPassword: password,
        };
        console.log("going fetch");
        console.log(user);
        await sendHttpRequest(user);
    };

    return (
        <div className={styles.container}>
            <form className={styles.form}>
                <input
                    type="text"
                    placeholder="Email"
                    className={styles.inputField}
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="Password"
                    className={styles.inputField}
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <div className={styles.buttonContainer}>
                    <button
                        className={`${styles.button} ${styles.loginButton}`}
                        onClick={handleLogin}
                    >
                        Log in
                    </button>
                    <button
                        className={`${styles.button} ${styles.registerButton}`}
                        onClick={handleRegister}
                    >
                        Register
                    </button>
                </div>
            </form>
        </div>
    );
};

export default Login;
