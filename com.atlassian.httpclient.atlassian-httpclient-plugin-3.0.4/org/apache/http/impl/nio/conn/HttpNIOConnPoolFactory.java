/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.conn;

import java.io.IOException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.nio.pool.NIOConnFactory;
import org.apache.http.nio.reactor.IOSession;

@Deprecated
class HttpNIOConnPoolFactory
implements NIOConnFactory<HttpRoute, IOSession> {
    HttpNIOConnPoolFactory() {
    }

    @Override
    public IOSession create(HttpRoute route, IOSession session) throws IOException {
        return session;
    }
}

