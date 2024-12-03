/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient.auth;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.httpclient.auth.InvalidCredentialsException;
import org.apache.commons.httpclient.auth.MalformedChallengeException;
import org.apache.commons.httpclient.auth.RFC2617Scheme;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BasicScheme
extends RFC2617Scheme {
    private static final Log LOG = LogFactory.getLog(BasicScheme.class);
    private boolean complete;

    public BasicScheme() {
        this.complete = false;
    }

    public BasicScheme(String challenge) throws MalformedChallengeException {
        super(challenge);
        this.complete = true;
    }

    @Override
    public String getSchemeName() {
        return "basic";
    }

    @Override
    public void processChallenge(String challenge) throws MalformedChallengeException {
        super.processChallenge(challenge);
        this.complete = true;
    }

    @Override
    public boolean isComplete() {
        return this.complete;
    }

    @Override
    public String authenticate(Credentials credentials, String method, String uri) throws AuthenticationException {
        LOG.trace((Object)"enter BasicScheme.authenticate(Credentials, String, String)");
        UsernamePasswordCredentials usernamepassword = null;
        try {
            usernamepassword = (UsernamePasswordCredentials)credentials;
        }
        catch (ClassCastException e) {
            throw new InvalidCredentialsException("Credentials cannot be used for basic authentication: " + credentials.getClass().getName());
        }
        return BasicScheme.authenticate(usernamepassword);
    }

    @Override
    public boolean isConnectionBased() {
        return false;
    }

    @Override
    public String authenticate(Credentials credentials, HttpMethod method) throws AuthenticationException {
        LOG.trace((Object)"enter BasicScheme.authenticate(Credentials, HttpMethod)");
        if (method == null) {
            throw new IllegalArgumentException("Method may not be null");
        }
        UsernamePasswordCredentials usernamepassword = null;
        try {
            usernamepassword = (UsernamePasswordCredentials)credentials;
        }
        catch (ClassCastException e) {
            throw new InvalidCredentialsException("Credentials cannot be used for basic authentication: " + credentials.getClass().getName());
        }
        return BasicScheme.authenticate(usernamepassword, method.getParams().getCredentialCharset());
    }

    public static String authenticate(UsernamePasswordCredentials credentials) {
        return BasicScheme.authenticate(credentials, "ISO-8859-1");
    }

    public static String authenticate(UsernamePasswordCredentials credentials, String charset) {
        LOG.trace((Object)"enter BasicScheme.authenticate(UsernamePasswordCredentials, String)");
        if (credentials == null) {
            throw new IllegalArgumentException("Credentials may not be null");
        }
        if (charset == null || charset.length() == 0) {
            throw new IllegalArgumentException("charset may not be null or empty");
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(credentials.getUserName());
        buffer.append(":");
        buffer.append(credentials.getPassword());
        return "Basic " + EncodingUtil.getAsciiString(Base64.encodeBase64((byte[])EncodingUtil.getBytes(buffer.toString(), charset)));
    }
}

