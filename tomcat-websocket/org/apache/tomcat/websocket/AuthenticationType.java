/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.websocket;

public enum AuthenticationType {
    WWW("Authorization", "WWW-Authenticate", "org.apache.tomcat.websocket.WS_AUTHENTICATION_USER_NAME", "org.apache.tomcat.websocket.WS_AUTHENTICATION_PASSWORD", "org.apache.tomcat.websocket.WS_AUTHENTICATION_REALM"),
    PROXY("Proxy-Authorization", "Proxy-Authenticate", "org.apache.tomcat.websocket.WS_AUTHENTICATION_PROXY_USER_NAME", "org.apache.tomcat.websocket.WS_AUTHENTICATION_PROXY_PASSWORD", "org.apache.tomcat.websocket.WS_AUTHENTICATION_PROXY_REALM");

    private final String authorizationHeaderName;
    private final String authenticateHeaderName;
    private final String userNameProperty;
    private final String userPasswordProperty;
    private final String userRealmProperty;

    private AuthenticationType(String authorizationHeaderName, String authenticateHeaderName, String userNameProperty, String userPasswordProperty, String userRealmProperty) {
        this.authorizationHeaderName = authorizationHeaderName;
        this.authenticateHeaderName = authenticateHeaderName;
        this.userNameProperty = userNameProperty;
        this.userPasswordProperty = userPasswordProperty;
        this.userRealmProperty = userRealmProperty;
    }

    public String getAuthorizationHeaderName() {
        return this.authorizationHeaderName;
    }

    public String getAuthenticateHeaderName() {
        return this.authenticateHeaderName;
    }

    public String getUserNameProperty() {
        return this.userNameProperty;
    }

    public String getUserPasswordProperty() {
        return this.userPasswordProperty;
    }

    public String getUserRealmProperty() {
        return this.userRealmProperty;
    }
}

