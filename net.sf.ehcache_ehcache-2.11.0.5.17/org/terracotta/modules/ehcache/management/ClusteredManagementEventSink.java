/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.internal.feature.ManagementInternalFeature
 *  org.terracotta.toolkit.internal.feature.ToolkitManagementEvent
 */
package org.terracotta.modules.ehcache.management;

import java.io.Serializable;
import net.sf.ehcache.management.event.ManagementEventSink;
import org.terracotta.toolkit.internal.feature.ManagementInternalFeature;
import org.terracotta.toolkit.internal.feature.ToolkitManagementEvent;

public class ClusteredManagementEventSink
implements ManagementEventSink {
    private final ManagementInternalFeature feature;

    public ClusteredManagementEventSink(ManagementInternalFeature feature) {
        this.feature = feature;
    }

    @Override
    public void sendManagementEvent(Serializable event, String type) {
        this.feature.sendEvent(new ToolkitManagementEvent(event, type));
    }
}

