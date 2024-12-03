/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.license.internal;

import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Either;
import com.atlassian.upm.license.internal.PluginLicenseDowngradeError;
import com.atlassian.upm.license.internal.PluginLicenseError;

public interface PluginLicenseValidator {
    public Either<PluginLicenseError, PluginLicense> validate(String var1, String var2);

    public Iterable<PluginLicenseDowngradeError> validateDowngrade(PluginLicense var1, PluginLicense var2);
}

