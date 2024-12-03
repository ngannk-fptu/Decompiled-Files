/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.license;

import com.atlassian.analytics.client.exception.NoLicenseException;
import java.util.Date;

public interface LicenseProvider {
    public Date getLicenseCreationDate() throws NoLicenseException;
}

