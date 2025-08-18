import { Fragment } from "react";
import React, { useEffect } from "react";
import { useLocation } from "react-router-dom";
import { useDispatch } from "react-redux";
import { mainActions } from "./store/main-slice";
import "./layout.css";

const Layout = (props) => {
    const location = useLocation();
    const dispatch = useDispatch();
    useEffect(() => {
        if (!location.pathname.startsWith("/search")) {
            dispatch(mainActions.setNewFilter(""));
        }
    }, [location, dispatch]);
    return (
        // <Fragment>
        //   <Header />
        //   <main>{props.children}</main>
        // </Fragment>
        <div className="layout">
            <div className="content">{props.children}</div>
        </div>
    );
};

export default Layout;
