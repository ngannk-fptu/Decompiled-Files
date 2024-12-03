/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.SendHandler
 *  javax.websocket.SendResult
 */
package org.apache.tomcat.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import javax.websocket.SendHandler;
import javax.websocket.SendResult;
import org.apache.tomcat.websocket.AsyncChannelWrapper;
import org.apache.tomcat.websocket.WsRemoteEndpointImplBase;

public class WsRemoteEndpointImplClient
extends WsRemoteEndpointImplBase {
    private final AsyncChannelWrapper channel;
    private final ReentrantLock lock = new ReentrantLock();

    public WsRemoteEndpointImplClient(AsyncChannelWrapper channel) {
        this.channel = channel;
    }

    @Override
    protected boolean isMasked() {
        return true;
    }

    @Override
    protected void doWrite(SendHandler handler, long blockingWriteTimeoutExpiry, ByteBuffer ... data) {
        for (ByteBuffer byteBuffer : data) {
            long timeout;
            if (blockingWriteTimeoutExpiry == -1L) {
                timeout = this.getSendTimeout();
                if (timeout < 1L) {
                    timeout = Long.MAX_VALUE;
                }
            } else {
                timeout = blockingWriteTimeoutExpiry - System.currentTimeMillis();
                if (timeout < 0L) {
                    SendResult sr = new SendResult((Throwable)new IOException(sm.getString("wsRemoteEndpoint.writeTimeout")));
                    handler.onResult(sr);
                }
            }
            try {
                this.channel.write(byteBuffer).get(timeout, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException | ExecutionException | TimeoutException e) {
                handler.onResult(new SendResult((Throwable)e));
                return;
            }
        }
        handler.onResult(SENDRESULT_OK);
    }

    @Override
    protected void doClose() {
        this.channel.close();
    }

    @Override
    protected ReentrantLock getLock() {
        return this.lock;
    }
}

