/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.conn.scheme;

import org.apache.http.nio.reactor.IOSession;

@Deprecated
public interface LayeringStrategy {
    public boolean isSecure();

    public IOSession layer(IOSession var1);
}

