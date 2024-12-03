/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.troubleshooting.jfr.config;

import com.atlassian.troubleshooting.jfr.config.JfrConfigurationRegistry;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public enum JfrProperty {
    MAX_AGE("jfr.recording.max_age", true){
        private static final long MAX_AGE_LOWER_LIMIT = 600000L;
        private static final long MAX_AGE_UPPER_LIMIT = 432000000L;

        @Override
        boolean validate(String value) {
            return JfrProperty.isLongValueValid(value, 600000L, 432000000L);
        }
    }
    ,
    MAX_SIZE("jfr.recording.max_size", true){
        private static final long MAX_SIZE_LOWER_LIMIT = 0xA00000L;
        private static final long MAX_SIZE_UPPER_LIMIT = 0x80000000L;

        @Override
        boolean validate(String value) {
            return JfrProperty.isLongValueValid(value, 0xA00000L, 0x80000000L);
        }
    }
    ,
    THREAD_DUMP_INTERVAL("jfr.thread.dump.interval", true){
        private static final long THREAD_DUMP_INTERVAL_LOWER_LIMIT = 1000L;
        private static final long THREAD_DUMP_INTERVAL_UPPER_LIMIT = 600000L;

        @Override
        boolean validate(String value) {
            return JfrProperty.isLongValueValid(value, 1000L, 600000L);
        }
    }
    ,
    FILE_TO_REMAIN("jfr.recording.files_to_remain", false){

        @Override
        boolean validate(String value) {
            return JfrProperty.isPositiveInt(value);
        }
    }
    ,
    RECORDING_PATH("jfr.recording.recordings_path", false){

        @Override
        boolean validate(String value) {
            return true;
        }
    }
    ,
    THREAD_DUMP_PATH("jfr.recording.threaddumps_path", false){

        @Override
        boolean validate(String value) {
            return true;
        }
    }
    ,
    DUMP_CRON_EXPRESSION("jfr.recording.dump_cron_expression", false){

        @Override
        boolean validate(String value) {
            return true;
        }
    }
    ,
    JFR_TEMPLATE_PATH("jfr.configuration.template.path", true){

        @Override
        boolean validate(String value) {
            if (StringUtils.isBlank((CharSequence)value)) {
                return true;
            }
            try {
                JfrConfigurationRegistry.getConfiguration(Paths.get(value, new String[0]));
            }
            catch (Exception ignored) {
                return false;
            }
            return true;
        }
    };

    private boolean overridden;
    private final boolean mutable;
    private final String propertyName;

    private JfrProperty(String propertyName, boolean mutable) {
        this.propertyName = propertyName;
        this.mutable = mutable;
    }

    abstract boolean validate(String var1);

    public boolean isMutable() {
        return this.mutable;
    }

    public boolean isOverridden() {
        return this.overridden;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public void setOverridden(boolean overridden) {
        this.overridden = overridden;
    }

    public static Optional<JfrProperty> fromPropertyName(String propertyName) {
        return Arrays.stream(JfrProperty.values()).filter(property -> property.propertyName.equals(propertyName)).findFirst();
    }

    private static boolean isLongValueValid(String value, long minValue, long maxValue) {
        try {
            long number = Long.parseLong(value);
            return JfrProperty.isInRangeInclusive(number, minValue, maxValue);
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isPositiveInt(String value) {
        try {
            int number = Integer.parseInt(value);
            return number > 0;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isInRangeInclusive(long number, long minValue, long maxValue) {
        return number >= minValue && number <= maxValue;
    }
}

