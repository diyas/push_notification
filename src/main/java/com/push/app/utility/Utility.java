package com.push.app.utility;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.push.app.config.oauth.Oauth2ResponseError;
import com.push.app.model.PaymentMethodEnum;
import com.push.app.model.payload.Response;
import org.springframework.http.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.mariadb.jdbc.internal.com.send.authentication.ed25519.Utils.bytesToHex;

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

    public static PaymentMethodEnum getById(Long id) {
        for(PaymentMethodEnum e : PaymentMethodEnum.values()) {
            if(e.code == id) return e;
        }
        return PaymentMethodEnum.UNKNOWN;
    }

    public static ResponseEntity setResponse(HttpStatus httpStatus, String message, Object data) {
        if (httpStatus.equals(HttpStatus.OK)) {
            return setResponse(message, data);
        }
        Oauth2ResponseError oauth2ResponseError = getClientMessage(message);
        Response resp = new Response();
        resp.setCode(httpStatus.value());
        resp.setStatus(httpStatus.getReasonPhrase());
        resp.setMessage(oauth2ResponseError.getErrorDescription());
        resp.setData(data);
        return new ResponseEntity<Response>(resp, httpStatus);
    }

    public static ResponseEntity setResponse(String message, Object data) {
        Response resp = new Response();
        resp.setCode(HttpStatus.OK.value());
        resp.setStatus(HttpStatus.OK.getReasonPhrase());
        resp.setMessage(message);
        resp.setData(data);
        return new ResponseEntity<Response>(resp, HttpStatus.OK);
    }

    public static Oauth2ResponseError getClientMessage(String message) {
        Oauth2ResponseError response = new Gson().fromJson(message, Oauth2ResponseError.class);
        return response;
    }

    public static boolean checkPasswordWithTimestamp(String passFromDb, String passRequest, String timeStamp) {
        boolean returnValue = false;
        try {
            MessageDigest mdSHA256 = MessageDigest.getInstance("SHA-256");
            byte[] baPasswdHashdb = mdSHA256.digest((timeStamp + passFromDb).getBytes(StandardCharsets.UTF_8));
            String strPasswdHashdb = bytesToHex(baPasswdHashdb).toLowerCase();

            if (passRequest.equals(strPasswdHashdb)) returnValue = true;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    public static Map toMap(Throwable map) {
        Map<String, String> newMap = new HashMap<>();
        if (map != null) {
            String[] aArr = (map.toString()).split(",");
            for (String strA : aArr) {
                String[] bArr = strA.split("=");
                for (String strB : bArr) {
                    newMap.put(bArr[0], strB.trim());
                }
            }
        } else {
            newMap.put("error", "");
            newMap.put("error_description", "");
        }
        return newMap;
    }
}
