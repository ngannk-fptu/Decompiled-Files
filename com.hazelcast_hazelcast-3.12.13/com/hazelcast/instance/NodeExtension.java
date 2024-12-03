/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.hotrestart.HotRestartService;
import com.hazelcast.hotrestart.InternalHotRestartService;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.internal.ascii.TextCommandService;
import com.hazelcast.internal.cluster.impl.JoinMessage;
import com.hazelcast.internal.diagnostics.Diagnostics;
import com.hazelcast.internal.dynamicconfig.DynamicConfigListener;
import com.hazelcast.internal.jmx.ManagementService;
import com.hazelcast.internal.management.ManagementCenterConnectionFactory;
import com.hazelcast.internal.management.TimedMemberStateFactory;
import com.hazelcast.internal.networking.ChannelInitializerProvider;
import com.hazelcast.internal.networking.InboundHandler;
import com.hazelcast.internal.networking.OutboundHandler;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.memory.MemoryStats;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.IOService;
import com.hazelcast.nio.MemberSocketInterceptor;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.security.SecurityContext;
import com.hazelcast.security.SecurityService;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.util.ByteArrayProcessor;
import com.hazelcast.version.Version;
import java.util.Map;

@PrivateApi
public interface NodeExtension {
    public void beforeStart();

    public void printNodeInfo();

    public void beforeJoin();

    public void afterStart();

    public boolean isStartCompleted();

    public void beforeShutdown();

    public void shutdown();

    public InternalSerializationService createSerializationService();

    public SecurityService getSecurityService();

    public SecurityContext getSecurityContext();

    public <T> T createService(Class<T> var1);

    public Map<String, Object> createExtensionServices();

    public MemberSocketInterceptor getSocketInterceptor(EndpointQualifier var1);

    public InboundHandler[] createInboundHandlers(EndpointQualifier var1, TcpIpConnection var2, IOService var3);

    public OutboundHandler[] createOutboundHandlers(EndpointQualifier var1, TcpIpConnection var2, IOService var3);

    public ChannelInitializerProvider createChannelInitializerProvider(IOService var1);

    public void onThreadStart(Thread var1);

    public void onThreadStop(Thread var1);

    public MemoryStats getMemoryStats();

    public void validateJoinRequest(JoinMessage var1);

    public void onInitialClusterState(ClusterState var1);

    public void beforeClusterStateChange(ClusterState var1, ClusterState var2, boolean var3);

    public void onClusterStateChange(ClusterState var1, boolean var2);

    public void afterClusterStateChange(ClusterState var1, ClusterState var2, boolean var3);

    public void onPartitionStateChange();

    public void onMemberListChange();

    public void onClusterVersionChange(Version var1);

    public boolean isNodeVersionCompatibleWith(Version var1);

    public boolean registerListener(Object var1);

    public HotRestartService getHotRestartService();

    public InternalHotRestartService getInternalHotRestartService();

    public String createMemberUuid(Address var1);

    public TimedMemberStateFactory createTimedMemberStateFactory(HazelcastInstanceImpl var1);

    public ManagementCenterConnectionFactory getManagementCenterConnectionFactory();

    public ManagementService createJMXManagementService(HazelcastInstanceImpl var1);

    public TextCommandService createTextCommandService();

    public ByteArrayProcessor createMulticastInputProcessor(IOService var1);

    public ByteArrayProcessor createMulticastOutputProcessor(IOService var1);

    public DynamicConfigListener createDynamicConfigListener();

    public void registerPlugins(Diagnostics var1);

    public void sendPhoneHome();

    public void scheduleClusterVersionAutoUpgrade();

    public boolean isClientFailoverSupported();
}

