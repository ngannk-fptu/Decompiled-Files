/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.license;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LicenseInfo {
    private static final Logger log = LoggerFactory.getLogger(LicenseInfo.class);
    private final boolean valid;
    private final boolean hasDCFeatures;
    private String error = "";

    private LicenseInfo(String error) {
        this(false, false);
        this.error = error;
    }

    private LicenseInfo(boolean valid, boolean hasDCFeatures) {
        this.valid = valid;
        this.hasDCFeatures = hasDCFeatures;
    }

    public static LicenseInfo create(boolean valid, boolean hasDCFeatures) {
        log.trace("Create license with validity: {} dcFeatures: {}", (Object)valid, (Object)hasDCFeatures);
        return new LicenseInfo(valid, hasDCFeatures);
    }

    public static LicenseInfo invalid(Exception e) {
        return new LicenseInfo(e.getMessage());
    }

    public boolean isValid() {
        return this.valid;
    }

    public boolean isDCFeatureLicensed() {
        return this.hasDCFeatures;
    }

    public String getError() {
        return this.error;
    }
}

