/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.net;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.Objects;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.net.Host;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.net.URISupport;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.LangUtils;
import org.apache.hc.core5.util.TextUtils;
import org.apache.hc.core5.util.Tokenizer;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public final class URIAuthority
implements NamedEndpoint,
Serializable {
    private static final long serialVersionUID = 1L;
    private final String userInfo;
    private final Host host;

    static URIAuthority parse(CharSequence s, Tokenizer.Cursor cursor) throws URISyntaxException {
        Tokenizer tokenizer = Tokenizer.INSTANCE;
        String userInfo = null;
        int initPos = cursor.getPos();
        String token = tokenizer.parseContent(s, cursor, URISupport.HOST_SEPARATORS);
        if (!cursor.atEnd() && s.charAt(cursor.getPos()) == '@') {
            cursor.updatePos(cursor.getPos() + 1);
            if (!TextUtils.isBlank(token)) {
                userInfo = token;
            }
        } else {
            cursor.updatePos(initPos);
        }
        Host host = Host.parse(s, cursor);
        return new URIAuthority(userInfo, host);
    }

    static URIAuthority parse(CharSequence s) throws URISyntaxException {
        Tokenizer.Cursor cursor = new Tokenizer.Cursor(0, s.length());
        return URIAuthority.parse(s, cursor);
    }

    static void format(StringBuilder buf, URIAuthority uriAuthority) {
        if (uriAuthority.getUserInfo() != null) {
            buf.append(uriAuthority.getUserInfo());
            buf.append("@");
        }
        Host.format(buf, uriAuthority);
    }

    static String format(URIAuthority uriAuthority) {
        StringBuilder buf = new StringBuilder();
        URIAuthority.format(buf, uriAuthority);
        return buf.toString();
    }

    public URIAuthority(String userInfo, String hostname, int port) {
        this.userInfo = userInfo;
        this.host = new Host(hostname, port);
    }

    public URIAuthority(String hostname, int port) {
        this(null, hostname, port);
    }

    public URIAuthority(String userInfo, Host host) {
        Args.notNull(host, "Host");
        this.userInfo = userInfo;
        this.host = host;
    }

    public URIAuthority(Host host) {
        this(null, host);
    }

    public URIAuthority(String userInfo, NamedEndpoint endpoint) {
        Args.notNull(endpoint, "Endpoint");
        this.userInfo = userInfo;
        this.host = new Host(endpoint.getHostName(), endpoint.getPort());
    }

    public URIAuthority(NamedEndpoint namedEndpoint) {
        this(null, namedEndpoint);
    }

    public static URIAuthority create(String s) throws URISyntaxException {
        if (TextUtils.isBlank(s)) {
            return null;
        }
        Tokenizer.Cursor cursor = new Tokenizer.Cursor(0, s.length());
        URIAuthority uriAuthority = URIAuthority.parse(s, cursor);
        if (!cursor.atEnd()) {
            throw URISupport.createException(s, cursor, "Unexpected content");
        }
        return uriAuthority;
    }

    public URIAuthority(String hostname) {
        this(null, hostname, -1);
    }

    public String getUserInfo() {
        return this.userInfo;
    }

    @Override
    public String getHostName() {
        return this.host.getHostName();
    }

    @Override
    public int getPort() {
        return this.host.getPort();
    }

    public String toString() {
        return URIAuthority.format(this);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof URIAuthority) {
            URIAuthority that = (URIAuthority)obj;
            return Objects.equals(this.userInfo, that.userInfo) && Objects.equals(this.host, that.host);
        }
        return false;
    }

    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.userInfo);
        hash = LangUtils.hashCode(hash, this.host);
        return hash;
    }
}

