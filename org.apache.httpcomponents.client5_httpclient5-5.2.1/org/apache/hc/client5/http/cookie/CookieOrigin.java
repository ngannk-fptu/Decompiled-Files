/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.TextUtils
 */
package org.apache.hc.client5.http.cookie;

import java.util.Locale;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TextUtils;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public final class CookieOrigin {
    private final String host;
    private final int port;
    private final String path;
    private final boolean secure;

    public CookieOrigin(String host, int port, String path, boolean secure) {
        Args.notBlank((CharSequence)host, (String)"Host");
        Args.notNegative((int)port, (String)"Port");
        Args.notNull((Object)path, (String)"Path");
        this.host = host.toLowerCase(Locale.ROOT);
        this.port = port;
        this.path = !TextUtils.isBlank((CharSequence)path) ? path : "/";
        this.secure = secure;
    }

    public String getHost() {
        return this.host;
    }

    public String getPath() {
        return this.path;
    }

    public int getPort() {
        return this.port;
    }

    public boolean isSecure() {
        return this.secure;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append('[');
        if (this.secure) {
            buffer.append("(secure)");
        }
        buffer.append(this.host);
        buffer.append(':');
        buffer.append(this.port);
        buffer.append(this.path);
        buffer.append(']');
        return buffer.toString();
    }
}

