/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 */
package com.atlassian.confluence.core;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import java.util.HashMap;
import java.util.Map;

public class DefaultFormatSettingsManager
implements FormatSettingsManager {
    private static final BandanaContext BANDANA_CONTEXT = new ConfluenceBandanaContext();
    private static final String BANDANA_KEY = DefaultFormatSettingsManager.class.getName();
    private static final String DATE_FORMAT = "dateFormat";
    private static final String TIME_FORMAT = "timeFormat";
    private static final String DATE_TIME_FORMAT = "dateTimeFormat";
    private static final String LONG_NUMBER_FORMAT = "longNumberFormat";
    private static final String DECIMAL_NUMBER_FORMAT = "decimalNumberFormat";
    private BandanaManager bandanaManager;
    public static final String DEFAULT_DATE_PATTERN = "MMM dd, yyyy";
    public static final String DEFAULT_DATE_TIME_PATTERN = "MMM dd, yyyy HH:mm";
    public static final String BLOG_DATE_PATTERN = "MMM dd, yyyy";
    public static final String EDITOR_BLOG_DATE_PATTERN = "yyyy-MM-dd";
    public static final String EDITOR_BLOG_TIME_PATTERN = "HH:mm";
    public static final String DEFAULT_TIME_PATTERN = "h:mm a";
    public static final String DEFAULT_LONG_NUMBER_PATTERN = "###############";
    public static final String DEFAULT_DECIMAL_NUMBER_PATTERN = "###############.##########";

    private Map<String, String> retrieveSettings() {
        Object settings = this.bandanaManager.getValue(BANDANA_CONTEXT, BANDANA_KEY);
        if (settings == null || !(settings instanceof Map)) {
            return new HashMap<String, String>();
        }
        Map typedSettings = (Map)settings;
        return typedSettings;
    }

    private String getSetting(String settingName, String defaultValue) {
        Map<String, String> settings = this.retrieveSettings();
        String value = settings.get(settingName);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    private void saveSettings(Map settings) {
        this.bandanaManager.setValue(BANDANA_CONTEXT, BANDANA_KEY, (Object)settings);
    }

    private void setSetting(String settingName, String value) {
        Map<String, String> settings = this.retrieveSettings();
        settings.put(settingName, value);
        this.saveSettings(settings);
    }

    @Override
    public String getDateFormat() {
        return this.getSetting(DATE_FORMAT, "MMM dd, yyyy");
    }

    @Override
    public void setDateFormat(String pattern) {
        this.setSetting(DATE_FORMAT, pattern);
    }

    @Override
    public String getTimeFormat() {
        return this.getSetting(TIME_FORMAT, DEFAULT_TIME_PATTERN);
    }

    @Override
    public void setTimeFormat(String pattern) {
        this.setSetting(TIME_FORMAT, pattern);
    }

    @Override
    public String getDateTimeFormat() {
        return this.getSetting(DATE_TIME_FORMAT, DEFAULT_DATE_TIME_PATTERN);
    }

    @Override
    public void setDateTimeFormat(String pattern) {
        this.setSetting(DATE_TIME_FORMAT, pattern);
    }

    @Override
    public String getEditorBlogPostDate() {
        return EDITOR_BLOG_DATE_PATTERN;
    }

    @Override
    public String getEditorBlogPostTime() {
        return EDITOR_BLOG_TIME_PATTERN;
    }

    @Override
    public String getLongNumberFormat() {
        return this.getSetting(LONG_NUMBER_FORMAT, DEFAULT_LONG_NUMBER_PATTERN);
    }

    @Override
    public void setLongNumberFormat(String pattern) {
        this.setSetting(LONG_NUMBER_FORMAT, pattern);
    }

    @Override
    public String getDecimalNumberFormat() {
        return this.getSetting(DECIMAL_NUMBER_FORMAT, DEFAULT_DECIMAL_NUMBER_PATTERN);
    }

    @Override
    public void setDecimalNumberFormat(String pattern) {
        this.setSetting(DECIMAL_NUMBER_FORMAT, pattern);
    }

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }
}

