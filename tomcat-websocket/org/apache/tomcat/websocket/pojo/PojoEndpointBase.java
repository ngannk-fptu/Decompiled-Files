/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.CloseReason
 *  javax.websocket.Endpoint
 *  javax.websocket.EndpointConfig
 *  javax.websocket.MessageHandler
 *  javax.websocket.Session
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket.pojo;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.pojo.PojoMessageHandlerWholeBase;
import org.apache.tomcat.websocket.pojo.PojoMethodMapping;

public abstract class PojoEndpointBase
extends Endpoint {
    private final Log log = LogFactory.getLog(PojoEndpointBase.class);
    private static final StringManager sm = StringManager.getManager(PojoEndpointBase.class);
    private Object pojo;
    private final Map<String, String> pathParameters;
    private PojoMethodMapping methodMapping;

    protected PojoEndpointBase(Map<String, String> pathParameters) {
        this.pathParameters = pathParameters;
    }

    protected final void doOnOpen(Session session, EndpointConfig config) {
        PojoMethodMapping methodMapping = this.getMethodMapping();
        Object pojo = this.getPojo();
        for (MessageHandler mh : methodMapping.getMessageHandlers(pojo, this.pathParameters, session, config)) {
            session.addMessageHandler(mh);
        }
        if (methodMapping.getOnOpen() != null) {
            try {
                methodMapping.getOnOpen().invoke(pojo, methodMapping.getOnOpenArgs(this.pathParameters, session, config));
            }
            catch (IllegalAccessException e) {
                this.log.error((Object)sm.getString("pojoEndpointBase.onOpenFail", new Object[]{pojo.getClass().getName()}), (Throwable)e);
                this.handleOnOpenOrCloseError(session, e);
            }
            catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                this.handleOnOpenOrCloseError(session, cause);
            }
            catch (Throwable t) {
                this.handleOnOpenOrCloseError(session, t);
            }
        }
    }

    private void handleOnOpenOrCloseError(Session session, Throwable t) {
        ExceptionUtils.handleThrowable((Throwable)t);
        this.onError(session, t);
        try {
            session.close();
        }
        catch (IOException ioe) {
            this.log.warn((Object)sm.getString("pojoEndpointBase.closeSessionFail"), (Throwable)ioe);
        }
    }

    public final void onClose(Session session, CloseReason closeReason) {
        if (this.methodMapping.getOnClose() != null) {
            try {
                this.methodMapping.getOnClose().invoke(this.pojo, this.methodMapping.getOnCloseArgs(this.pathParameters, session, closeReason));
            }
            catch (Throwable t) {
                this.log.error((Object)sm.getString("pojoEndpointBase.onCloseFail", new Object[]{this.pojo.getClass().getName()}), t);
                this.handleOnOpenOrCloseError(session, t);
            }
        }
        Set messageHandlers = session.getMessageHandlers();
        for (MessageHandler messageHandler : messageHandlers) {
            if (!(messageHandler instanceof PojoMessageHandlerWholeBase)) continue;
            ((PojoMessageHandlerWholeBase)messageHandler).onClose();
        }
    }

    public final void onError(Session session, Throwable throwable) {
        if (this.methodMapping.getOnError() == null) {
            this.log.error((Object)sm.getString("pojoEndpointBase.onError", new Object[]{this.pojo.getClass().getName()}), throwable);
        } else {
            try {
                this.methodMapping.getOnError().invoke(this.pojo, this.methodMapping.getOnErrorArgs(this.pathParameters, session, throwable));
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                this.log.error((Object)sm.getString("pojoEndpointBase.onErrorFail", new Object[]{this.pojo.getClass().getName()}), t);
            }
        }
    }

    protected Object getPojo() {
        return this.pojo;
    }

    protected void setPojo(Object pojo) {
        this.pojo = pojo;
    }

    protected PojoMethodMapping getMethodMapping() {
        return this.methodMapping;
    }

    protected void setMethodMapping(PojoMethodMapping methodMapping) {
        this.methodMapping = methodMapping;
    }
}

