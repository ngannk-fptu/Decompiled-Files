/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.hercules;

import com.atlassian.sisyphus.DefaultSisyphusPatternMatcher;
import com.atlassian.sisyphus.LogLine;
import com.atlassian.sisyphus.MatchResultVisitor;
import com.atlassian.sisyphus.SisyphusDateMatcher;
import com.atlassian.sisyphus.SisyphusPattern;
import com.atlassian.troubleshooting.stp.action.DefaultMessage;
import com.atlassian.troubleshooting.stp.hercules.FileProgressMonitor;
import com.atlassian.troubleshooting.stp.hercules.FileProgressMonitorInputStream;
import com.atlassian.troubleshooting.stp.hercules.LogScanHelper;
import com.atlassian.troubleshooting.stp.hercules.LogScanMonitor;
import com.atlassian.troubleshooting.stp.hercules.LogScanResult;
import com.atlassian.troubleshooting.stp.hercules.sisyphus.ATSTDateMatcher;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.task.MonitoredCallable;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.ConnectException;
import java.util.Objects;
import java.util.regex.PatternSyntaxException;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class LogScanTask
implements MonitoredCallable<LogScanResult, LogScanMonitor> {
    private static final Logger LOG = LoggerFactory.getLogger(LogScanTask.class);
    private static final String LOG_SCAN_URL = "https://confluence.atlassian.com/x/KZoiLw";
    private final SupportApplicationInfo applicationInfo;
    private final File logFile;
    private final LogScanMonitor monitor;
    private final LogScanResult result;
    private final LogScanHelper logScanHelper;
    private final SisyphusDateMatcher dateMatcher;

    LogScanTask(File logFile, SupportApplicationInfo applicationInfo, LogScanHelper logScanHelper, LogScanMonitor logScanMonitor) {
        this.applicationInfo = Objects.requireNonNull(applicationInfo);
        this.logFile = Objects.requireNonNull(logFile);
        this.logScanHelper = Objects.requireNonNull(logScanHelper);
        this.monitor = Objects.requireNonNull(logScanMonitor);
        this.result = new LogScanResult();
        this.dateMatcher = new ATSTDateMatcher();
    }

    public File getLogFile() {
        return this.logFile;
    }

    @Override
    @Nonnull
    public LogScanMonitor getMonitor() {
        return this.monitor;
    }

    @Override
    public LogScanResult call() throws Exception {
        try (BufferedReader reader = this.logScanHelper.makeReader(new FileProgressMonitorInputStream(this.logFile, new LogFileProgressMonitor(this.applicationInfo, this.monitor)));){
            DefaultSisyphusPatternMatcher matcher = new DefaultSisyphusPatternMatcher(this.applicationInfo.getPatternSource(), this.dateMatcher);
            matcher.match(reader, new ScanResultVisitor(this.monitor, this.result), this.applicationInfo.getApplicationRestartPattern());
        }
        catch (ConnectException e) {
            this.monitor.addError(new DefaultMessage(this.applicationInfo.getText("stp.hercules.scan.failed"), this.applicationInfo.getText("stp.hercules.scan.failed.connection"), LOG_SCAN_URL));
            LOG.info("Scan failed:", (Throwable)e);
        }
        catch (FileNotFoundException e) {
            this.monitor.addError(new DefaultMessage(this.applicationInfo.getText("stp.hercules.scan.failed"), this.applicationInfo.getText("stp.hercules.scan.error.file.not.found"), LOG_SCAN_URL));
            LOG.info("Scan failed:", (Throwable)e);
        }
        catch (PatternSyntaxException e) {
            this.monitor.addWarning(new DefaultMessage(this.applicationInfo.getText("stp.hercules.scan.incomplete"), this.applicationInfo.getText("stp.hercules.scan.warn.invalid.pattern"), LOG_SCAN_URL));
            LOG.info("Scan may have failed:", (Throwable)e);
        }
        return this.result;
    }

    private static class ScanResultVisitor
    implements MatchResultVisitor {
        private final TaskMonitor<?> monitor;
        private final LogScanResult result;

        private ScanResultVisitor(TaskMonitor<?> monitor, LogScanResult result) {
            this.monitor = monitor;
            this.result = result;
        }

        @Override
        public void patternMatched(String s, LogLine logLine, SisyphusPattern pattern) {
            this.result.add(pattern, logLine);
        }

        @Override
        public boolean isCancelled() {
            return this.monitor.isCancelled();
        }
    }

    private static class LogFileProgressMonitor
    implements FileProgressMonitor {
        private final SupportApplicationInfo applicationInfo;
        private final LogScanMonitor monitor;
        private long bytesProcessed;
        private long totalSize;

        private LogFileProgressMonitor(SupportApplicationInfo applicationInfo, LogScanMonitor monitor) {
            this.applicationInfo = applicationInfo;
            this.monitor = monitor;
        }

        @Override
        public void setTotalSize(long value) {
            this.totalSize = value;
        }

        @Override
        public void setProgress(long value) {
            this.bytesProcessed = value;
            this.monitor.updateProgress((int)Math.round(100.0 * (double)this.bytesProcessed / (double)this.totalSize), this.applicationInfo.getText("stp.hercules.scan.progress", new Serializable[]{this.monitor.getLogFilePath(), Long.valueOf(this.bytesProcessed / 1024L), Long.valueOf(this.totalSize / 1024L)}));
        }

        @Override
        public boolean isCancelled() {
            return this.monitor.isCancelled();
        }
    }
}

