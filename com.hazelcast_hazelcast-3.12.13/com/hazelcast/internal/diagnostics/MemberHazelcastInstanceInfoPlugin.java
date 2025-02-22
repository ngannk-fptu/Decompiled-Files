/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

import com.hazelcast.core.Member;
import com.hazelcast.instance.NodeState;
import com.hazelcast.internal.diagnostics.DiagnosticsLogWriter;
import com.hazelcast.internal.diagnostics.DiagnosticsPlugin;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.properties.HazelcastProperty;
import java.util.concurrent.TimeUnit;

public class MemberHazelcastInstanceInfoPlugin
extends DiagnosticsPlugin {
    public static final HazelcastProperty PERIOD_SECONDS = new HazelcastProperty("hazelcast.diagnostics.memberinfo.period.seconds", 60, TimeUnit.SECONDS);
    private final long periodMillis;
    private final NodeEngineImpl nodeEngine;

    public MemberHazelcastInstanceInfoPlugin(NodeEngineImpl nodeEngine) {
        super(nodeEngine.getLogger(MemberHazelcastInstanceInfoPlugin.class));
        this.periodMillis = nodeEngine.getProperties().getMillis(PERIOD_SECONDS);
        this.nodeEngine = nodeEngine;
    }

    @Override
    public long getPeriodMillis() {
        return this.periodMillis;
    }

    @Override
    public void onStart() {
        this.logger.info("Plugin:active, period-millis:" + this.periodMillis);
    }

    @Override
    public void run(DiagnosticsLogWriter writer) {
        writer.startSection("HazelcastInstance");
        writer.writeKeyValueEntry("thisAddress", this.nodeEngine.getNode().getThisAddress().toString());
        writer.writeKeyValueEntry("isRunning", this.nodeEngine.getNode().isRunning());
        writer.writeKeyValueEntry("isLite", this.nodeEngine.getNode().isLiteMember());
        writer.writeKeyValueEntry("joined", this.nodeEngine.getNode().getClusterService().isJoined());
        NodeState state = this.nodeEngine.getNode().getState();
        writer.writeKeyValueEntry("nodeState", state == null ? "null" : state.toString());
        writer.writeKeyValueEntry("clusterId", this.nodeEngine.getClusterService().getClusterId());
        writer.writeKeyValueEntry("clusterSize", this.nodeEngine.getClusterService().getSize());
        writer.writeKeyValueEntry("isMaster", this.nodeEngine.getClusterService().isMaster());
        Address masterAddress = this.nodeEngine.getClusterService().getMasterAddress();
        writer.writeKeyValueEntry("masterAddress", masterAddress == null ? "null" : masterAddress.toString());
        writer.startSection("Members");
        for (Member member : this.nodeEngine.getClusterService().getMemberImpls()) {
            writer.writeEntry(member.getAddress().toString());
        }
        writer.endSection();
        writer.endSection();
    }
}

