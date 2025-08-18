import { createSlice } from "@reduxjs/toolkit";

const initialState = {
    isCreatePostVisible: false,
    isFiltered: false,
    filter: localStorage.getItem("searchFilter") || "",
};

const mainSlice = createSlice({
    name: "main",
    initialState,
    reducers: {
        toggleCreatePostVisibility(state) {
            state.isCreatePostVisible = !state.isCreatePostVisible;
        },
        toggleSearchFilter(state) {
            state.isFiltered = !state.isFiltered;
        },
        setNewFilter(state, action) {
            console.log(action.payload);
            state.filter = action.payload;
            localStorage.setItem("searchFilter", action.payload);
            console.log(state.filter);
        },
    },
});

export const mainActions = mainSlice.actions;

export default mainSlice;
