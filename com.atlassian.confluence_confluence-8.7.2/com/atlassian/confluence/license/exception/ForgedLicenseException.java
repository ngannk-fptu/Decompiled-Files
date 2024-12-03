/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.license.exception;

import com.atlassian.confluence.license.exception.LicenseException;

public class ForgedLicenseException
extends LicenseException {
    public ForgedLicenseException() {
        super("The given license is forged.");
    }
}

