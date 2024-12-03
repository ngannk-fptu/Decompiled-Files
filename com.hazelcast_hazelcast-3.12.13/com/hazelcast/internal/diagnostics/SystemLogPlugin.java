/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipAdapter;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MigrationEvent;
import com.hazelcast.core.MigrationListener;
import com.hazelcast.instance.NodeExtension;
import com.hazelcast.internal.cluster.ClusterVersionListener;
import com.hazelcast.internal.diagnostics.DiagnosticsLogWriter;
import com.hazelcast.internal.diagnostics.DiagnosticsPlugin;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.ConnectionListenable;
import com.hazelcast.nio.ConnectionListener;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.properties.HazelcastProperty;
import com.hazelcast.version.Version;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class SystemLogPlugin
extends DiagnosticsPlugin {
    public static final HazelcastProperty ENABLED = new HazelcastProperty("hazelcast.diagnostics.systemlog.enabled", "true");
    public static final HazelcastProperty LOG_PARTITIONS = new HazelcastProperty("hazelcast.diagnostics.systemlog.partitions", "false");
    private static final long PERIOD_MILLIS = TimeUnit.SECONDS.toMillis(1L);
    private final Queue<Object> logQueue = new ConcurrentLinkedQueue<Object>();
    private final ConnectionListenable connectionObservable;
    private final HazelcastInstance hazelcastInstance;
    private final Address thisAddress;
    private final boolean logPartitions;
    private final boolean enabled;
    private final NodeExtension nodeExtension;

    public SystemLogPlugin(NodeEngineImpl nodeEngine) {
        this(nodeEngine.getProperties(), nodeEngine.getNode().networkingService.getAggregateEndpointManager(), nodeEngine.getHazelcastInstance(), nodeEngine.getLogger(SystemLogPlugin.class), nodeEngine.getNode().getNodeExtension());
    }

    public SystemLogPlugin(HazelcastProperties properties, ConnectionListenable connectionObservable, HazelcastInstance hazelcastInstance, ILogger logger) {
        this(properties, connectionObservable, hazelcastInstance, logger, null);
    }

    public SystemLogPlugin(HazelcastProperties properties, ConnectionListenable connectionObservable, HazelcastInstance hazelcastInstance, ILogger logger, NodeExtension nodeExtension) {
        super(logger);
        this.connectionObservable = connectionObservable;
        this.hazelcastInstance = hazelcastInstance;
        this.thisAddress = this.getThisAddress(hazelcastInstance);
        this.logPartitions = properties.getBoolean(LOG_PARTITIONS);
        this.enabled = properties.getBoolean(ENABLED);
        this.nodeExtension = nodeExtension;
    }

    private Address getThisAddress(HazelcastInstance hazelcastInstance) {
        try {
            return hazelcastInstance.getCluster().getLocalMember().getAddress();
        }
        catch (UnsupportedOperationException e) {
            return null;
        }
    }

    @Override
    public long getPeriodMillis() {
        if (!this.enabled) {
            return 0L;
        }
        return PERIOD_MILLIS;
    }

    @Override
    public void onStart() {
        this.logger.info("Plugin:active: logPartitions:" + this.logPartitions);
        this.connectionObservable.addConnectionListener(new ConnectionListenerImpl());
        this.hazelcastInstance.getCluster().addMembershipListener(new MembershipListenerImpl());
        if (this.logPartitions) {
            this.hazelcastInstance.getPartitionService().addMigrationListener(new MigrationListenerImpl());
        }
        this.hazelcastInstance.getLifecycleService().addLifecycleListener(new LifecycleListenerImpl());
        if (this.nodeExtension != null) {
            this.nodeExtension.registerListener(new ClusterVersionListenerImpl());
        }
    }

    @Override
    public void run(DiagnosticsLogWriter writer) {
        Object item;
        while ((item = this.logQueue.poll()) != null) {
            if (item instanceof LifecycleEvent) {
                this.render(writer, (LifecycleEvent)item);
                continue;
            }
            if (item instanceof MembershipEvent) {
                this.render(writer, (MembershipEvent)item);
                continue;
            }
            if (item instanceof MigrationEvent) {
                this.render(writer, (MigrationEvent)item);
                continue;
            }
            if (item instanceof ConnectionEvent) {
                ConnectionEvent event = (ConnectionEvent)item;
                this.render(writer, event);
                continue;
            }
            if (!(item instanceof Version)) continue;
            this.render(writer, (Version)item);
        }
        return;
    }

    private void render(DiagnosticsLogWriter writer, LifecycleEvent event) {
        writer.startSection("Lifecycle");
        writer.writeEntry(event.getState().name());
        writer.endSection();
    }

    private void render(DiagnosticsLogWriter writer, MembershipEvent event) {
        switch (event.getEventType()) {
            case 1: {
                writer.startSection("MemberAdded");
                break;
            }
            case 2: {
                writer.startSection("MemberRemoved");
                break;
            }
            default: {
                return;
            }
        }
        writer.writeKeyValueEntry("member", event.getMember().getAddress().toString());
        writer.startSection("Members");
        Set<Member> members = event.getMembers();
        if (members != null) {
            boolean first = true;
            for (Member member : members) {
                if (member.getAddress().equals(this.thisAddress)) {
                    if (first) {
                        writer.writeEntry(member.getAddress().toString() + ":this:master");
                    } else {
                        writer.writeEntry(member.getAddress().toString() + ":this");
                    }
                } else if (first) {
                    writer.writeEntry(member.getAddress().toString() + ":master");
                } else {
                    writer.writeEntry(member.getAddress().toString());
                }
                first = false;
            }
        }
        writer.endSection();
        writer.endSection();
    }

    private void render(DiagnosticsLogWriter writer, MigrationEvent event) {
        switch (event.getStatus()) {
            case STARTED: {
                writer.startSection("MigrationStarted");
                break;
            }
            case COMPLETED: {
                writer.startSection("MigrationCompleted");
                break;
            }
            case FAILED: {
                writer.startSection("MigrationFailed");
                break;
            }
            default: {
                return;
            }
        }
        Member oldOwner = event.getOldOwner();
        writer.writeKeyValueEntry("oldOwner", oldOwner == null ? "null" : oldOwner.getAddress().toString());
        writer.writeKeyValueEntry("newOwner", event.getNewOwner().getAddress().toString());
        writer.writeKeyValueEntry("partitionId", event.getPartitionId());
        writer.endSection();
    }

    private void render(DiagnosticsLogWriter writer, ConnectionEvent event) {
        if (event.added) {
            writer.startSection("ConnectionAdded");
        } else {
            writer.startSection("ConnectionRemoved");
        }
        Connection connection = event.connection;
        writer.writeEntry(connection.toString());
        writer.writeKeyValueEntry("type", connection.getType().name());
        writer.writeKeyValueEntry("isAlive", connection.isAlive());
        if (!event.added) {
            String closeReason = connection.getCloseReason();
            Throwable closeCause = connection.getCloseCause();
            if (closeReason == null && closeCause != null) {
                closeReason = closeCause.getMessage();
            }
            writer.writeKeyValueEntry("closeReason", closeReason == null ? "Unknown" : closeReason);
            if (closeCause != null) {
                writer.startSection("CloseCause");
                String s = closeCause.getClass().getName();
                String message = closeCause.getMessage();
                writer.writeEntry(message != null ? s + ": " + message : s);
                for (StackTraceElement element : closeCause.getStackTrace()) {
                    writer.writeEntry(element.toString());
                }
                writer.endSection();
            }
        }
        writer.endSection();
    }

    private void render(DiagnosticsLogWriter writer, Version version) {
        writer.startSection("ClusterVersionChanged");
        writer.writeEntry(version.toString());
        writer.endSection();
    }

    private class ClusterVersionListenerImpl
    implements ClusterVersionListener {
        private ClusterVersionListenerImpl() {
        }

        @Override
        public void onClusterVersionChange(Version newVersion) {
            SystemLogPlugin.this.logQueue.add(newVersion);
        }
    }

    private class MigrationListenerImpl
    implements MigrationListener {
        private MigrationListenerImpl() {
        }

        @Override
        public void migrationStarted(MigrationEvent event) {
            SystemLogPlugin.this.logQueue.add(event);
        }

        @Override
        public void migrationCompleted(MigrationEvent event) {
            SystemLogPlugin.this.logQueue.add(event);
        }

        @Override
        public void migrationFailed(MigrationEvent event) {
            SystemLogPlugin.this.logQueue.add(event);
        }
    }

    private class MembershipListenerImpl
    extends MembershipAdapter {
        private MembershipListenerImpl() {
        }

        @Override
        public void memberAdded(MembershipEvent event) {
            SystemLogPlugin.this.logQueue.add(event);
        }

        @Override
        public void memberRemoved(MembershipEvent event) {
            SystemLogPlugin.this.logQueue.add(event);
        }
    }

    private class ConnectionListenerImpl
    implements ConnectionListener {
        private ConnectionListenerImpl() {
        }

        @Override
        public void connectionAdded(Connection connection) {
            SystemLogPlugin.this.logQueue.add(new ConnectionEvent(true, connection));
        }

        @Override
        public void connectionRemoved(Connection connection) {
            SystemLogPlugin.this.logQueue.add(new ConnectionEvent(false, connection));
        }
    }

    private static final class ConnectionEvent {
        final boolean added;
        final Connection connection;

        private ConnectionEvent(boolean added, Connection connection) {
            this.added = added;
            this.connection = connection;
        }
    }

    private class LifecycleListenerImpl
    implements LifecycleListener {
        private LifecycleListenerImpl() {
        }

        @Override
        public void stateChanged(LifecycleEvent event) {
            SystemLogPlugin.this.logQueue.add(event);
        }
    }
}

