/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerContext
 *  com.atlassian.spring.container.ContainerManager
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.impl.profiling;

import com.atlassian.confluence.impl.metrics.ConfluenceJmxConfig;
import com.atlassian.confluence.impl.profiling.NodeJmxMonitoringConfig;
import com.atlassian.spring.container.ContainerContext;
import com.atlassian.spring.container.ContainerManager;
import java.io.Serializable;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.StringUtils;

public class CollectNodeJmxMonitoringConfigs
implements Callable<NodeJmxMonitoringConfig>,
Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public NodeJmxMonitoringConfig call() throws Exception {
        ContainerContext context = ContainerManager.getInstance().getContainerContext();
        ConfluenceJmxConfig jmxConfig = (ConfluenceJmxConfig)context.getComponent((Object)"confluenceJmxConfig");
        boolean jmxEnabled = jmxConfig.isJmxEnabled();
        boolean sysPropSet = StringUtils.isNotEmpty((CharSequence)System.getProperty("confluence.jmx.disabled"));
        return new NodeJmxMonitoringConfigImpl(jmxEnabled, sysPropSet);
    }

    private static class NodeJmxMonitoringConfigImpl
    implements NodeJmxMonitoringConfig,
    Serializable {
        private static final long serialVersionUID = 1L;
        private final boolean enabled;
        private final boolean systemPropertySet;

        public NodeJmxMonitoringConfigImpl(boolean enabled, Boolean systemPropertySet) {
            this.enabled = enabled;
            this.systemPropertySet = systemPropertySet;
        }

        @Override
        public boolean isEnabled() {
            return this.enabled;
        }

        @Override
        public boolean isSystemPropertySet() {
            return this.systemPropertySet;
        }
    }
}

