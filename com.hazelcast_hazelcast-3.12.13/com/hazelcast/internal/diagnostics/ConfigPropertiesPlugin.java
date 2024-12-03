/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

import com.hazelcast.internal.diagnostics.DiagnosticsLogWriter;
import com.hazelcast.internal.diagnostics.DiagnosticsPlugin;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.properties.HazelcastProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigPropertiesPlugin
extends DiagnosticsPlugin {
    private final HazelcastProperties properties;
    private final List<String> keyList = new ArrayList<String>();

    public ConfigPropertiesPlugin(NodeEngineImpl nodeEngine) {
        this(nodeEngine.getLogger(ConfigPropertiesPlugin.class), nodeEngine.getProperties());
    }

    public ConfigPropertiesPlugin(ILogger logger, HazelcastProperties properties) {
        super(logger);
        this.properties = properties;
    }

    @Override
    public void onStart() {
        this.logger.info("Plugin:active");
    }

    @Override
    public long getPeriodMillis() {
        return -1L;
    }

    @Override
    public void run(DiagnosticsLogWriter writer) {
        this.keyList.clear();
        this.keyList.addAll(this.properties.keySet());
        Collections.sort(this.keyList);
        writer.startSection("ConfigProperties");
        for (String key : this.keyList) {
            String value = this.properties.get(key);
            writer.writeKeyValueEntry(key, value);
        }
        writer.endSection();
    }
}

