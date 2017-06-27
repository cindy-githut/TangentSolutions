package com.solutions.tangent.tangentsolutions;

import okhttp3.MediaType;

/**
 * Created by cindymbonani on 2017/06/27.
 */

public class NetworkUrls {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static final String BASE_USERSERVICE_URL = "http://userservice.staging.tangentmicroservices.com";

    public static String authenticateUserLogin(){
        return NetworkUrls.BASE_USERSERVICE_URL+":80/api-token-auth/";
    }

}
