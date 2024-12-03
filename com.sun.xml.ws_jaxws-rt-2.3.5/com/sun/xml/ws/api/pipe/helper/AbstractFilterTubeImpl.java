/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.pipe.helper;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractTubeImpl;

public abstract class AbstractFilterTubeImpl
extends AbstractTubeImpl {
    protected final Tube next;

    protected AbstractFilterTubeImpl(Tube next) {
        this.next = next;
    }

    protected AbstractFilterTubeImpl(AbstractFilterTubeImpl that, TubeCloner cloner) {
        super(that, cloner);
        this.next = that.next != null ? cloner.copy(that.next) : null;
    }

    @Override
    @NotNull
    public NextAction processRequest(Packet request) {
        return this.doInvoke(this.next, request);
    }

    @Override
    @NotNull
    public NextAction processResponse(Packet response) {
        return this.doReturnWith(response);
    }

    @Override
    @NotNull
    public NextAction processException(Throwable t) {
        return this.doThrow(t);
    }

    @Override
    public void preDestroy() {
        if (this.next != null) {
            this.next.preDestroy();
        }
    }
}

