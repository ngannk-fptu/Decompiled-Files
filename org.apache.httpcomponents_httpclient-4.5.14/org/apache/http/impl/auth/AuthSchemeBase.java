/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.FormattedHeader
 *  org.apache.http.Header
 *  org.apache.http.HttpRequest
 *  org.apache.http.protocol.HTTP
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.Args
 *  org.apache.http.util.CharArrayBuffer
 */
package org.apache.http.impl.auth;

import java.util.Locale;
import org.apache.http.FormattedHeader;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.ChallengeState;
import org.apache.http.auth.ContextAwareAuthScheme;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

public abstract class AuthSchemeBase
implements ContextAwareAuthScheme {
    protected ChallengeState challengeState;

    @Deprecated
    public AuthSchemeBase(ChallengeState challengeState) {
        this.challengeState = challengeState;
    }

    public AuthSchemeBase() {
    }

    @Override
    public void processChallenge(Header header) throws MalformedChallengeException {
        int pos;
        CharArrayBuffer buffer;
        Args.notNull((Object)header, (String)"Header");
        String authheader = header.getName();
        if (authheader.equalsIgnoreCase("WWW-Authenticate")) {
            this.challengeState = ChallengeState.TARGET;
        } else if (authheader.equalsIgnoreCase("Proxy-Authenticate")) {
            this.challengeState = ChallengeState.PROXY;
        } else {
            throw new MalformedChallengeException("Unexpected header name: " + authheader);
        }
        if (header instanceof FormattedHeader) {
            buffer = ((FormattedHeader)header).getBuffer();
            pos = ((FormattedHeader)header).getValuePos();
        } else {
            String s = header.getValue();
            if (s == null) {
                throw new MalformedChallengeException("Header value is null");
            }
            buffer = new CharArrayBuffer(s.length());
            buffer.append(s);
            pos = 0;
        }
        while (pos < buffer.length() && HTTP.isWhitespace((char)buffer.charAt(pos))) {
            ++pos;
        }
        int beginIndex = pos;
        while (pos < buffer.length() && !HTTP.isWhitespace((char)buffer.charAt(pos))) {
            ++pos;
        }
        int endIndex = pos;
        String s = buffer.substring(beginIndex, endIndex);
        if (!s.equalsIgnoreCase(this.getSchemeName())) {
            throw new MalformedChallengeException("Invalid scheme identifier: " + s);
        }
        this.parseChallenge(buffer, pos, buffer.length());
    }

    @Override
    public Header authenticate(Credentials credentials, HttpRequest request, HttpContext context) throws AuthenticationException {
        return this.authenticate(credentials, request);
    }

    protected abstract void parseChallenge(CharArrayBuffer var1, int var2, int var3) throws MalformedChallengeException;

    public boolean isProxy() {
        return this.challengeState != null && this.challengeState == ChallengeState.PROXY;
    }

    public ChallengeState getChallengeState() {
        return this.challengeState;
    }

    public String toString() {
        String name = this.getSchemeName();
        return name != null ? name.toUpperCase(Locale.ROOT) : super.toString();
    }
}

