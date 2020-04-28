package com.push.app.utility;

import com.google.gson.Gson;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class Utility {
    public static String objectToString(Object o) {
        Gson g = new Gson();
        String json = g.toJson(o);
        return json;
    }

    public static Object jsonToObject(String json) {
        Gson g = new Gson();
        Object o = g.fromJson(json, Object.class);
        return o;
    }

    public static String getUser() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        return authentication.getName();
    }
}
