package com.push.app.config.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

@Configuration
public class ResourceConfig {

    private static final String RESOURCE_ID = "rest-api";

    @Configuration
    @Order(10)
    protected static class NonOauthResources extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers("/auth/login").permitAll()
                    .antMatchers("/**").permitAll()
                    .and().anonymous();
        }
    }

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Autowired
        Oauth2Properties properties;

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            RemoteTokenServices tokenService = new RemoteTokenServices();
            tokenService.setClientId(properties.getClientId());
            tokenService.setClientSecret(properties.getClientSecret());
            tokenService.setCheckTokenEndpointUrl(properties.getCheckTokenUrl());
            resources.resourceId(RESOURCE_ID).tokenServices(tokenService);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.anonymous().disable()
                    .authorizeRequests()
                    .antMatchers("/private").access("hasRole('CLIENT')")
                    .antMatchers("/private").access("hasRole('USER')")
                    .antMatchers("/api/**").access("hasRole('ADMIN')")
                    .and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
        }
    }
}
