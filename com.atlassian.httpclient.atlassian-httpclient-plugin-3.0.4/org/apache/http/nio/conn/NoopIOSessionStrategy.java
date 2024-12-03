/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.conn;

import org.apache.http.HttpHost;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.reactor.IOSession;

public class NoopIOSessionStrategy
implements SchemeIOSessionStrategy {
    public static final NoopIOSessionStrategy INSTANCE = new NoopIOSessionStrategy();

    @Override
    public IOSession upgrade(HttpHost host, IOSession ioSession) {
        return ioSession;
    }

    @Override
    public boolean isLayeringRequired() {
        return false;
    }
}

