/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.pipe.helper;

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Pipe;
import com.sun.xml.ws.api.pipe.PipeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractPipeImpl;

public abstract class AbstractFilterPipeImpl
extends AbstractPipeImpl {
    protected final Pipe next;

    protected AbstractFilterPipeImpl(Pipe next) {
        this.next = next;
        assert (next != null);
    }

    protected AbstractFilterPipeImpl(AbstractFilterPipeImpl that, PipeCloner cloner) {
        super(that, cloner);
        this.next = cloner.copy(that.next);
        assert (this.next != null);
    }

    @Override
    public Packet process(Packet packet) {
        return this.next.process(packet);
    }

    @Override
    public void preDestroy() {
        this.next.preDestroy();
    }
}

