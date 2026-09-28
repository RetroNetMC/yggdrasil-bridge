package com.czompi.mcservicessdk.utils;

public class MSAConstants {

    public static final String CLIENT_ID = System.getenv("MSA_CLIENT_ID");
    public static final String CLIENT_SECRET = System.getenv("MSA_CLIENT_SECRET");
    public static final String BASE_URL = System.getenv("BASE_URL");
    public static final String REDIRECT_URI = BASE_URL + "/auth/callback";

}
