import { Route, Routes, BrowserRouter as Router } from "react-router-dom";

import NotFound from "./pages/NotFound";
import NavigationBar from "./components/NavigationBar";
import SideBar from "./components/SideBar";
import Home from "./pages/Home";
import Login from "./pages/Login";
import MyComments from "./pages/MyComments";
import User from "./pages/User";
import MyPosts from "./pages/MyPosts";
import Post from "./pages/Post";
import Search from "./pages/Search";
import Layout from "./Layout";
import AdminPage from "./pages/AdminPage";

function App() {
    return (
        <Router>
            <NavigationBar />
            <SideBar />
            <Layout>
                <Routes>
                    <Route path="/not-found" element={<NotFound />} />
                    <Route path="/" element={<Home />} />
                    <Route path="/search/:searchText" element={<Search />} />
                    <Route path="/post/:postId" element={<Post />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/my-comments" element={<MyComments />} />
                    <Route path="/user/:username" element={<User />} />
                    <Route path="/my-posts" element={<MyPosts />} />
                    <Route path="/admin" element={<AdminPage />} />
                </Routes>
            </Layout>
        </Router>
    );
}

export default App;
