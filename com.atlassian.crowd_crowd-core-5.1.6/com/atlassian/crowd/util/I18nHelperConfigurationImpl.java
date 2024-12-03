/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.util.I18nHelperConfiguration
 */
package com.atlassian.crowd.util;

import com.atlassian.crowd.util.I18nHelperConfiguration;
import java.util.List;
import java.util.Locale;

public class I18nHelperConfigurationImpl
implements I18nHelperConfiguration {
    private final Locale locale;
    private final List<String> bundleLocations;

    public I18nHelperConfigurationImpl(Locale locale, List<String> bundleLocations) {
        this.locale = locale;
        this.bundleLocations = bundleLocations;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public List<String> getBundleLocations() {
        return this.bundleLocations;
    }
}

