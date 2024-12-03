/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.io.ClientConnectionFactory
 *  org.eclipse.jetty.io.Connection
 *  org.eclipse.jetty.io.EndPoint
 *  org.eclipse.jetty.io.NegotiatingClientConnectionFactory
 *  org.eclipse.jetty.io.ssl.ALPNProcessor$Client
 *  org.eclipse.jetty.util.TypeUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.alpn.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.Executor;
import javax.net.ssl.SSLEngine;
import org.eclipse.jetty.alpn.client.ALPNClientConnection;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.NegotiatingClientConnectionFactory;
import org.eclipse.jetty.io.ssl.ALPNProcessor;
import org.eclipse.jetty.util.TypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ALPNClientConnectionFactory
extends NegotiatingClientConnectionFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ALPNClientConnectionFactory.class);
    private final List<ALPNProcessor.Client> processors = new ArrayList<ALPNProcessor.Client>();
    private final Executor executor;
    private final List<String> protocols;

    public ALPNClientConnectionFactory(Executor executor, ClientConnectionFactory connectionFactory, List<String> protocols) {
        super(connectionFactory);
        if (protocols.isEmpty()) {
            throw new IllegalArgumentException("ALPN protocol list cannot be empty");
        }
        this.executor = executor;
        this.protocols = protocols;
        IllegalStateException failure = new IllegalStateException("No Client ALPNProcessors!");
        TypeUtil.serviceProviderStream(ServiceLoader.load(ALPNProcessor.Client.class)).forEach(provider -> {
            ALPNProcessor.Client processor;
            try {
                processor = (ALPNProcessor.Client)provider.get();
            }
            catch (Throwable x) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Unable to load client processor", x);
                }
                failure.addSuppressed(x);
                return;
            }
            try {
                processor.init();
                this.processors.add(processor);
            }
            catch (Throwable x) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Could not initialize {}", (Object)processor, (Object)x);
                }
                failure.addSuppressed(x);
            }
        });
        if (LOG.isDebugEnabled()) {
            LOG.debug("protocols: {}", protocols);
            LOG.debug("processors: {}", this.processors);
        }
        if (this.processors.isEmpty()) {
            throw failure;
        }
    }

    public Connection newConnection(EndPoint endPoint, Map<String, Object> context) {
        SSLEngine engine = (SSLEngine)context.get("org.eclipse.jetty.client.ssl.engine");
        for (ALPNProcessor.Client processor : this.processors) {
            if (!processor.appliesTo(engine)) continue;
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} for {} on {}", new Object[]{processor, engine, endPoint});
            }
            ALPNClientConnection connection = new ALPNClientConnection(endPoint, this.executor, this.getClientConnectionFactory(), engine, context, this.protocols);
            processor.configure(engine, (Connection)connection);
            return this.customize((Connection)connection, context);
        }
        throw new IllegalStateException("No ALPNProcessor for " + engine);
    }
}

