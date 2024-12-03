/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 */
package org.apache.jackrabbit.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.jcr.Credentials;
import javax.jcr.GuestCredentials;
import javax.jcr.LoginException;
import javax.jcr.SimpleCredentials;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.jackrabbit.server.CredentialsProvider;
import org.apache.jackrabbit.util.Base64;

public class BasicCredentialsProvider
implements CredentialsProvider {
    public static final String EMPTY_DEFAULT_HEADER_VALUE = "";
    public static final String GUEST_DEFAULT_HEADER_VALUE = "guestcredentials";
    private final String defaultHeaderValue;

    public BasicCredentialsProvider(String defaultHeaderValue) {
        this.defaultHeaderValue = defaultHeaderValue;
    }

    @Override
    public Credentials getCredentials(HttpServletRequest request) throws LoginException, ServletException {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null) {
                String[] authStr = authHeader.split(" ");
                if (authStr.length >= 2 && authStr[0].equalsIgnoreCase("BASIC")) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    Base64.decode(authStr[1].toCharArray(), (OutputStream)out);
                    String decAuthStr = out.toString("ISO-8859-1");
                    int pos = decAuthStr.indexOf(58);
                    String userid = decAuthStr.substring(0, pos);
                    String passwd = decAuthStr.substring(pos + 1);
                    return new SimpleCredentials(userid, passwd.toCharArray());
                }
                throw new ServletException("Unable to decode authorization.");
            }
            if (this.defaultHeaderValue == null) {
                throw new LoginException();
            }
            if (EMPTY_DEFAULT_HEADER_VALUE.equals(this.defaultHeaderValue)) {
                return null;
            }
            if (GUEST_DEFAULT_HEADER_VALUE.equals(this.defaultHeaderValue)) {
                return new GuestCredentials();
            }
            int pos = this.defaultHeaderValue.indexOf(58);
            if (pos < 0) {
                return new SimpleCredentials(this.defaultHeaderValue, new char[0]);
            }
            return new SimpleCredentials(this.defaultHeaderValue.substring(0, pos), this.defaultHeaderValue.substring(pos + 1).toCharArray());
        }
        catch (IOException e) {
            throw new ServletException("Unable to decode authorization: " + e.toString());
        }
    }
}

