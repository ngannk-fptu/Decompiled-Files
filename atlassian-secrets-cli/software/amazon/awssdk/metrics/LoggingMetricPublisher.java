/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.metrics;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.event.Level;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.metrics.MetricCollection;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public final class LoggingMetricPublisher
implements MetricPublisher {
    private static final Logger LOGGER = Logger.loggerFor(LoggingMetricPublisher.class);
    private static final Integer PRETTY_INDENT_SIZE = 4;
    private final Level logLevel;
    private final Format format;

    private LoggingMetricPublisher(Level logLevel, Format format) {
        this.logLevel = Validate.notNull(logLevel, "logLevel", new Object[0]);
        this.format = Validate.notNull(format, "format", new Object[0]);
    }

    public static LoggingMetricPublisher create() {
        return new LoggingMetricPublisher(Level.INFO, Format.PLAIN);
    }

    public static LoggingMetricPublisher create(Level logLevel, Format format) {
        return new LoggingMetricPublisher(logLevel, format);
    }

    @Override
    public void publish(MetricCollection metrics) {
        if (!LOGGER.isLoggingLevelEnabled(this.logLevel)) {
            return;
        }
        switch (this.format) {
            case PLAIN: {
                LOGGER.log(this.logLevel, () -> "Metrics published: " + metrics);
                break;
            }
            case PRETTY: {
                String guid = Integer.toHexString(metrics.hashCode());
                this.logPretty(guid, metrics, 0);
                break;
            }
            default: {
                throw new IllegalStateException("Unsupported format: " + (Object)((Object)this.format));
            }
        }
    }

    private void logPretty(String guid, MetricCollection metrics, int indent) {
        ArrayList<String> metricValues = new ArrayList<String>();
        metrics.forEach(m -> metricValues.add(String.format("%s=%s", m.metric().name(), m.value())));
        int maxLen = LoggingMetricPublisher.getMaxLen(metricValues);
        LOGGER.log(this.logLevel, () -> String.format("[%s]%s %s", guid, StringUtils.repeat(" ", indent), metrics.name()));
        LOGGER.log(this.logLevel, () -> String.format("[%s]%s \u250c%s\u2510", guid, StringUtils.repeat(" ", indent), StringUtils.repeat("\u2500", maxLen + 2)));
        metricValues.forEach(metric -> LOGGER.log(this.logLevel, () -> String.format("[%s]%s \u2502 %s \u2502", guid, StringUtils.repeat(" ", indent), LoggingMetricPublisher.pad(metric, maxLen))));
        LOGGER.log(this.logLevel, () -> String.format("[%s]%s \u2514%s\u2518", guid, StringUtils.repeat(" ", indent), StringUtils.repeat("\u2500", maxLen + 2)));
        metrics.children().forEach(child -> this.logPretty(guid, (MetricCollection)child, indent + PRETTY_INDENT_SIZE));
    }

    private static int getMaxLen(List<String> strings) {
        int maxLen = 0;
        for (String str : strings) {
            maxLen = Math.max(maxLen, str.length());
        }
        return maxLen;
    }

    private static String pad(String str, int length) {
        return str + StringUtils.repeat(" ", length - str.length());
    }

    @Override
    public void close() {
    }

    public static enum Format {
        PLAIN,
        PRETTY;

    }
}

