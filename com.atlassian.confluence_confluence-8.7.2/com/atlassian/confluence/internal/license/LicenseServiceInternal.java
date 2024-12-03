/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.license;

import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.license.exception.LicenseException;

public interface LicenseServiceInternal
extends LicenseService {
    public boolean isLicensed() throws LicenseException;
}

