/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.AuthenticationException;

public abstract class Authenticator {
    private static final StringManager sm = StringManager.getManager(Authenticator.class);
    private static final Pattern pattern = Pattern.compile("(\\w+)\\s*=\\s*(\"([^\"]+)\"|([^,=\"]+))\\s*,?");

    @Deprecated
    public String getAuthorization(String requestUri, String authenticateHeader, Map<String, Object> userProperties) throws AuthenticationException {
        return this.getAuthorization(requestUri, authenticateHeader, (String)userProperties.get("org.apache.tomcat.websocket.WS_AUTHENTICATION_USER_NAME"), (String)userProperties.get("org.apache.tomcat.websocket.WS_AUTHENTICATION_PASSWORD"), (String)userProperties.get("org.apache.tomcat.websocket.WS_AUTHENTICATION_REALM"));
    }

    public abstract String getAuthorization(String var1, String var2, String var3, String var4, String var5) throws AuthenticationException;

    public abstract String getSchemeName();

    @Deprecated
    public Map<String, String> parseWWWAuthenticateHeader(String authenticateHeader) {
        return this.parseAuthenticateHeader(authenticateHeader);
    }

    public Map<String, String> parseAuthenticateHeader(String authenticateHeader) {
        Matcher m = pattern.matcher(authenticateHeader);
        HashMap<String, String> parameterMap = new HashMap<String, String>();
        while (m.find()) {
            String key = m.group(1);
            String qtedValue = m.group(3);
            String value = m.group(4);
            parameterMap.put(key, qtedValue != null ? qtedValue : value);
        }
        return parameterMap;
    }

    protected void validateUsername(String userName) throws AuthenticationException {
        if (userName == null) {
            throw new AuthenticationException(sm.getString("authenticator.nullUserName"));
        }
    }

    protected void validatePassword(String password) throws AuthenticationException {
        if (password == null) {
            throw new AuthenticationException(sm.getString("authenticator.nullPassword"));
        }
    }

    protected void validateRealm(String userRealm, String serverRealm) throws AuthenticationException {
        if (userRealm == null) {
            return;
        }
        if ((userRealm = userRealm.trim()).length() == 0) {
            return;
        }
        if (serverRealm != null && userRealm.equals(serverRealm = serverRealm.trim())) {
            return;
        }
        throw new AuthenticationException(sm.getString("authenticator.realmMismatch", new Object[]{userRealm, serverRealm}));
    }
}

