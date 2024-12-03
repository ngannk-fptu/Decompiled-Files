/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.Email
 *  com.atlassian.templaterenderer.RenderingException
 *  com.google.common.util.concurrent.ListenableFutureTask
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.io.FilenameUtils
 *  org.apache.commons.lang3.time.DateFormatUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.hercules;

import com.atlassian.mail.Email;
import com.atlassian.templaterenderer.RenderingException;
import com.atlassian.troubleshooting.stp.hercules.LogScanHelper;
import com.atlassian.troubleshooting.stp.hercules.LogScanReportSettings;
import com.atlassian.troubleshooting.stp.hercules.LogScanResult;
import com.atlassian.troubleshooting.stp.hercules.LogScanTask;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.mail.MailUtility;
import com.atlassian.troubleshooting.stp.salext.mail.ProductAwareEmail;
import com.atlassian.troubleshooting.stp.scheduler.utils.RenderingUtils;
import com.atlassian.troubleshooting.stp.task.DefaultTaskMonitor;
import com.atlassian.troubleshooting.stp.task.MonitoredCallable;
import com.google.common.util.concurrent.ListenableFutureTask;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class LogScanReportTask
implements MonitoredCallable<Void, DefaultTaskMonitor<Void>> {
    private static final Logger LOG = LoggerFactory.getLogger(LogScanReportTask.class);
    private final SupportApplicationInfo info;
    private final MailUtility mailUtility;
    private final DefaultTaskMonitor<Void> monitor;
    private final LogScanReportSettings settings;
    private final LogScanTask logScanTask;
    private final LogScanHelper logScanHelper;

    LogScanReportTask(LogScanReportSettings settings, LogScanTask logScanTask, SupportApplicationInfo info, MailUtility mailUtility, LogScanHelper logScanHelper, DefaultTaskMonitor<Void> monitor) {
        this.info = Objects.requireNonNull(info);
        this.mailUtility = Objects.requireNonNull(mailUtility);
        this.monitor = Objects.requireNonNull(monitor);
        this.settings = Objects.requireNonNull(settings);
        this.logScanTask = Objects.requireNonNull(logScanTask);
        this.logScanHelper = Objects.requireNonNull(logScanHelper);
    }

    @Override
    @Nonnull
    public DefaultTaskMonitor<Void> getMonitor() {
        return this.monitor;
    }

    @Override
    public Void call() throws Exception {
        String logFilePath = this.logScanTask.getLogFile().getPath();
        File primaryLog = this.logScanHelper.makeFile(logFilePath);
        if (!primaryLog.exists()) {
            LOG.error("Log file '{}' doesn't exist, can't continue with the scan.", (Object)logFilePath);
            return null;
        }
        LOG.info("Scanning log file '{}'...", (Object)logFilePath);
        long start = System.currentTimeMillis();
        ListenableFutureTask subTask = ListenableFutureTask.create((Callable)this.logScanTask);
        this.logScanTask.getMonitor().init("scan-log-subtask", subTask);
        try {
            LogScanResult result = this.logScanTask.call();
            LOG.info("Finished scanning {} using Hercules. Total size: {} bytes. Time taken: {} ms. Patterns matched: {}", new Object[]{logFilePath, primaryLog.length(), System.currentTimeMillis() - start, result.size()});
            if (result.isEmpty()) {
                LOG.info("No issues found in log file '{}'. Not sending the report by email.", (Object)logFilePath);
                return null;
            }
            LOG.info("Preparing hercules report...");
            try {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("info", this.info);
                params.put("fileName", FilenameUtils.getName((String)logFilePath));
                params.put("results", result.getMatches());
                params.put("warnings", this.monitor.getWarnings());
                params.put("errors", this.monitor.getErrors());
                String mailBody = RenderingUtils.render(this.info.getTemplateRenderer(), "/templates/email/hercules-report.vm", params);
                String recipients = this.settings.getRecipients();
                Email email = new ProductAwareEmail(recipients).addProductHeader(this.info.getApplicationName()).setFrom(this.info.getFromAddress()).setSubject(this.info.getText("stp.scheduler.hercules.mail.subject", new Serializable[]{DateFormatUtils.ISO_DATE_FORMAT.format(new Date())})).setBody(mailBody).setMimeType("text/html");
                LOG.info("Sending Hercules report...");
                this.mailUtility.sendMail(email);
            }
            catch (RenderingException e) {
                LOG.error("Error rendering Hercules report: ", (Throwable)e);
            }
            catch (IOException e) {
                LOG.error("I/O error while generating Hercules report: ", (Throwable)e);
            }
        }
        catch (InterruptedException e) {
            LOG.error("Interrupted while waiting for Hercules scan to complete: ", (Throwable)e);
            Thread.currentThread().interrupt();
        }
        catch (ExecutionException e) {
            LOG.error("Error generating Hercules report: ", e.getCause());
        }
        return null;
    }
}

