/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.reactor;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Set;
import java.util.concurrent.Future;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.reactor.ListenerEndpoint;

public interface ConnectionAcceptor {
    default public Future<ListenerEndpoint> listen(SocketAddress address, Object attachment, FutureCallback<ListenerEndpoint> callback) {
        return this.listen(address, callback);
    }

    public Future<ListenerEndpoint> listen(SocketAddress var1, FutureCallback<ListenerEndpoint> var2);

    public void pause() throws IOException;

    public void resume() throws IOException;

    public Set<ListenerEndpoint> getEndpoints();
}

