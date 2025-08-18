import { configureStore } from "@reduxjs/toolkit";
import mainSlice from "./main-slice";

const store = configureStore({
    reducer: {
        main: mainSlice.reducer,
    },
});

export default store;
