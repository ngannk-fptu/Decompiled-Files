/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.server.provider;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.ws.api.server.AsyncProviderCallback;
import com.sun.xml.ws.api.server.Invoker;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.server.AbstractWebServiceContext;
import com.sun.xml.ws.server.provider.ProviderArgumentsBuilder;
import com.sun.xml.ws.server.provider.ProviderInvokerTube;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsyncProviderInvokerTube<T>
extends ProviderInvokerTube<T> {
    private static final Logger LOGGER = Logger.getLogger("com.sun.xml.ws.server.AsyncProviderInvokerTube");

    public AsyncProviderInvokerTube(Invoker invoker, ProviderArgumentsBuilder<T> argsBuilder) {
        super(invoker, argsBuilder);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @NotNull
    public NextAction processRequest(@NotNull Packet request) {
        Object param = this.argsBuilder.getParameter(request);
        NoSuspendResumer resumer = new NoSuspendResumer();
        AsyncProviderCallbackImpl callback = new AsyncProviderCallbackImpl(request, resumer);
        AsyncWebServiceContext ctxt = new AsyncWebServiceContext(this.getEndpoint(), request);
        LOGGER.fine("Invoking AsyncProvider Endpoint");
        try {
            this.getInvoker(request).invokeAsyncProvider(request, param, callback, ctxt);
        }
        catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return this.doThrow(e);
        }
        AsyncProviderCallbackImpl asyncProviderCallbackImpl = callback;
        synchronized (asyncProviderCallbackImpl) {
            if (resumer.response != null) {
                ThrowableContainerPropertySet tc = resumer.response.getSatellite(ThrowableContainerPropertySet.class);
                Throwable t = tc != null ? tc.getThrowable() : null;
                return t != null ? this.doThrow(resumer.response, t) : this.doReturnWith(resumer.response);
            }
            callback.resumer = new FiberResumer();
            return this.doSuspend();
        }
    }

    @Override
    @NotNull
    public NextAction processResponse(@NotNull Packet response) {
        return this.doReturnWith(response);
    }

    @Override
    @NotNull
    public NextAction processException(@NotNull Throwable t) {
        return this.doThrow(t);
    }

    public class AsyncWebServiceContext
    extends AbstractWebServiceContext {
        final Packet packet;

        public AsyncWebServiceContext(WSEndpoint endpoint, Packet packet) {
            super(endpoint);
            this.packet = packet;
        }

        @Override
        @NotNull
        public Packet getRequestPacket() {
            return this.packet;
        }
    }

    public class AsyncProviderCallbackImpl
    implements AsyncProviderCallback<T> {
        private final Packet request;
        private Resumer resumer;

        public AsyncProviderCallbackImpl(Packet request, Resumer resumer) {
            this.request = request;
            this.resumer = resumer;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void send(@Nullable T param) {
            if (param == null && this.request.transportBackChannel != null) {
                this.request.transportBackChannel.close();
            }
            Packet packet = AsyncProviderInvokerTube.this.argsBuilder.getResponse(this.request, param, AsyncProviderInvokerTube.this.getEndpoint().getPort(), AsyncProviderInvokerTube.this.getEndpoint().getBinding());
            AsyncProviderCallbackImpl asyncProviderCallbackImpl = this;
            synchronized (asyncProviderCallbackImpl) {
                this.resumer.onResume(packet);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void sendError(@NotNull Throwable t) {
            Exception e = t instanceof Exception ? (Exception)t : new RuntimeException(t);
            Packet packet = AsyncProviderInvokerTube.this.argsBuilder.getResponse(this.request, e, AsyncProviderInvokerTube.this.getEndpoint().getPort(), AsyncProviderInvokerTube.this.getEndpoint().getBinding());
            AsyncProviderCallbackImpl asyncProviderCallbackImpl = this;
            synchronized (asyncProviderCallbackImpl) {
                this.resumer.onResume(packet);
            }
        }
    }

    private class NoSuspendResumer
    implements Resumer {
        protected Packet response = null;

        private NoSuspendResumer() {
        }

        @Override
        public void onResume(Packet response) {
            this.response = response;
        }
    }

    public class FiberResumer
    implements Resumer {
        private final Fiber fiber = Fiber.current();

        @Override
        public void onResume(Packet response) {
            ThrowableContainerPropertySet tc = response.getSatellite(ThrowableContainerPropertySet.class);
            Throwable t = tc != null ? tc.getThrowable() : null;
            this.fiber.resume(t, response);
        }
    }

    private static interface Resumer {
        public void onResume(Packet var1);
    }
}

