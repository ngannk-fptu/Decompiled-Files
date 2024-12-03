/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.net;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.Locale;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.net.InetAddressUtils;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.net.Ports;
import org.apache.hc.core5.net.URISupport;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.LangUtils;
import org.apache.hc.core5.util.TextUtils;
import org.apache.hc.core5.util.Tokenizer;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public final class Host
implements NamedEndpoint,
Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final String lcName;
    private final int port;

    public Host(String name, int port) {
        this.name = Args.notNull(name, "Host name");
        this.port = Ports.checkWithDefault(port);
        this.lcName = this.name.toLowerCase(Locale.ROOT);
    }

    static Host parse(CharSequence s, Tokenizer.Cursor cursor) throws URISyntaxException {
        int port;
        String hostName;
        boolean ipv6Brackets;
        Tokenizer tokenizer = Tokenizer.INSTANCE;
        boolean bl = ipv6Brackets = !cursor.atEnd() && s.charAt(cursor.getPos()) == '[';
        if (ipv6Brackets) {
            cursor.updatePos(cursor.getPos() + 1);
            hostName = tokenizer.parseContent(s, cursor, URISupport.IPV6_HOST_TERMINATORS);
            if (cursor.atEnd() || s.charAt(cursor.getPos()) != ']') {
                throw URISupport.createException(s, cursor, "Expected an IPv6 closing bracket ']'");
            }
            cursor.updatePos(cursor.getPos() + 1);
            if (!InetAddressUtils.isIPv6Address(hostName)) {
                throw URISupport.createException(s, cursor, "Expected an IPv6 address");
            }
        } else {
            hostName = tokenizer.parseContent(s, cursor, URISupport.PORT_SEPARATORS);
        }
        String portText = null;
        if (!cursor.atEnd() && s.charAt(cursor.getPos()) == ':') {
            cursor.updatePos(cursor.getPos() + 1);
            portText = tokenizer.parseContent(s, cursor, URISupport.TERMINATORS);
        }
        if (!TextUtils.isBlank(portText)) {
            if (!ipv6Brackets && portText.contains(":")) {
                throw URISupport.createException(s, cursor, "Expected IPv6 address to be enclosed in brackets");
            }
            try {
                port = Integer.parseInt(portText);
            }
            catch (NumberFormatException ex) {
                throw URISupport.createException(s, cursor, "Port is invalid");
            }
        } else {
            port = -1;
        }
        return new Host(hostName, port);
    }

    static Host parse(CharSequence s) throws URISyntaxException {
        Tokenizer.Cursor cursor = new Tokenizer.Cursor(0, s.length());
        return Host.parse(s, cursor);
    }

    static void format(StringBuilder buf, NamedEndpoint endpoint) {
        String hostName = endpoint.getHostName();
        if (InetAddressUtils.isIPv6Address(hostName)) {
            buf.append('[').append(hostName).append(']');
        } else {
            buf.append(hostName);
        }
        if (endpoint.getPort() != -1) {
            buf.append(":");
            buf.append(endpoint.getPort());
        }
    }

    static void format(StringBuilder buf, Host host) {
        Host.format(buf, (NamedEndpoint)host);
    }

    static String format(Host host) {
        StringBuilder buf = new StringBuilder();
        Host.format(buf, host);
        return buf.toString();
    }

    public static Host create(String s) throws URISyntaxException {
        Args.notEmpty(s, "HTTP Host");
        Tokenizer.Cursor cursor = new Tokenizer.Cursor(0, s.length());
        Host host = Host.parse(s, cursor);
        if (TextUtils.isBlank(host.getHostName())) {
            throw URISupport.createException(s, cursor, "Hostname is invalid");
        }
        if (!cursor.atEnd()) {
            throw URISupport.createException(s, cursor, "Unexpected content");
        }
        return host;
    }

    @Override
    public String getHostName() {
        return this.name;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Host) {
            Host that = (Host)o;
            return this.lcName.equals(that.lcName) && this.port == that.port;
        }
        return false;
    }

    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.lcName);
        hash = LangUtils.hashCode(hash, this.port);
        return hash;
    }

    public String toString() {
        return Host.format(this);
    }
}

