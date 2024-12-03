/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.client.impl.ClientEngine;
import com.hazelcast.config.MemcacheProtocolConfig;
import com.hazelcast.config.RestApiConfig;
import com.hazelcast.config.SSLConfig;
import com.hazelcast.config.SymmetricEncryptionConfig;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.internal.ascii.TextCommandService;
import com.hazelcast.internal.networking.InboundHandler;
import com.hazelcast.internal.networking.OutboundHandler;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.logging.LoggingService;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.MemberSocketInterceptor;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.spi.properties.HazelcastProperties;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;

@PrivateApi
public interface IOService {
    public static final int KILO_BYTE = 1024;

    public boolean isActive();

    public HazelcastProperties properties();

    public String getHazelcastName();

    public LoggingService getLoggingService();

    public Address getThisAddress();

    public Map<EndpointQualifier, Address> getThisAddresses();

    public void onFatalError(Exception var1);

    public SymmetricEncryptionConfig getSymmetricEncryptionConfig(EndpointQualifier var1);

    public RestApiConfig getRestApiConfig();

    public MemcacheProtocolConfig getMemcacheProtocolConfig();

    public SSLConfig getSSLConfig(EndpointQualifier var1);

    public ClientEngine getClientEngine();

    public TextCommandService getTextCommandService();

    public void removeEndpoint(Address var1);

    public void onSuccessfulConnection(Address var1);

    public void onFailedConnection(Address var1);

    public void shouldConnectTo(Address var1);

    public boolean isSocketBind();

    public boolean isSocketBindAny();

    public void interceptSocket(EndpointQualifier var1, Socket var2, boolean var3) throws IOException;

    public boolean isSocketInterceptorEnabled(EndpointQualifier var1);

    public int getSocketConnectTimeoutSeconds(EndpointQualifier var1);

    public long getConnectionMonitorInterval();

    public int getConnectionMonitorMaxFaults();

    public void onDisconnect(Address var1, Throwable var2);

    public void executeAsync(Runnable var1);

    public EventService getEventService();

    public Collection<Integer> getOutboundPorts(EndpointQualifier var1);

    public InternalSerializationService getSerializationService();

    public MemberSocketInterceptor getSocketInterceptor(EndpointQualifier var1);

    public InboundHandler[] createInboundHandlers(EndpointQualifier var1, TcpIpConnection var2);

    public OutboundHandler[] createOutboundHandlers(EndpointQualifier var1, TcpIpConnection var2);
}

