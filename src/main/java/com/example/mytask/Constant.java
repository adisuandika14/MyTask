package com.example.mytask;

public class Constant {
        public static final String URL = "http://192.168.1.12";
        public static final String HOME = URL+"/api";
        public static final String LOGIN = HOME+"/auth/login";
        public static final String REGISTER = HOME+"/auth/signup";
        public static final String SAVE_USER_INFO = HOME+"/auth/save_user_info";
        public static final String TASK = HOME+"/tugas";
        public static final String ADD_POST = HOME+"/tugas/create";
        public static final String LOGOUT = HOME+"/auth/logout";
        public static final String UPDATE_POST = TASK+"/update";
        public static final String DELETE_POST = TASK+"/delete";
        public static final String COMMENT = TASK+"/comments";
        public static final String DELETE_COMMENT = TASK+"/comments/delete";
        public static final String CREATE_COMMENT = TASK+"/comments/create";
        public static final String MY_TASK = TASK+"/mytask";
}