/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 *  javax.xml.ws.WebServiceContext
 */
package com.sun.xml.ws.api.server;

import com.sun.istack.Nullable;
import com.sun.xml.ws.api.message.Packet;
import javax.xml.ws.WebServiceContext;

public interface WSWebServiceContext
extends WebServiceContext {
    @Nullable
    public Packet getRequestPacket();
}

