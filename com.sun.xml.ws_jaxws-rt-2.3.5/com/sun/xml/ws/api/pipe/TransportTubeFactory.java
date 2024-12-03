/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.api.pipe;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.pipe.ClientPipeAssemblerContext;
import com.sun.xml.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.Pipe;
import com.sun.xml.ws.api.pipe.TransportPipeFactory;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.ws.transport.http.client.HttpTransportPipe;
import com.sun.xml.ws.util.ServiceFinder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.WebServiceException;

public abstract class TransportTubeFactory {
    private static final TransportTubeFactory DEFAULT = new DefaultTransportTubeFactory();
    private static final Logger logger = Logger.getLogger(TransportTubeFactory.class.getName());

    public abstract Tube doCreate(@NotNull ClientTubeAssemblerContext var1);

    public static Tube create(@Nullable ClassLoader classLoader, @NotNull ClientTubeAssemblerContext context) {
        for (TransportTubeFactory factory : ServiceFinder.find(TransportTubeFactory.class, classLoader, context.getContainer())) {
            Tube tube = factory.doCreate(context);
            if (tube == null) continue;
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "{0} successfully created {1}", new Object[]{factory.getClass(), tube});
            }
            return tube;
        }
        ClientPipeAssemblerContext ctxt = new ClientPipeAssemblerContext(context.getAddress(), context.getWsdlModel(), context.getService(), context.getBinding(), context.getContainer());
        ctxt.setCodec(context.getCodec());
        for (TransportPipeFactory factory : ServiceFinder.find(TransportPipeFactory.class, classLoader)) {
            Pipe pipe = factory.doCreate(ctxt);
            if (pipe == null) continue;
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "{0} successfully created {1}", new Object[]{factory.getClass(), pipe});
            }
            return PipeAdapter.adapt(pipe);
        }
        return DEFAULT.createDefault(ctxt);
    }

    protected Tube createDefault(ClientTubeAssemblerContext context) {
        String scheme = context.getAddress().getURI().getScheme();
        if (scheme != null && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
            return this.createHttpTransport(context);
        }
        throw new WebServiceException("Unsupported endpoint address: " + context.getAddress());
    }

    protected Tube createHttpTransport(ClientTubeAssemblerContext context) {
        return new HttpTransportPipe(context.getCodec(), context.getBinding());
    }

    private static class DefaultTransportTubeFactory
    extends TransportTubeFactory {
        private DefaultTransportTubeFactory() {
        }

        @Override
        public Tube doCreate(ClientTubeAssemblerContext context) {
            return this.createDefault(context);
        }
    }
}

