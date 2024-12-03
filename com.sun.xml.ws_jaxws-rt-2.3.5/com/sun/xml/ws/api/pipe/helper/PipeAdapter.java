/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.pipe.helper;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.Pipe;
import com.sun.xml.ws.api.pipe.PipeCloner;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractPipeImpl;
import com.sun.xml.ws.api.pipe.helper.AbstractTubeImpl;

public class PipeAdapter
extends AbstractTubeImpl {
    private final Pipe next;

    public static Tube adapt(Pipe p) {
        if (p instanceof Tube) {
            return (Tube)((Object)p);
        }
        return new PipeAdapter(p);
    }

    public static Pipe adapt(Tube p) {
        if (p instanceof Pipe) {
            return (Pipe)((Object)p);
        }
        class TubeAdapter
        extends AbstractPipeImpl {
            private final Tube t;

            public TubeAdapter(Tube t) {
                this.t = t;
            }

            private TubeAdapter(TubeAdapter that, PipeCloner cloner) {
                super(that, cloner);
                this.t = cloner.copy(that.t);
            }

            @Override
            public Packet process(Packet request) {
                return Fiber.current().runSync(this.t, request);
            }

            @Override
            public Pipe copy(PipeCloner cloner) {
                return new TubeAdapter(this, cloner);
            }
        }
        return new TubeAdapter(p);
    }

    private PipeAdapter(Pipe next) {
        this.next = next;
    }

    private PipeAdapter(PipeAdapter that, TubeCloner cloner) {
        super(that, cloner);
        this.next = ((PipeCloner)cloner).copy(that.next);
    }

    @Override
    @NotNull
    public NextAction processRequest(@NotNull Packet p) {
        return this.doReturnWith(this.next.process(p));
    }

    @Override
    @NotNull
    public NextAction processResponse(@NotNull Packet p) {
        throw new IllegalStateException();
    }

    @Override
    @NotNull
    public NextAction processException(@NotNull Throwable t) {
        throw new IllegalStateException();
    }

    @Override
    public void preDestroy() {
        this.next.preDestroy();
    }

    @Override
    public PipeAdapter copy(TubeCloner cloner) {
        return new PipeAdapter(this, cloner);
    }

    public String toString() {
        return super.toString() + "[" + this.next.toString() + "]";
    }
}

