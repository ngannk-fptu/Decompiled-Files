/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.MessageHandler
 */
package org.apache.tomcat.websocket;

import javax.websocket.MessageHandler;

public interface WrappedMessageHandler {
    public long getMaxMessageSize();

    public MessageHandler getWrappedHandler();
}

