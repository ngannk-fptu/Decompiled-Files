/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang.StringUtils
 *  org.joda.time.DateTime
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.hercules;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.troubleshooting.stp.ValidationLog;
import com.atlassian.troubleshooting.stp.action.AbstractSupportToolsAction;
import com.atlassian.troubleshooting.stp.action.Message;
import com.atlassian.troubleshooting.stp.action.SupportToolsAction;
import com.atlassian.troubleshooting.stp.events.EventStage;
import com.atlassian.troubleshooting.stp.events.StpLogScannerEvent;
import com.atlassian.troubleshooting.stp.hercules.HerculesDateTimeUtils;
import com.atlassian.troubleshooting.stp.hercules.LogScanMatch;
import com.atlassian.troubleshooting.stp.hercules.LogScanMonitor;
import com.atlassian.troubleshooting.stp.hercules.LogScanResult;
import com.atlassian.troubleshooting.stp.hercules.LogScanService;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.servlet.SafeHttpServletRequest;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nonnull;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SupportToolsHerculesScanAction
extends AbstractSupportToolsAction {
    public static final String ACTION_NAME = "hercules";
    private static final String SCAN_ID = "scanId";
    private static final String SCAN_RESULTS = "scanResults";
    private static final String SCAN_DATE = "scanDate";
    private static final String LOG_FILE_PATH = "logFilePath";
    private static final String FILE_NAME = "fileName";
    private static final String START_TIME = "startTime";
    private static final Logger LOG = LoggerFactory.getLogger(SupportToolsHerculesScanAction.class);
    private final SupportApplicationInfo applicationInfo;
    private final LogScanService scanService;
    private final EventPublisher eventPublisher;

    public SupportToolsHerculesScanAction(SupportApplicationInfo info, LogScanService scanService, EventPublisher eventPublisher) {
        super(ACTION_NAME, "stp.troubleshooting.title", "stp.log.analyzer.title", null);
        this.applicationInfo = Objects.requireNonNull(info);
        this.scanService = Objects.requireNonNull(scanService);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    @Nonnull
    public SupportToolsAction newInstance() {
        return new SupportToolsHerculesScanAction(this.applicationInfo, this.scanService, this.eventPublisher);
    }

    @Override
    public void prepare(Map<String, Object> context, SafeHttpServletRequest req, ValidationLog validationLog) {
        if (req.getParameter("startAgain") != null) {
            String scanId = req.getParameter(SCAN_ID);
            if (scanId != null && SupportToolsHerculesScanAction.canModifyState(req)) {
                this.scanService.cancelScan(scanId);
            }
            if (context.containsKey(SCAN_RESULTS)) {
                context.remove(SCAN_RESULTS);
                context.remove(SCAN_DATE);
                context.remove(LOG_FILE_PATH);
                context.remove(FILE_NAME);
            }
        } else {
            LogScanMonitor monitor = this.scanService.getLastScan();
            if (monitor != null && monitor.isDone()) {
                try {
                    LogScanResult scanResults = (LogScanResult)monitor.get(100L, TimeUnit.MILLISECONDS);
                    if (!scanResults.isEmpty()) {
                        List<LogScanMatch> matches = scanResults.getMatches();
                        if (!matches.isEmpty()) {
                            context.put(SCAN_RESULTS, scanResults.getMatches());
                            String logFilePath = monitor.getLogFilePath();
                            context.put(SCAN_DATE, HerculesDateTimeUtils.getTimeInRelativeFormat(monitor.getCreatedTimestamp()));
                            context.put(LOG_FILE_PATH, logFilePath);
                            context.put(FILE_NAME, Paths.get(logFilePath, new String[0]).getFileName().toString());
                        } else if (SupportToolsHerculesScanAction.canModifyState(req)) {
                            this.scanService.clearScanResultCache();
                        }
                    }
                }
                catch (TimeoutException e) {
                    LOG.debug("Log scan has not finished", (Throwable)e);
                }
                catch (ExecutionException e) {
                    LOG.warn("Log scan failed", (Throwable)e);
                    validationLog.addError("stp.hercules.scan.failed", new Serializable[]{e.getCause().getMessage()});
                }
                catch (InterruptedException e) {
                    LOG.warn("Interrupted while waiting for log scan to complete", (Throwable)e);
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public void validate(Map<String, Object> context, SafeHttpServletRequest req, ValidationLog validationLog) {
        String logFilePath = req.getParameter(LOG_FILE_PATH);
        String scanId = req.getParameter(SCAN_ID);
        if (scanId != null) {
            LogScanMonitor monitor = this.scanService.getMonitor(scanId);
            if (monitor == null) {
                validationLog.addError("stp.hercules.scan.timedout", new Serializable[0]);
            }
        } else if (logFilePath == null || logFilePath.length() == 0 || !new File(logFilePath).exists()) {
            validationLog.addFieldError(LOG_FILE_PATH, "You must provide the location of a valid log file.");
        }
    }

    @Override
    public void execute(Map<String, Object> context, SafeHttpServletRequest req, ValidationLog validationLog) {
        LogScanMonitor monitor;
        String scanId = req.getParameter(SCAN_ID);
        String logFilePath = req.getParameter(LOG_FILE_PATH);
        if (scanId != null) {
            monitor = this.scanService.getMonitor(scanId);
            if (monitor != null) {
                logFilePath = monitor.getLogFilePath();
            }
        } else {
            monitor = this.scanService.getLatestScan();
        }
        if (monitor == null && SupportToolsHerculesScanAction.canModifyState(req)) {
            monitor = this.scanService.scan(new File(logFilePath));
            this.triggerAnalytics(EventStage.RAN, monitor);
            context.put(START_TIME, new DateTime().getMillis());
        }
        context.put(LOG_FILE_PATH, logFilePath);
        context.put(SCAN_ID, monitor == null ? null : monitor.getTaskId());
        if (monitor != null && monitor.isDone() && !monitor.isCancelled()) {
            try {
                LogScanResult results = (LogScanResult)monitor.get(100L, TimeUnit.MILLISECONDS);
                List<LogScanMatch> matches = results.getMatches();
                if (monitor.getErrors().isEmpty()) {
                    context.put(SCAN_RESULTS, matches);
                } else {
                    for (Message message : monitor.getErrors()) {
                        validationLog.addError(message);
                        this.triggerAnalytics(EventStage.FAIL, monitor);
                    }
                }
                if (!monitor.getWarnings().isEmpty()) {
                    for (Message message : monitor.getWarnings()) {
                        validationLog.addWarning(message);
                        this.triggerAnalytics(EventStage.WARN, monitor);
                    }
                }
                String fileName = logFilePath.substring(logFilePath.lastIndexOf(File.separator) + 1);
                context.put(FILE_NAME, fileName);
                String startTime = req.getParameter(START_TIME);
                if (StringUtils.isNumeric((String)startTime)) {
                    long stopTime = new DateTime().getMillis();
                    long durationInSeconds = (stopTime - Long.parseLong(startTime)) / 1000L;
                    this.triggerAnalytics(monitor, durationInSeconds, matches.size());
                }
            }
            catch (TimeoutException e) {
                LOG.debug("Log scan has not finished", (Throwable)e);
            }
            catch (ExecutionException e) {
                LOG.warn("Log scan failed", (Throwable)e);
                validationLog.addError("stp.hercules.scan.failed", new Serializable[]{e.getCause().getMessage()});
                this.triggerAnalytics(EventStage.FAIL, monitor);
            }
            catch (InterruptedException e) {
                LOG.warn("Interrupted while waiting for log scan to complete", (Throwable)e);
                Thread.currentThread().interrupt();
            }
        } else if (monitor != null && monitor.isCancelled()) {
            validationLog.addError("stp.hercules.cancelled", new Serializable[0]);
        }
    }

    private void triggerAnalytics(EventStage stage, LogScanMonitor monitor) {
        StpLogScannerEvent event = new StpLogScannerEvent(stage, monitor.getTaskId());
        this.eventPublisher.publish((Object)event);
    }

    private void triggerAnalytics(LogScanMonitor monitor, long duration, int resultSize) {
        this.eventPublisher.publish((Object)new StpLogScannerEvent(EventStage.DONE, monitor.getTaskId(), duration, resultSize));
    }
}

