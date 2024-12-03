/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

import com.hazelcast.internal.diagnostics.DiagnosticsLogWriter;
import com.hazelcast.internal.diagnostics.DiagnosticsPlugin;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.impl.NodeEngineImpl;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SystemPropertiesPlugin
extends DiagnosticsPlugin {
    private static final String JVM_ARGS = "java.vm.args";
    private final List keys = new ArrayList();
    private String inputArgs;

    public SystemPropertiesPlugin(NodeEngineImpl nodeEngine) {
        this(nodeEngine.getLogger(SystemPropertiesPlugin.class));
    }

    public SystemPropertiesPlugin(ILogger logger) {
        super(logger);
    }

    @Override
    public long getPeriodMillis() {
        return -1L;
    }

    @Override
    public void onStart() {
        this.logger.info("Plugin:active");
        this.inputArgs = SystemPropertiesPlugin.getInputArgs();
    }

    private static String getInputArgs() {
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        List<String> arguments = runtimeMxBean.getInputArguments();
        StringBuilder sb = new StringBuilder();
        for (String argument : arguments) {
            sb.append(argument);
            sb.append(' ');
        }
        return sb.toString();
    }

    @Override
    public void run(DiagnosticsLogWriter writer) {
        writer.startSection("SystemProperties");
        this.keys.clear();
        this.keys.addAll(System.getProperties().keySet());
        this.keys.add(JVM_ARGS);
        Collections.sort(this.keys);
        for (Object key : this.keys) {
            String keyString = (String)key;
            if (SystemPropertiesPlugin.isIgnored(keyString)) continue;
            String value = this.getProperty(keyString);
            writer.writeKeyValueEntry(keyString, value);
        }
        writer.endSection();
    }

    private static boolean isIgnored(String systemProperty) {
        if (systemProperty.startsWith("java.awt")) {
            return true;
        }
        return !systemProperty.startsWith("java") && !systemProperty.startsWith("hazelcast") && !systemProperty.startsWith("sun") && !systemProperty.startsWith("os");
    }

    private String getProperty(String keyString) {
        if (keyString.equals(JVM_ARGS)) {
            return this.inputArgs;
        }
        return System.getProperty(keyString);
    }
}

