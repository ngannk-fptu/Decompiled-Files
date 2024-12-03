/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.ws.WebServiceContext
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.server;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.ws.api.server.AsyncProviderCallback;
import com.sun.xml.ws.api.server.Invoker;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.resources.ServerMessages;
import com.sun.xml.ws.server.AbstractWebServiceContext;
import com.sun.xml.ws.server.EndpointAwareTube;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;

public abstract class InvokerTube<T>
extends com.sun.xml.ws.server.sei.InvokerTube<Invoker>
implements EndpointAwareTube {
    private WSEndpoint endpoint;
    private static final ThreadLocal<Packet> packets = new ThreadLocal();
    private final Invoker wrapper = new Invoker(){

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Object invoke(Packet p, Method m, Object ... args) throws InvocationTargetException, IllegalAccessException {
            Packet old = this.set(p);
            try {
                Object object = ((Invoker)InvokerTube.this.invoker).invoke(p, m, args);
                return object;
            }
            finally {
                this.set(old);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public <T> T invokeProvider(Packet p, T arg) throws IllegalAccessException, InvocationTargetException {
            Packet old = this.set(p);
            try {
                T t = ((Invoker)InvokerTube.this.invoker).invokeProvider(p, arg);
                return t;
            }
            finally {
                this.set(old);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public <T> void invokeAsyncProvider(Packet p, T arg, AsyncProviderCallback cbak, WebServiceContext ctxt) throws IllegalAccessException, InvocationTargetException {
            Packet old = this.set(p);
            try {
                ((Invoker)InvokerTube.this.invoker).invokeAsyncProvider(p, arg, cbak, ctxt);
            }
            finally {
                this.set(old);
            }
        }

        private Packet set(Packet p) {
            Packet old = (Packet)packets.get();
            packets.set(p);
            return old;
        }
    };

    protected InvokerTube(Invoker invoker) {
        super(invoker);
    }

    public void setEndpoint(WSEndpoint endpoint) {
        this.endpoint = endpoint;
        AbstractWebServiceContext webServiceContext = new AbstractWebServiceContext(endpoint){

            @Override
            @Nullable
            public Packet getRequestPacket() {
                Packet p = (Packet)packets.get();
                return p;
            }
        };
        ((Invoker)this.invoker).start(webServiceContext, endpoint);
    }

    protected WSEndpoint getEndpoint() {
        return this.endpoint;
    }

    @Override
    @NotNull
    public final Invoker getInvoker(Packet request) {
        return this.wrapper;
    }

    @Override
    public final AbstractTubeImpl copy(TubeCloner cloner) {
        cloner.add(this, this);
        return this;
    }

    @Override
    public void preDestroy() {
        ((Invoker)this.invoker).dispose();
    }

    @NotNull
    public static Packet getCurrentPacket() {
        Packet packet = packets.get();
        if (packet == null) {
            throw new WebServiceException(ServerMessages.NO_CURRENT_PACKET());
        }
        return packet;
    }
}

