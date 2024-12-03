/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.monitoring;

import com.atlassian.confluence.event.events.monitoring.AbstractMonitoringAuditEvent;
import java.util.List;

public class IpdMonitoringAuditEvent
extends AbstractMonitoringAuditEvent {
    public IpdMonitoringAuditEvent(boolean jmxToggleEnabled, boolean ipdToggleEnabled, boolean appToggleEnabled) {
        super(List.of(new AbstractMonitoringAuditEvent.Monitoring(AbstractMonitoringAuditEvent.MonitoringType.JMX, !jmxToggleEnabled && !ipdToggleEnabled, jmxToggleEnabled), new AbstractMonitoringAuditEvent.Monitoring(AbstractMonitoringAuditEvent.MonitoringType.APP_MONITORING, false, appToggleEnabled), new AbstractMonitoringAuditEvent.Monitoring(AbstractMonitoringAuditEvent.MonitoringType.IPD, true, ipdToggleEnabled)));
    }
}

