/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.impl.auth;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.hc.client5.http.auth.AuthChallenge;
import org.apache.hc.client5.http.auth.AuthScheme;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.AuthStateCacheable;
import org.apache.hc.client5.http.auth.AuthenticationException;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.MalformedChallengeException;
import org.apache.hc.client5.http.impl.auth.AuthSchemeSupport;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.utils.Base64;
import org.apache.hc.client5.http.utils.ByteArrayBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AuthStateCacheable
public class BasicScheme
implements AuthScheme,
Serializable {
    private static final long serialVersionUID = -1931571557597830536L;
    private static final Logger LOG = LoggerFactory.getLogger(BasicScheme.class);
    private final Map<String, String> paramMap = new HashMap<String, String>();
    private transient Charset defaultCharset;
    private transient ByteArrayBuilder buffer;
    private transient Base64 base64codec;
    private boolean complete;
    private String username;
    private char[] password;

    public BasicScheme(Charset charset) {
        this.defaultCharset = charset != null ? charset : StandardCharsets.US_ASCII;
        this.complete = false;
    }

    public BasicScheme() {
        this(StandardCharsets.US_ASCII);
    }

    private void applyCredentials(Credentials credentials) {
        this.username = credentials.getUserPrincipal().getName();
        this.password = credentials.getPassword();
    }

    private void clearCredentials() {
        this.username = null;
        this.password = null;
    }

    public void initPreemptive(Credentials credentials) {
        if (credentials != null) {
            this.applyCredentials(credentials);
        } else {
            this.clearCredentials();
        }
    }

    @Override
    public String getName() {
        return "Basic";
    }

    @Override
    public boolean isConnectionBased() {
        return false;
    }

    @Override
    public String getRealm() {
        return this.paramMap.get("realm");
    }

    @Override
    public void processChallenge(AuthChallenge authChallenge, HttpContext context) throws MalformedChallengeException {
        this.paramMap.clear();
        List<NameValuePair> params = authChallenge.getParams();
        if (params != null) {
            for (NameValuePair param : params) {
                this.paramMap.put(param.getName().toLowerCase(Locale.ROOT), param.getValue());
            }
        }
        this.complete = true;
    }

    @Override
    public boolean isChallengeComplete() {
        return this.complete;
    }

    @Override
    public boolean isResponseReady(HttpHost host, CredentialsProvider credentialsProvider, HttpContext context) throws AuthenticationException {
        Args.notNull(host, "Auth host");
        Args.notNull(credentialsProvider, "CredentialsProvider");
        AuthScope authScope = new AuthScope(host, this.getRealm(), this.getName());
        Credentials credentials = credentialsProvider.getCredentials(authScope, context);
        if (credentials != null) {
            this.applyCredentials(credentials);
            return true;
        }
        if (LOG.isDebugEnabled()) {
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            String exchangeId = clientContext.getExchangeId();
            LOG.debug("{} No credentials found for auth scope [{}]", (Object)exchangeId, (Object)authScope);
        }
        this.clearCredentials();
        return false;
    }

    @Override
    public Principal getPrincipal() {
        return null;
    }

    private void validateUsername() throws AuthenticationException {
        if (this.username == null) {
            throw new AuthenticationException("User credentials not set");
        }
        for (int i = 0; i < this.username.length(); ++i) {
            char ch = this.username.charAt(i);
            if (Character.isISOControl(ch)) {
                throw new AuthenticationException("Username must not contain any control characters");
            }
            if (ch != ':') continue;
            throw new AuthenticationException("Username contains a colon character and is invalid");
        }
    }

    @Override
    public String generateAuthResponse(HttpHost host, HttpRequest request, HttpContext context) throws AuthenticationException {
        this.validateUsername();
        if (this.buffer == null) {
            this.buffer = new ByteArrayBuilder(64);
        } else {
            this.buffer.reset();
        }
        Charset charset = AuthSchemeSupport.parseCharset(this.paramMap.get("charset"), this.defaultCharset);
        this.buffer.charset(charset);
        this.buffer.append(this.username).append(":").append(this.password);
        if (this.base64codec == null) {
            this.base64codec = new Base64();
        }
        byte[] encodedCreds = this.base64codec.encode(this.buffer.toByteArray());
        this.buffer.reset();
        return "Basic " + new String(encodedCreds, 0, encodedCreds.length, StandardCharsets.US_ASCII);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(this.defaultCharset.name());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        try {
            this.defaultCharset = Charset.forName(in.readUTF());
        }
        catch (UnsupportedCharsetException ex) {
            this.defaultCharset = StandardCharsets.US_ASCII;
        }
    }

    private void readObjectNoData() {
    }

    public String toString() {
        return this.getName() + this.paramMap;
    }
}

