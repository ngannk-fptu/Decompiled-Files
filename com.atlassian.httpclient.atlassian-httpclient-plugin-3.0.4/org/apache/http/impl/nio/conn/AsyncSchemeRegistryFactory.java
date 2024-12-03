/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.conn;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.nio.conn.scheme.AsyncScheme;
import org.apache.http.nio.conn.scheme.AsyncSchemeRegistry;
import org.apache.http.nio.conn.ssl.SSLLayeringStrategy;

@Deprecated
@Contract(threading=ThreadingBehavior.SAFE)
public final class AsyncSchemeRegistryFactory {
    public static AsyncSchemeRegistry createDefault() {
        AsyncSchemeRegistry registry = new AsyncSchemeRegistry();
        registry.register(new AsyncScheme("http", 80, null));
        registry.register(new AsyncScheme("https", 443, SSLLayeringStrategy.getDefaultStrategy()));
        return registry;
    }
}

