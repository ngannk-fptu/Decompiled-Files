/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.ProductLicense
 */
package com.atlassian.confluence.license.exception;

import com.atlassian.confluence.license.exception.LicenseValidationException;
import com.atlassian.extras.api.ProductLicense;

public class MissingConfluenceLicenseValidationException
extends LicenseValidationException {
    private final Iterable<ProductLicense> otherProductLicenses;

    public MissingConfluenceLicenseValidationException(Iterable<ProductLicense> otherProductLicenses) {
        super("The given license is missing Confluence.");
        this.otherProductLicenses = otherProductLicenses;
    }

    public Iterable<ProductLicense> getOtherProductLicenses() {
        return this.otherProductLicenses;
    }
}

