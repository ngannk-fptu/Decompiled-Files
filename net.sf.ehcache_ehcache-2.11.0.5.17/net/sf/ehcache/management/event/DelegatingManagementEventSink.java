/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management.event;

import java.io.Serializable;
import net.sf.ehcache.management.event.ManagementEventSink;
import net.sf.ehcache.terracotta.ClusteredInstanceFactory;
import net.sf.ehcache.terracotta.TerracottaClient;

public class DelegatingManagementEventSink
implements ManagementEventSink {
    private final TerracottaClient terracottaClient;
    private volatile ClusteredInstanceFactory clusteredInstanceFactory;
    private volatile ManagementEventSink managementEventSink;

    public DelegatingManagementEventSink(TerracottaClient terracottaClient) {
        this.terracottaClient = terracottaClient;
    }

    private ManagementEventSink get() {
        ClusteredInstanceFactory cif = this.terracottaClient.getClusteredInstanceFactory();
        if (cif != null && cif != this.clusteredInstanceFactory) {
            this.managementEventSink = cif.createEventSink();
            this.clusteredInstanceFactory = cif;
        }
        return this.managementEventSink;
    }

    @Override
    public void sendManagementEvent(Serializable event, String type) {
        ManagementEventSink sink = this.get();
        if (sink != null) {
            sink.sendManagementEvent(event, type);
        }
    }
}

