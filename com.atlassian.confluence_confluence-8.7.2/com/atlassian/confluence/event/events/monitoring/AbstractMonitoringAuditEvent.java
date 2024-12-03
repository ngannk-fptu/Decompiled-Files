/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.event.events.monitoring;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.annotations.Internal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@EventName(value="confluence.monitoring.audit.event")
@Internal
public abstract class AbstractMonitoringAuditEvent {
    private final List<Monitoring> monitoringList;

    AbstractMonitoringAuditEvent(List<Monitoring> monitoringList) {
        this.monitoringList = monitoringList;
    }

    public Set<String> getChangedMonitoringNames() {
        return this.monitoringList.stream().filter(Monitoring::isChanged).map(Monitoring::getName).collect(Collectors.toSet());
    }

    public boolean isMonitoringBeingEnabled() {
        return this.monitoringList.stream().anyMatch(monitoring -> !monitoring.wasEnabled() && monitoring.isChanged());
    }

    protected List<Monitoring> getMonitoringList() {
        return this.monitoringList;
    }

    protected static enum MonitoringType {
        JMX("JMX"),
        IPD("In-product diagnostics"),
        APP_MONITORING("App monitoring");

        private final String name;

        private MonitoringType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    protected static class Monitoring {
        private final MonitoringType type;
        private final boolean isChanged;
        private final boolean wasEnabled;

        public Monitoring(MonitoringType type, boolean isChanged, boolean wasEnabled) {
            this.type = type;
            this.isChanged = isChanged;
            this.wasEnabled = wasEnabled;
        }

        public boolean isChanged() {
            return this.isChanged;
        }

        public boolean wasEnabled() {
            return this.wasEnabled;
        }

        public String getName() {
            return this.type.getName();
        }

        public MonitoringType getType() {
            return this.type;
        }
    }
}

