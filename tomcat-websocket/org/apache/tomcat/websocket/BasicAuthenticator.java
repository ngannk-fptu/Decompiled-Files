/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.websocket;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.apache.tomcat.websocket.AuthenticationException;
import org.apache.tomcat.websocket.Authenticator;

public class BasicAuthenticator
extends Authenticator {
    public static final String schemeName = "basic";
    public static final String charsetparam = "charset";

    @Override
    public String getAuthorization(String requestUri, String authenticateHeader, String userName, String userPassword, String userRealm) throws AuthenticationException {
        this.validateUsername(userName);
        this.validatePassword(userPassword);
        Map<String, String> parameterMap = this.parseAuthenticateHeader(authenticateHeader);
        String realm = parameterMap.get("realm");
        this.validateRealm(userRealm, realm);
        String userPass = userName + ":" + userPassword;
        Charset charset = parameterMap.get(charsetparam) != null && parameterMap.get(charsetparam).equalsIgnoreCase("UTF-8") ? StandardCharsets.UTF_8 : StandardCharsets.ISO_8859_1;
        String base64 = Base64.getEncoder().encodeToString(userPass.getBytes(charset));
        return " Basic " + base64;
    }

    @Override
    public String getSchemeName() {
        return schemeName;
    }
}

