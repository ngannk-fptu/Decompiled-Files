/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio.ssl;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import org.apache.hc.core5.http.nio.ssl.SecurePortStrategy;
import org.apache.hc.core5.util.Args;

@Deprecated
public final class FixedPortStrategy
implements SecurePortStrategy {
    private final int[] securePorts;

    public FixedPortStrategy(int ... securePorts) {
        this.securePorts = Args.notNull(securePorts, "Secure ports");
    }

    @Override
    public boolean isSecure(SocketAddress localAddress) {
        int port = ((InetSocketAddress)localAddress).getPort();
        for (int securePort : this.securePorts) {
            if (port != securePort) continue;
            return true;
        }
        return false;
    }
}

