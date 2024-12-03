/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.server.sei;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.server.sei.Invoker;

public interface InvokerSource<T extends Invoker> {
    @NotNull
    public T getInvoker(Packet var1);
}

