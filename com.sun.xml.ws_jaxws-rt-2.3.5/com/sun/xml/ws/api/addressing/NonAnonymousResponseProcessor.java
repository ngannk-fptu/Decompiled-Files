/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.addressing;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.WSService;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.xml.ws.api.pipe.TransportTubeFactory;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.binding.BindingImpl;

public class NonAnonymousResponseProcessor {
    private static final NonAnonymousResponseProcessor DEFAULT = new NonAnonymousResponseProcessor();

    public static NonAnonymousResponseProcessor getDefault() {
        return DEFAULT;
    }

    protected NonAnonymousResponseProcessor() {
    }

    public Packet process(Packet packet) {
        Fiber.CompletionCallback currentFiberCallback;
        Fiber.CompletionCallback fiberCallback = null;
        Fiber currentFiber = Fiber.getCurrentIfSet();
        if (currentFiber != null && (currentFiberCallback = currentFiber.getCompletionCallback()) != null) {
            fiberCallback = new Fiber.CompletionCallback(){

                @Override
                public void onCompletion(@NotNull Packet response) {
                    currentFiberCallback.onCompletion(response);
                }

                @Override
                public void onCompletion(@NotNull Throwable error) {
                    currentFiberCallback.onCompletion(error);
                }
            };
            currentFiber.setCompletionCallback(null);
        }
        WSEndpoint endpoint = packet.endpoint;
        WSBinding binding = endpoint.getBinding();
        Tube transport = TransportTubeFactory.create(Thread.currentThread().getContextClassLoader(), new ClientTubeAssemblerContext(packet.endpointAddress, endpoint.getPort(), (WSService)null, binding, endpoint.getContainer(), ((BindingImpl)binding).createCodec(), null, null));
        Fiber fiber = endpoint.getEngine().createFiber();
        fiber.start(transport, packet, fiberCallback);
        Packet copy = packet.copy(false);
        copy.endpointAddress = null;
        return copy;
    }
}

