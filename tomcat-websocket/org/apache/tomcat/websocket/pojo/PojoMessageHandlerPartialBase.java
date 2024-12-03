/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.DecodeException
 *  javax.websocket.MessageHandler$Partial
 *  javax.websocket.Session
 */
package org.apache.tomcat.websocket.pojo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import javax.websocket.DecodeException;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import org.apache.tomcat.websocket.WsSession;
import org.apache.tomcat.websocket.pojo.PojoMessageHandlerBase;

public abstract class PojoMessageHandlerPartialBase<T>
extends PojoMessageHandlerBase<T>
implements MessageHandler.Partial<T> {
    private final int indexBoolean;

    public PojoMessageHandlerPartialBase(Object pojo, Method method, Session session, Object[] params, int indexPayload, boolean convert, int indexBoolean, int indexSession, long maxMessageSize) {
        super(pojo, method, session, params, indexPayload, convert, indexSession, maxMessageSize);
        this.indexBoolean = indexBoolean;
    }

    public final void onMessage(T message, boolean last) {
        if (this.params.length == 1 && this.params[0] instanceof DecodeException) {
            ((WsSession)this.session).getLocal().onError(this.session, (Throwable)((DecodeException)this.params[0]));
            return;
        }
        Object[] parameters = (Object[])this.params.clone();
        if (this.indexBoolean != -1) {
            parameters[this.indexBoolean] = last;
        }
        if (this.indexSession != -1) {
            parameters[this.indexSession] = this.session;
        }
        parameters[this.indexPayload] = this.convert ? (Object)((ByteBuffer)message).array() : message;
        Object result = null;
        try {
            result = this.method.invoke(this.pojo, parameters);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            this.handlePojoMethodException(e);
        }
        this.processResult(result);
    }
}

