/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.io.ssl.SslHandshakeListener
 *  org.eclipse.jetty.io.ssl.SslHandshakeListener$Event
 *  org.eclipse.jetty.server.Connector
 *  org.eclipse.jetty.server.Server
 */
package io.micrometer.core.instrument.binder.jetty;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import javax.net.ssl.SSLSession;
import org.eclipse.jetty.io.ssl.SslHandshakeListener;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;

public class JettySslHandshakeMetrics
implements SslHandshakeListener {
    private static final String METER_NAME = "jetty.ssl.handshakes";
    private static final String DESCRIPTION = "SSL/TLS handshakes";
    private static final String TAG_RESULT = "result";
    private static final String TAG_PROTOCOL = "protocol";
    private static final String TAG_CIPHER_SUITE = "ciphersuite";
    private static final String TAG_VALUE_UNKNOWN = "unknown";
    private final MeterRegistry registry;
    private final Iterable<Tag> tags;
    private final Counter handshakesFailed;

    public JettySslHandshakeMetrics(MeterRegistry registry) {
        this(registry, Tags.empty());
    }

    public JettySslHandshakeMetrics(MeterRegistry registry, Iterable<Tag> tags) {
        this.registry = registry;
        this.tags = tags;
        this.handshakesFailed = Counter.builder(METER_NAME).baseUnit("events").description(DESCRIPTION).tag(TAG_RESULT, "failed").tag(TAG_PROTOCOL, TAG_VALUE_UNKNOWN).tag(TAG_CIPHER_SUITE, TAG_VALUE_UNKNOWN).tags(tags).register(registry);
    }

    public JettySslHandshakeMetrics(MeterRegistry registry, Connector connector) {
        this(registry, connector, Tags.empty());
    }

    public JettySslHandshakeMetrics(MeterRegistry registry, Connector connector, Iterable<Tag> tags) {
        this(registry, JettySslHandshakeMetrics.getConnectorNameTag(connector).and(tags));
    }

    private static Tags getConnectorNameTag(Connector connector) {
        String name = connector.getName();
        return Tags.of("connector.name", name != null ? name : "unnamed");
    }

    public void handshakeSucceeded(SslHandshakeListener.Event event) {
        SSLSession session = event.getSSLEngine().getSession();
        Counter.builder(METER_NAME).baseUnit("events").description(DESCRIPTION).tag(TAG_RESULT, "succeeded").tag(TAG_PROTOCOL, session.getProtocol()).tag(TAG_CIPHER_SUITE, session.getCipherSuite()).tags(this.tags).register(this.registry).increment();
    }

    public void handshakeFailed(SslHandshakeListener.Event event, Throwable failure) {
        this.handshakesFailed.increment();
    }

    public static void addToAllConnectors(Server server, MeterRegistry registry, Iterable<Tag> tags) {
        for (Connector connector : server.getConnectors()) {
            if (connector == null) continue;
            connector.addBean((Object)new JettySslHandshakeMetrics(registry, connector, tags));
        }
    }

    public static void addToAllConnectors(Server server, MeterRegistry registry) {
        JettySslHandshakeMetrics.addToAllConnectors(server, registry, Tags.empty());
    }
}

