/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Alert
 *  com.atlassian.diagnostics.AlertListener
 *  com.atlassian.diagnostics.AlertTrigger
 *  com.atlassian.diagnostics.Issue
 *  com.atlassian.diagnostics.Severity
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.Alert;
import com.atlassian.diagnostics.AlertListener;
import com.atlassian.diagnostics.AlertTrigger;
import com.atlassian.diagnostics.Issue;
import com.atlassian.diagnostics.Severity;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingAlertListener
implements AlertListener {
    private static final String LOG_MESSAGE = "{} Component '{}' alerted '{}' (details: {}, trigger: {})";
    private Logger dataLogger = LoggerFactory.getLogger((String)"atlassian-diagnostics-data-logger");
    private String dataSeparator = " ; ";
    private Logger regularLogger = LoggerFactory.getLogger((String)"atlassian-diagnostics");

    public void onAlert(@Nonnull Alert alert) {
        Severity severity = Objects.requireNonNull(alert, "alert").getIssue().getSeverity();
        boolean dataLoggerEnabled = LoggingAlertListener.isEnabled(this.dataLogger, severity);
        boolean regularLoggerEnabled = LoggingAlertListener.isEnabled(this.regularLogger, severity);
        if (!dataLoggerEnabled && !regularLoggerEnabled) {
            return;
        }
        String detailsJson = LoggingAlertListener.getDetailsJson(alert);
        if (dataLoggerEnabled) {
            LoggingAlertListener.log(this.dataLogger, severity, LoggingAlertListener.createDataLoggerMessage(alert, this.dataSeparator, detailsJson), new Object[0]);
        }
        if (regularLoggerEnabled) {
            Issue issue = alert.getIssue();
            LoggingAlertListener.log(this.regularLogger, severity, LOG_MESSAGE, alert.getTimestamp(), issue.getComponent().getName(), issue.getSummary(), StringUtils.defaultIfBlank((CharSequence)detailsJson, (CharSequence)"{}"), LoggingAlertListener.getTrigger(alert));
        }
    }

    public void setDataLogger(Logger dataLogger) {
        this.dataLogger = dataLogger;
    }

    public void setRegularLogger(Logger regularLogger) {
        this.regularLogger = regularLogger;
    }

    private static void appendProperty(StringBuilder builder, String propertyName, String value) {
        if (StringUtils.isBlank((CharSequence)value)) {
            return;
        }
        if (builder.length() > 0) {
            builder.append(", ");
        }
        builder.append("\"").append(propertyName).append("\": \"").append(value).append("\"");
    }

    private static String createDataLoggerMessage(Alert alert, String separator, String detailsJson) {
        Issue issue = alert.getIssue();
        StringBuilder builder = new StringBuilder().append(alert.getTimestamp().toEpochMilli()).append(separator).append(issue.getSeverity()).append(separator).append(issue.getComponent().getId()).append(separator).append(issue.getId()).append(separator).append(issue.getSummary()).append(separator);
        AlertTrigger trigger = alert.getTrigger();
        builder.append(trigger.getPluginKey()).append(separator);
        trigger.getPluginVersion().ifPresent(builder::append);
        builder.append(separator);
        trigger.getModule().ifPresent(builder::append);
        builder.append(separator);
        builder.append(detailsJson);
        return builder.toString();
    }

    private static String getDetailsJson(Alert alert) {
        try {
            return alert.getDetails().map(details -> alert.getIssue().getJsonMapper().toJson(details)).orElse("");
        }
        catch (RuntimeException e) {
            return "";
        }
    }

    private static String getTrigger(Alert alert) {
        AlertTrigger trigger = alert.getTrigger();
        StringBuilder builder = new StringBuilder();
        LoggingAlertListener.appendProperty(builder, "pluginKey", trigger.getPluginKey());
        trigger.getPluginVersion().ifPresent(key -> LoggingAlertListener.appendProperty(builder, "pluginVersion", key));
        trigger.getModule().ifPresent(key -> LoggingAlertListener.appendProperty(builder, "module", key));
        return "{" + builder.toString() + "}";
    }

    private static boolean isEnabled(Logger logger, Severity severity) {
        if (logger == null) {
            return false;
        }
        switch (severity) {
            case INFO: {
                return logger.isInfoEnabled();
            }
            case WARNING: {
                return logger.isWarnEnabled();
            }
            case ERROR: {
                return logger.isErrorEnabled();
            }
        }
        return false;
    }

    private static void log(Logger logger, Severity severity, String format, Object ... arguments) {
        switch (severity) {
            case INFO: {
                logger.info(format, arguments);
                break;
            }
            case WARNING: {
                logger.warn(format, arguments);
                break;
            }
            case ERROR: {
                logger.error(format, arguments);
            }
        }
    }
}

