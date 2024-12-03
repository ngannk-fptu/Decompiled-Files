/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdLoggingService
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdMetric
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdMetricValue
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal.ipd;

import com.atlassian.diagnostics.ipd.internal.spi.IpdLoggingService;
import com.atlassian.diagnostics.ipd.internal.spi.IpdMetric;
import com.atlassian.diagnostics.ipd.internal.spi.IpdMetricValue;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class DefaultLoggingService
implements IpdLoggingService {
    private static final Logger dataLogger = LoggerFactory.getLogger((String)"ipd-monitoring-data-logger");
    private final Logger regularLogger = LoggerFactory.getLogger((String)"ipd-monitoring");
    private static final ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public void logMetric(IpdMetric metric, boolean includeExtraLogging) {
        this.logMetric(metric, DefaultLoggingService.getCurrentTimestamp(), includeExtraLogging);
    }

    public void logMetric(IpdMetric ipdMetric, String timestamp, boolean includeExtraLogging) {
        if (!ipdMetric.isEnabled()) {
            return;
        }
        ipdMetric.readValues(includeExtraLogging).stream().filter(metricValue -> !metricValue.getAttributes().isEmpty()).map(metric -> new IpdLogEntry(timestamp, (IpdMetricValue)metric, includeExtraLogging)).forEach(value -> {
            try {
                this.logData(objectMapper.writeValueAsString(value));
            }
            catch (JsonProcessingException e) {
                this.regularLogger.warn("Can't serialize Jmx instrument: {}", value);
            }
        });
    }

    private void logData(String data) {
        if (dataLogger.isInfoEnabled()) {
            dataLogger.info(this.formatData(data));
        }
    }

    public static String getCurrentTimestamp() {
        return String.valueOf(Instant.now().getEpochSecond());
    }

    protected String formatData(String data) {
        return "IPDMONITORING " + data;
    }

    static class IpdLogEntry {
        private final String timestamp;
        private final String label;
        private final String objectName;
        private final Map<String, String> tags;
        private final Object attributes;

        public IpdLogEntry(String timestamp, IpdMetricValue metricValue, boolean includeExtraLogging) {
            this.timestamp = timestamp;
            this.label = metricValue.getLabel().toUpperCase();
            this.objectName = includeExtraLogging ? metricValue.getObjectName() : null;
            this.tags = metricValue.getTags().isEmpty() ? null : metricValue.getTags();
            this.attributes = metricValue.getAttributes();
        }

        public String getTimestamp() {
            return this.timestamp;
        }

        public String getLabel() {
            return this.label;
        }

        public String getObjectName() {
            return this.objectName;
        }

        public Map<String, String> getTags() {
            return this.tags;
        }

        public Object getAttributes() {
            return this.attributes;
        }
    }
}

