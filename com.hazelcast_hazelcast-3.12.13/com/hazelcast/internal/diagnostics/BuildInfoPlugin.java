/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

import com.hazelcast.instance.BuildInfo;
import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.instance.JetBuildInfo;
import com.hazelcast.internal.diagnostics.DiagnosticsLogWriter;
import com.hazelcast.internal.diagnostics.DiagnosticsPlugin;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.impl.NodeEngineImpl;

public class BuildInfoPlugin
extends DiagnosticsPlugin {
    private final BuildInfo buildInfo = BuildInfoProvider.getBuildInfo();

    public BuildInfoPlugin(NodeEngineImpl nodeEngine) {
        this(nodeEngine.getLogger(BuildInfoPlugin.class));
    }

    public BuildInfoPlugin(ILogger logger) {
        super(logger);
    }

    @Override
    public long getPeriodMillis() {
        return -1L;
    }

    @Override
    public void onStart() {
        this.logger.info("Plugin:active");
    }

    @Override
    public void run(DiagnosticsLogWriter writer) {
        writer.startSection("BuildInfo");
        writer.writeKeyValueEntry("Build", this.buildInfo.getBuild());
        writer.writeKeyValueEntry("BuildNumber", "" + this.buildInfo.getBuildNumber());
        writer.writeKeyValueEntry("Revision", this.buildInfo.getRevision());
        BuildInfo upstreamBuildInfo = this.buildInfo.getUpstreamBuildInfo();
        if (upstreamBuildInfo != null) {
            writer.writeKeyValueEntry("UpstreamRevision", upstreamBuildInfo.getRevision());
        }
        writer.writeKeyValueEntry("Version", this.buildInfo.getVersion());
        writer.writeKeyValueEntry("SerialVersion", this.buildInfo.getSerializationVersion());
        writer.writeKeyValueEntry("Enterprise", this.buildInfo.isEnterprise());
        JetBuildInfo jetBuildInfo = this.buildInfo.getJetBuildInfo();
        if (jetBuildInfo != null) {
            writer.writeKeyValueEntry("JetVersion", jetBuildInfo.getVersion());
            writer.writeKeyValueEntry("JetBuild", jetBuildInfo.getBuild());
            writer.writeKeyValueEntry("JetRevision", jetBuildInfo.getRevision());
        }
        writer.endSection();
    }
}

