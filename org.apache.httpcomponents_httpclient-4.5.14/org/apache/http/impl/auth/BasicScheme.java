/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.http.Consts
 *  org.apache.http.Header
 *  org.apache.http.HttpRequest
 *  org.apache.http.message.BufferedHeader
 *  org.apache.http.protocol.BasicHttpContext
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.Args
 *  org.apache.http.util.CharArrayBuffer
 *  org.apache.http.util.EncodingUtils
 */
package org.apache.http.impl.auth;

import java.nio.charset.Charset;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.ChallengeState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.impl.auth.RFC2617Scheme;
import org.apache.http.message.BufferedHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EncodingUtils;

public class BasicScheme
extends RFC2617Scheme {
    private static final long serialVersionUID = -1931571557597830536L;
    private boolean complete;

    public BasicScheme(Charset credentialsCharset) {
        super(credentialsCharset);
        this.complete = false;
    }

    @Deprecated
    public BasicScheme(ChallengeState challengeState) {
        super(challengeState);
    }

    public BasicScheme() {
        this(Consts.ASCII);
    }

    @Override
    public String getSchemeName() {
        return "basic";
    }

    @Override
    public void processChallenge(Header header) throws MalformedChallengeException {
        super.processChallenge(header);
        this.complete = true;
    }

    @Override
    public boolean isComplete() {
        return this.complete;
    }

    @Override
    public boolean isConnectionBased() {
        return false;
    }

    @Override
    @Deprecated
    public Header authenticate(Credentials credentials, HttpRequest request) throws AuthenticationException {
        return this.authenticate(credentials, request, (HttpContext)new BasicHttpContext());
    }

    @Override
    public Header authenticate(Credentials credentials, HttpRequest request, HttpContext context) throws AuthenticationException {
        Args.notNull((Object)credentials, (String)"Credentials");
        Args.notNull((Object)request, (String)"HTTP request");
        StringBuilder tmp = new StringBuilder();
        tmp.append(credentials.getUserPrincipal().getName());
        tmp.append(":");
        tmp.append(credentials.getPassword() == null ? "null" : credentials.getPassword());
        Base64 base64codec = new Base64(0);
        byte[] base64password = base64codec.encode(EncodingUtils.getBytes((String)tmp.toString(), (String)this.getCredentialsCharset(request)));
        CharArrayBuffer buffer = new CharArrayBuffer(32);
        if (this.isProxy()) {
            buffer.append("Proxy-Authorization");
        } else {
            buffer.append("Authorization");
        }
        buffer.append(": Basic ");
        buffer.append(base64password, 0, base64password.length);
        return new BufferedHeader(buffer);
    }

    @Deprecated
    public static Header authenticate(Credentials credentials, String charset, boolean proxy) {
        Args.notNull((Object)credentials, (String)"Credentials");
        Args.notNull((Object)charset, (String)"charset");
        StringBuilder tmp = new StringBuilder();
        tmp.append(credentials.getUserPrincipal().getName());
        tmp.append(":");
        tmp.append(credentials.getPassword() == null ? "null" : credentials.getPassword());
        byte[] base64password = Base64.encodeBase64((byte[])EncodingUtils.getBytes((String)tmp.toString(), (String)charset), (boolean)false);
        CharArrayBuffer buffer = new CharArrayBuffer(32);
        if (proxy) {
            buffer.append("Proxy-Authorization");
        } else {
            buffer.append("Authorization");
        }
        buffer.append(": Basic ");
        buffer.append(base64password, 0, base64password.length);
        return new BufferedHeader(buffer);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BASIC [complete=").append(this.complete).append("]");
        return builder.toString();
    }
}

