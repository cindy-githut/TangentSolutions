package com.solutions.tangent.tangentsolutions;

import okhttp3.MediaType;

/**
 * Created by cindymbonani on 2017/06/27.
 */

public class NetworkUrls {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static final String AUTHENTICATE_USER_LOGIN = "http://userservice.staging.tangentmicroservices.com:80/api-token-auth/";
    public static final String BASE_PROJECT_URL = "http://projectservice.staging.tangentmicroservices.com/api/v1/projects/";
    public static final String GET_USER_PROFILE = "http://userservice.staging.tangentmicroservices.com:80/api/v1/users/me/";

}
