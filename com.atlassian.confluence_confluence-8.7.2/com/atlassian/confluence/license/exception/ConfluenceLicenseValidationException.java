/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 */
package com.atlassian.confluence.license.exception;

import com.atlassian.confluence.license.exception.LicenseValidationException;
import com.atlassian.extras.api.confluence.ConfluenceLicense;

public class ConfluenceLicenseValidationException
extends LicenseValidationException {
    private final ConfluenceLicense license;

    public ConfluenceLicenseValidationException(String message, ConfluenceLicense license) {
        super(message);
        this.license = license;
    }

    public ConfluenceLicense getLicense() {
        return this.license;
    }
}

