/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.monitoring;

import com.atlassian.confluence.event.events.monitoring.AbstractMonitoringAuditEvent;
import java.util.List;

public class JmxMonitoringAuditEvent
extends AbstractMonitoringAuditEvent {
    public JmxMonitoringAuditEvent(boolean jmxToggleEnabled, boolean ipdToggleEnabled, boolean appToggleEnabled) {
        super(List.of(new AbstractMonitoringAuditEvent.Monitoring(AbstractMonitoringAuditEvent.MonitoringType.JMX, true, jmxToggleEnabled), new AbstractMonitoringAuditEvent.Monitoring(AbstractMonitoringAuditEvent.MonitoringType.APP_MONITORING, jmxToggleEnabled && appToggleEnabled, appToggleEnabled), new AbstractMonitoringAuditEvent.Monitoring(AbstractMonitoringAuditEvent.MonitoringType.IPD, jmxToggleEnabled && ipdToggleEnabled, ipdToggleEnabled)));
    }
}

