/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.Session
 */
package org.apache.tomcat.websocket.pojo;

import java.lang.reflect.Method;
import javax.websocket.Session;
import org.apache.tomcat.websocket.pojo.PojoMessageHandlerPartialBase;

public class PojoMessageHandlerPartialText
extends PojoMessageHandlerPartialBase<String> {
    public PojoMessageHandlerPartialText(Object pojo, Method method, Session session, Object[] params, int indexPayload, boolean convert, int indexBoolean, int indexSession, long maxMessageSize) {
        super(pojo, method, session, params, indexPayload, convert, indexBoolean, indexSession, maxMessageSize);
    }
}

