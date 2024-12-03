/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.monitoring;

import com.atlassian.confluence.event.events.monitoring.AbstractMonitoringAuditEvent;
import java.util.List;

public class AppMonitoringAuditEvent
extends AbstractMonitoringAuditEvent {
    public AppMonitoringAuditEvent(boolean jmxToggleEnabled, boolean ipdToggleEnabled, boolean appToggleEnabled) {
        super(List.of(new AbstractMonitoringAuditEvent.Monitoring(AbstractMonitoringAuditEvent.MonitoringType.JMX, !jmxToggleEnabled && !appToggleEnabled, jmxToggleEnabled), new AbstractMonitoringAuditEvent.Monitoring(AbstractMonitoringAuditEvent.MonitoringType.APP_MONITORING, true, appToggleEnabled), new AbstractMonitoringAuditEvent.Monitoring(AbstractMonitoringAuditEvent.MonitoringType.IPD, false, ipdToggleEnabled)));
    }
}

