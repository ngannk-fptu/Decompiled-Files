/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.reactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.io.Closer;
import org.apache.hc.core5.io.SocketTimeoutExceptionFactory;
import org.apache.hc.core5.reactor.IOEventHandlerFactory;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.reactor.IOSessionRequest;
import org.apache.hc.core5.reactor.InternalChannel;
import org.apache.hc.core5.reactor.InternalDataChannel;
import org.apache.hc.core5.reactor.SocksProxyProtocolHandler;
import org.apache.hc.core5.util.Timeout;

final class InternalConnectChannel
extends InternalChannel {
    private final SelectionKey key;
    private final SocketChannel socketChannel;
    private final IOSessionRequest sessionRequest;
    private final InternalDataChannel dataChannel;
    private final IOEventHandlerFactory eventHandlerFactory;
    private final IOReactorConfig reactorConfig;
    private final long creationTimeMillis;

    InternalConnectChannel(SelectionKey key, SocketChannel socketChannel, IOSessionRequest sessionRequest, InternalDataChannel dataChannel, IOEventHandlerFactory eventHandlerFactory, IOReactorConfig reactorConfig) {
        this.key = key;
        this.socketChannel = socketChannel;
        this.sessionRequest = sessionRequest;
        this.dataChannel = dataChannel;
        this.eventHandlerFactory = eventHandlerFactory;
        this.reactorConfig = reactorConfig;
        this.creationTimeMillis = System.currentTimeMillis();
    }

    @Override
    void onIOEvent(int readyOps) throws IOException {
        if ((readyOps & 8) != 0) {
            long now;
            if (this.socketChannel.isConnectionPending()) {
                this.socketChannel.finishConnect();
            }
            if (this.checkTimeout(now = System.currentTimeMillis())) {
                this.key.attach(this.dataChannel);
                if (this.reactorConfig.getSocksProxyAddress() == null) {
                    this.dataChannel.upgrade(this.eventHandlerFactory.createHandler(this.dataChannel, this.sessionRequest.attachment));
                    this.sessionRequest.completed(this.dataChannel);
                    this.dataChannel.handleIOEvent(8);
                } else {
                    SocksProxyProtocolHandler ioEventHandler = new SocksProxyProtocolHandler(this.dataChannel, this.sessionRequest, this.eventHandlerFactory, this.reactorConfig);
                    this.dataChannel.upgrade(ioEventHandler);
                    ioEventHandler.connected(this.dataChannel);
                }
            }
        }
    }

    @Override
    Timeout getTimeout() {
        return this.sessionRequest.timeout;
    }

    @Override
    long getLastEventTime() {
        return this.creationTimeMillis;
    }

    @Override
    void onTimeout(Timeout timeout) throws IOException {
        this.sessionRequest.failed(SocketTimeoutExceptionFactory.create(timeout));
        this.close();
    }

    @Override
    void onException(Exception cause) {
        this.sessionRequest.failed(cause);
    }

    @Override
    public void close() throws IOException {
        this.key.cancel();
        this.socketChannel.close();
    }

    @Override
    public void close(CloseMode closeMode) {
        this.key.cancel();
        Closer.closeQuietly(this.socketChannel);
    }

    public String toString() {
        return this.sessionRequest.toString();
    }
}

