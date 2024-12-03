/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.DecodeException
 *  javax.websocket.Decoder
 *  javax.websocket.MessageHandler$Whole
 *  javax.websocket.Session
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.InstanceManager
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket.pojo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingException;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.WsSession;
import org.apache.tomcat.websocket.pojo.PojoMessageHandlerBase;

public abstract class PojoMessageHandlerWholeBase<T>
extends PojoMessageHandlerBase<T>
implements MessageHandler.Whole<T> {
    private final Log log = LogFactory.getLog(PojoMessageHandlerWholeBase.class);
    private static final StringManager sm = StringManager.getManager(PojoMessageHandlerWholeBase.class);
    protected final List<Decoder> decoders = new ArrayList<Decoder>();

    public PojoMessageHandlerWholeBase(Object pojo, Method method, Session session, Object[] params, int indexPayload, boolean convert, int indexSession, long maxMessageSize) {
        super(pojo, method, session, params, indexPayload, convert, indexSession, maxMessageSize);
    }

    protected Decoder createDecoderInstance(Class<? extends Decoder> clazz) throws ReflectiveOperationException, NamingException {
        InstanceManager instanceManager = ((WsSession)this.session).getInstanceManager();
        if (instanceManager == null) {
            return clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        return (Decoder)instanceManager.newInstance(clazz);
    }

    public final void onMessage(T message) {
        Object payload;
        if (this.params.length == 1 && this.params[0] instanceof DecodeException) {
            ((WsSession)this.session).getLocal().onError(this.session, (Throwable)((DecodeException)((Object)this.params[0])));
            return;
        }
        try {
            payload = this.decode(message);
        }
        catch (DecodeException de) {
            ((WsSession)this.session).getLocal().onError(this.session, (Throwable)de);
            return;
        }
        if (payload == null) {
            payload = this.convert ? this.convert(message) : message;
        }
        Object[] parameters = (Object[])this.params.clone();
        if (this.indexSession != -1) {
            parameters[this.indexSession] = this.session;
        }
        parameters[this.indexPayload] = payload;
        Object result = null;
        try {
            result = this.method.invoke(this.pojo, parameters);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            this.handlePojoMethodException(e);
        }
        this.processResult(result);
    }

    protected void onClose() {
        InstanceManager instanceManager = ((WsSession)this.session).getInstanceManager();
        for (Decoder decoder : this.decoders) {
            decoder.destroy();
            if (instanceManager == null) continue;
            try {
                instanceManager.destroyInstance((Object)decoder);
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                this.log.warn((Object)sm.getString("pojoMessageHandlerWholeBase.decodeDestoryFailed", new Object[]{decoder.getClass()}), (Throwable)e);
            }
        }
    }

    protected Object convert(T message) {
        return message;
    }

    protected abstract Object decode(T var1) throws DecodeException;
}

