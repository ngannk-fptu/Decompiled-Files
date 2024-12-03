/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.PongMessage
 *  javax.websocket.Session
 */
package org.apache.tomcat.websocket.pojo;

import java.lang.reflect.Method;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import org.apache.tomcat.websocket.pojo.PojoMessageHandlerWholeBase;

public class PojoMessageHandlerWholePong
extends PojoMessageHandlerWholeBase<PongMessage> {
    public PojoMessageHandlerWholePong(Object pojo, Method method, Session session, Object[] params, int indexPayload, boolean convert, int indexSession) {
        super(pojo, method, session, params, indexPayload, convert, indexSession, -1L);
    }

    @Override
    protected Object decode(PongMessage message) {
        return null;
    }

    @Override
    protected void onClose() {
    }
}

