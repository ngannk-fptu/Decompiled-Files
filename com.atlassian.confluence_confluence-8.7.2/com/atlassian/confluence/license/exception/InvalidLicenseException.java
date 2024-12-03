/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.license.exception;

import com.atlassian.confluence.license.exception.LicenseException;

public class InvalidLicenseException
extends LicenseException {
    private final String licenseString;

    public InvalidLicenseException(String licenseString) {
        this(licenseString, null);
    }

    public InvalidLicenseException(String licenseString, Throwable cause) {
        super(String.format("Unable to decode the license string [%s]%s.", licenseString, cause == null ? "" : ", see cause"), cause);
        this.licenseString = null;
    }

    public String getLicenseString() {
        return this.licenseString;
    }
}

