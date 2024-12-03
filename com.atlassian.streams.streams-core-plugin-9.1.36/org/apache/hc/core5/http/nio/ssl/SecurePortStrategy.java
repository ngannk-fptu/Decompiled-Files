/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio.ssl;

import java.net.SocketAddress;

@Deprecated
public interface SecurePortStrategy {
    public boolean isSecure(SocketAddress var1);
}

