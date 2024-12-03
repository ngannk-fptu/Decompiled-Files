/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.server.sei;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.ws.server.sei.Invoker;
import com.sun.xml.ws.server.sei.InvokerSource;

public abstract class InvokerTube<T extends Invoker>
extends AbstractTubeImpl
implements InvokerSource<T> {
    protected final T invoker;

    protected InvokerTube(T invoker) {
        this.invoker = invoker;
    }

    protected InvokerTube(InvokerTube<T> that, TubeCloner cloner) {
        cloner.add(that, this);
        this.invoker = that.invoker;
    }

    @Override
    @NotNull
    public T getInvoker(Packet request) {
        return this.invoker;
    }
}

