/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.EncodeException
 *  javax.websocket.MessageHandler
 *  javax.websocket.RemoteEndpoint$Basic
 *  javax.websocket.Session
 *  org.apache.tomcat.util.ExceptionUtils
 */
package org.apache.tomcat.websocket.pojo;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import javax.websocket.EncodeException;
import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.websocket.WrappedMessageHandler;

public abstract class PojoMessageHandlerBase<T>
implements WrappedMessageHandler {
    protected final Object pojo;
    protected final Method method;
    protected final Session session;
    protected final Object[] params;
    protected final int indexPayload;
    protected final boolean convert;
    protected final int indexSession;
    protected final long maxMessageSize;

    public PojoMessageHandlerBase(Object pojo, Method method, Session session, Object[] params, int indexPayload, boolean convert, int indexSession, long maxMessageSize) {
        this.pojo = pojo;
        this.method = method;
        try {
            this.method.setAccessible(true);
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.session = session;
        this.params = params;
        this.indexPayload = indexPayload;
        this.convert = convert;
        this.indexSession = indexSession;
        this.maxMessageSize = maxMessageSize;
    }

    protected final void processResult(Object result) {
        if (result == null) {
            return;
        }
        RemoteEndpoint.Basic remoteEndpoint = this.session.getBasicRemote();
        try {
            if (result instanceof String) {
                remoteEndpoint.sendText((String)result);
            } else if (result instanceof ByteBuffer) {
                remoteEndpoint.sendBinary((ByteBuffer)result);
            } else if (result instanceof byte[]) {
                remoteEndpoint.sendBinary(ByteBuffer.wrap((byte[])result));
            } else {
                remoteEndpoint.sendObject(result);
            }
        }
        catch (IOException | EncodeException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    @Override
    public final MessageHandler getWrappedHandler() {
        if (this.pojo instanceof MessageHandler) {
            return (MessageHandler)this.pojo;
        }
        return null;
    }

    @Override
    public final long getMaxMessageSize() {
        return this.maxMessageSize;
    }

    protected final void handlePojoMethodException(Throwable t) {
        t = ExceptionUtils.unwrapInvocationTargetException((Throwable)t);
        ExceptionUtils.handleThrowable((Throwable)t);
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        throw new RuntimeException(t.getMessage(), t);
    }
}

