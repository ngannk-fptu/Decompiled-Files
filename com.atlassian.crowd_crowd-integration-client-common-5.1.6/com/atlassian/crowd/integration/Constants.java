/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.authentication.CookieConfiguration
 */
package com.atlassian.crowd.integration;

import com.atlassian.crowd.model.authentication.CookieConfiguration;

public class Constants {
    public static final String PROPERTIES_FILE = "crowd.properties";
    public static final String USE_ENVIRONMENT_VARIABLES = "atlassian.use.environment.variables";
    public static final String PROPERTIES_FILE_APPLICATION_NAME = "application.name";
    public static final String PROPERTIES_FILE_APPLICATION_PASSWORD = "application.password";
    public static final String PROPERTIES_FILE_APPLICATION_LOGIN_URL = "application.login.url";
    public static final String PROPERTIES_FILE_SECURITY_SERVER_URL = "crowd.server.url";
    public static final String PROPERTIES_FILE_BASE_URL = "crowd.base.url";
    public static final String PROPERTIES_FILE_COOKIE_TOKENKEY = "cookie.tokenkey";
    public static final String PROPERTIES_FILE_SESSIONKEY_TOKENKEY = "session.tokenkey";
    public static final String PROPERTIES_FILE_SESSIONKEY_VALIDATIONINTERVAL = "session.validationinterval";
    public static final String PROPERTIES_FILE_SESSIONKEY_LASTVALIDATION = "session.lastvalidation";
    public static final String PROPERTIES_FILE_HTTP_PROXY_HOST = "http.proxy.host";
    public static final String PROPERTIES_FILE_HTTP_PROXY_PORT = "http.proxy.port";
    public static final String PROPERTIES_FILE_HTTP_PROXY_USERNAME = "http.proxy.username";
    public static final String PROPERTIES_FILE_HTTP_PROXY_PASSWORD = "http.proxy.password";
    public static final String PROPERTIES_FILE_HTTP_MAX_CONNECTIONS = "http.max.connections";
    public static final String PROPERTIES_FILE_HTTP_TIMEOUT = "http.timeout";
    public static final String PROPERTIES_FILE_COOKIE_DOMAIN = "cookie.domain";
    public static final String PROPERTIES_FILE_SOCKET_TIMEOUT = "socket.timeout";
    public static final String COOKIE_TOKEN_KEY = CookieConfiguration.DEFAULT_COOKIE_TOKEN_KEY;
    public static final String COOKIE_PATH = "/";
    public static final String SECURITY_SERVER_NAME = "SecurityServer";
    public static final String CROWD_SERVICE_LOCATION = "services";
    public static final String CACHE_CONFIGURATION = "crowd-ehcache.xml";
    public static final String REQUEST_SSO_COOKIE_COMMITTED = "com.atlassian.crowd.integration.http.HttpAuthenticator.REQUEST_SSO_COOKIE_COMMITTED";
    public static final String AUTHENTICATION_METHOD = "authentication.method";
}

