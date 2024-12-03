/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.license.exception;

import com.atlassian.confluence.license.exception.LicenseValidationException;

public class EmptyLicenseValidationException
extends LicenseValidationException {
    public EmptyLicenseValidationException() {
        super("The given license string was empty.");
    }
}

