package com.push.app.utility;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;

import java.io.Serializable;
import java.util.*;

public class CustomAccessTokenConverter extends DefaultAccessTokenConverter {

    private UserAuthenticationConverter userTokenConverter = new DefaultUserAuthenticationConverter();

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        Map<String, String> parameters = new HashMap<String, String>();
        @SuppressWarnings("unchecked")
        Set<String> scope = new LinkedHashSet<String>(map.containsKey(SCOPE) ? (Collection<String>) map.get(SCOPE)
                : Collections.<String>emptySet());
        Authentication user = userTokenConverter.extractAuthentication(map);
        String clientId = (String) map.get(CLIENT_ID);
        parameters.put(CLIENT_ID, clientId);
        parameters.put("account_information", String.valueOf((((Map) map.get("account_information")).get("accountid"))));
        @SuppressWarnings("unchecked")
        Set<String> resourceIds = new LinkedHashSet<String>(map.containsKey(AUD) ? (Collection<String>) map.get(AUD)
                : Collections.<String>emptySet());

        Map<String, Serializable> extensions = new HashMap<String, Serializable>();
        extensions.put("account_information", (HashMap) map.get("account_information"));

        OAuth2Request request = new OAuth2Request(parameters, clientId, null, true, scope, resourceIds, null, null,
                extensions);
        return new OAuth2Authentication(request, user);
    }

}
