package com.push.app.utility;

import com.google.gson.Gson;
import com.push.app.model.payload.Response;
import org.springframework.http.*;
import org.springframework.security.core.*;
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

    public static ResponseEntity setResponse(String message, Object data) {
        Response resp = new Response();
        resp.setCode(HttpStatus.OK.value());
        resp.setStatus(HttpStatus.OK.getReasonPhrase());
        resp.setMessage(message);
        resp.setData(data);
        return new ResponseEntity<Response>(resp, HttpStatus.OK);
    }
}
