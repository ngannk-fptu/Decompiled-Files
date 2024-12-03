/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.core.Filter
 *  org.apache.logging.log4j.core.LoggerContext
 *  org.apache.logging.log4j.core.filter.BurstFilter
 *  org.apache.logging.log4j.core.filter.BurstFilter$Builder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.logging.log4j.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.filter.BurstFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log4j2BurstFilterInstantiator {
    private static final Logger log = LoggerFactory.getLogger(Log4j2BurstFilterInstantiator.class);
    private static final String PROPERTIES_FILE_NAME = "burstFilterConfig.properties";
    private static final String BURST_FILTER_PREFIX = "burstFilter";
    private static final String BURST_FILTER_MAX_BURST_KEY = "maxBurst";
    private static final String BURST_FILTER_RATE_KEY = "rate";
    private static final String BURST_FILTER_LEVEL_KEY = "level";
    private LoggerContext loggerContext = (LoggerContext)LogManager.getContext((boolean)false);

    public Log4j2BurstFilterInstantiator() {
        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);){
            Properties props = new Properties();
            if (input == null) {
                log.debug("Could not find burst filter instantiator property file: {}", (Object)PROPERTIES_FILE_NAME);
            }
            props.load(input);
            new Log4j2BurstFilterInstantiator((LoggerContext)LogManager.getContext((boolean)false), props);
        }
        catch (IOException ex) {
            log.debug("Error while reading burst filter insantiator property file: {}", (Object)PROPERTIES_FILE_NAME, (Object)ex);
        }
    }

    public Log4j2BurstFilterInstantiator(LoggerContext loggerContext, Properties props) {
        this.loggerContext = loggerContext;
        this.instantiateBurstFilters(props);
    }

    private void instantiateBurstFilters(Properties props) {
        HashMap<String, BurstFilterConfig> filters = this.parseProps(props);
        for (Map.Entry<String, BurstFilterConfig> entry : filters.entrySet()) {
            BurstFilterConfig config = entry.getValue();
            String loggerName = config.getLoggerName();
            if (loggerName == null || loggerName.isBlank()) continue;
            this.instantiateBurstFilter(config);
        }
    }

    private void instantiateBurstFilter(BurstFilterConfig config) {
        BurstFilter burstFilter = new BurstFilter.Builder().setMaxBurst(config.getMaxBurst().longValue()).setRate(config.getRate().floatValue()).setLevel(config.getLevel()).build();
        burstFilter.start();
        this.loggerContext.getConfiguration().addLoggerFilter(this.loggerContext.getLogger(config.getLoggerName()), (Filter)burstFilter);
    }

    private HashMap<String, BurstFilterConfig> parseProps(Properties props) {
        HashMap<String, BurstFilterConfig> filters = new HashMap<String, BurstFilterConfig>();
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            String[] splitKey = entry.getKey().toString().split(Pattern.quote("."));
            String value = entry.getValue().toString();
            if (this.isInvalidKeyFormat(splitKey)) {
                log.debug("Invalid key format passed into burst filter instantiator: {}", (Object)this.escapeStringForCrlfInjection(entry.getKey().toString()));
                continue;
            }
            String filterId = splitKey[1];
            if (splitKey.length == 3) {
                String option = splitKey[2];
                this.parsePropAsOption(filters, option, value, filterId);
            }
            if (splitKey.length != 2) continue;
            this.parsePropAsLoggerName(filters, value, filterId);
        }
        return filters;
    }

    private void parsePropAsLoggerName(HashMap<String, BurstFilterConfig> filters, String value, String filterId) {
        if (!filters.containsKey(filterId)) {
            filters.put(filterId, new BurstFilterConfig());
        }
        BurstFilterConfig filter = filters.get(filterId);
        filter.setLoggerName(value);
    }

    private void parsePropAsOption(HashMap<String, BurstFilterConfig> filters, String option, String value, String filterId) {
        if (!filters.containsKey(filterId)) {
            filters.put(filterId, new BurstFilterConfig());
        }
        BurstFilterConfig config = filters.get(filterId);
        if (option.equals(BURST_FILTER_MAX_BURST_KEY)) {
            this.parseMaxBurst(value, config);
        } else if (option.equals(BURST_FILTER_RATE_KEY)) {
            this.parseRate(value, config);
        } else if (option.equals(BURST_FILTER_LEVEL_KEY)) {
            config.setLevel(Level.toLevel((String)value));
        }
    }

    private void parseRate(String value, BurstFilterConfig config) {
        try {
            config.setRate(Float.valueOf(Float.parseFloat(value)));
        }
        catch (NumberFormatException ex) {
            config.setRate(Float.valueOf(0.0f));
            log.warn("Invalid rate value {} used, using default log4j2 value", (Object)this.escapeStringForCrlfInjection(value));
        }
    }

    private void parseMaxBurst(String value, BurstFilterConfig config) {
        try {
            config.setMaxBurst(Long.parseLong(value));
        }
        catch (NumberFormatException ex) {
            config.setMaxBurst(0L);
            log.warn("Invalid max burst value {} used, using default log4j2 value", (Object)this.escapeStringForCrlfInjection(value));
        }
    }

    private boolean isInvalidKeyFormat(String[] splitKey) {
        return splitKey.length < 2 || splitKey.length > 3 || !splitKey[0].equals(BURST_FILTER_PREFIX);
    }

    private String escapeStringForCrlfInjection(String s) {
        return s.replaceAll("[\r\n]", "");
    }

    static class BurstFilterConfig {
        private String loggerName;
        private Long maxBurst;
        private Float rate;
        private Level level;

        BurstFilterConfig() {
        }

        public String getLoggerName() {
            return this.loggerName;
        }

        public Long getMaxBurst() {
            return this.maxBurst;
        }

        public Float getRate() {
            return this.rate;
        }

        public Level getLevel() {
            return this.level;
        }

        public void setLoggerName(String loggerName) {
            this.loggerName = loggerName;
        }

        public void setMaxBurst(Long maxBurst) {
            this.maxBurst = maxBurst;
        }

        public void setRate(Float rate) {
            this.rate = rate;
        }

        public void setLevel(Level level) {
            this.level = level;
        }
    }
}

