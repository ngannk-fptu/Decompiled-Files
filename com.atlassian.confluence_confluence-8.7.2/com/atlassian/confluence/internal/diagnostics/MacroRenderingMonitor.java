/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.MonitoringService
 *  com.atlassian.diagnostics.Severity
 *  com.atlassian.diagnostics.detail.ThreadDumpProducer
 *  com.atlassian.plugin.osgi.util.OsgiHeaderUtil
 *  com.atlassian.util.profiling.MetricTimer
 *  com.atlassian.util.profiling.Metrics
 *  com.google.common.collect.ImmutableMap
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.FrameworkUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.diagnostics;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.internal.diagnostics.AlertTriggerFactory;
import com.atlassian.confluence.internal.diagnostics.ConfluenceMonitor;
import com.atlassian.confluence.internal.diagnostics.DiagnosticsInfo;
import com.atlassian.confluence.internal.diagnostics.DiagnosticsWorker;
import com.atlassian.confluence.internal.diagnostics.EventListeningDarkFeatureSetting;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.xhtml.XhtmlMacroManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.diagnostics.MonitoringService;
import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.detail.ThreadDumpProducer;
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import com.atlassian.util.profiling.MetricTimer;
import com.atlassian.util.profiling.Metrics;
import com.google.common.collect.ImmutableMap;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacroRenderingMonitor
extends ConfluenceMonitor {
    private static final long SLOW_MACRO_RENDERING_SECS = Integer.getInteger("diagnostics.slow.macro.rendering.secs", 30).intValue();
    private static final int MACRO_RENDERING_EXCEEDS_TIME_LIMIT_ID = 1001;
    private static final Logger logger = LoggerFactory.getLogger(MacroRenderingMonitor.class);
    private static final String MONITOR_ID = "MACRO";
    public static final String METRIC_KEY = "macro.render";
    private final Map<MacroRendering, DiagnosticsInfo> renderings = new ConcurrentHashMap<MacroRendering, DiagnosticsInfo>();
    private final ThreadDumpProducer threadDumpProducer;
    private final AlertTriggerFactory alertTriggerFactory;
    private final EventListeningDarkFeatureSetting riskyDiagnosticMonitorsEnabled;

    public MacroRenderingMonitor(@NonNull ThreadDumpProducer threadDumpProducer, @NonNull AlertTriggerFactory alertTriggerFactory, @NonNull EventListeningDarkFeatureSetting riskyDiagnosticMonitorsEnabled) {
        this.threadDumpProducer = Objects.requireNonNull(threadDumpProducer);
        this.alertTriggerFactory = Objects.requireNonNull(alertTriggerFactory);
        this.riskyDiagnosticMonitorsEnabled = riskyDiagnosticMonitorsEnabled;
    }

    @Override
    public void init(MonitoringService monitoringService) {
        super.init(monitoringService);
        this.monitor = monitoringService.createMonitor(MONITOR_ID, "diagnostics.macro.rendering.name", this.riskyDiagnosticMonitorsEnabled::isEnabled);
        this.defineIssue("diagnostics.macro.rendering.issue", 1001, Severity.WARNING);
        this.startMonitorThread();
        logger.debug("{} monitor has been initialized", (Object)MONITOR_ID);
    }

    @Override
    protected String getMonitorId() {
        return MONITOR_ID;
    }

    public void start(MacroRendering macroRendering) {
        this.renderings.put(macroRendering, new DiagnosticsInfo(Thread.currentThread(), AuthenticatedUserThreadLocal.getUsername(), Duration.ofSeconds(SLOW_MACRO_RENDERING_SECS)));
    }

    public void stop(MacroRendering macroRendering) {
        DiagnosticsInfo diagnostics = this.renderings.remove(macroRendering);
        if (diagnostics != null) {
            macroRendering.timer.update(diagnostics.getActualTime());
        }
    }

    private void startMonitorThread() {
        this.startMonitorThread(new DiagnosticsWorker<MacroRendering>(this.renderings, this::alert, Duration.ofSeconds(SLOW_MACRO_RENDERING_SECS)), "diagnostics-macro-rendering-thread");
    }

    private void alert(MacroRendering rendering, DiagnosticsInfo info) {
        this.alert(1001, builder -> builder.timestamp(Instant.now()).trigger(this.alertTriggerFactory.create(rendering.macroClass)).details(() -> ImmutableMap.builder().put((Object)"username", (Object)info.getUsername().orElse("")).put((Object)"macroName", (Object)rendering.macroDefinition.getName()).put((Object)"pageUrl", (Object)rendering.conversionContext.getEntity().getUrlPath()).put((Object)"thresholdInSecs", (Object)info.getTimeLimit().getSeconds()).put((Object)"threadId", (Object)info.getWorkerThread().getId()).put((Object)"threadName", (Object)info.getWorkerThread().getName()).put((Object)"threadStatus", (Object)info.getWorkerThread().getState()).put((Object)"threadDump", (Object)this.threadDumpProducer.produce(Collections.singleton(info.getWorkerThread()))).build()));
    }

    public static class MacroRendering {
        private final ConversionContext conversionContext;
        private final MacroDefinition macroDefinition;
        private final Class<?> macroClass;
        private final MetricTimer timer;

        public MacroRendering(ConversionContext conversionContext, MacroDefinition macroDefinition, Macro macro) {
            this.conversionContext = conversionContext;
            this.macroDefinition = macroDefinition;
            this.macroClass = XhtmlMacroManager.unwrapMacroProxy(macro).getClass();
            this.timer = Metrics.metric((String)MacroRenderingMonitor.METRIC_KEY).fromPluginKey(MacroRendering.getPluginKey(macro)).tag("macroName", macroDefinition.getName()).timer();
        }

        private static String getPluginKey(Macro macro) {
            Bundle bundle = FrameworkUtil.getBundle(macro.getClass());
            if (bundle == null) {
                return null;
            }
            return OsgiHeaderUtil.getPluginKey((Bundle)bundle);
        }
    }
}

