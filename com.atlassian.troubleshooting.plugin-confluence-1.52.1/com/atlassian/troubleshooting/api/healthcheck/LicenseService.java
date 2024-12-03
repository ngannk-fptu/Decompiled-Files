/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseType
 *  com.atlassian.extras.api.ProductLicense
 *  javax.annotation.Nullable
 */
package com.atlassian.troubleshooting.api.healthcheck;

import com.atlassian.extras.api.LicenseType;
import com.atlassian.extras.api.ProductLicense;
import java.util.Date;
import javax.annotation.Nullable;

public interface LicenseService {
    public static boolean isStarterLicense(@Nullable ProductLicense productLicense, String productNamespace) {
        if (productLicense == null || LicenseType.STARTER.equals((Object)productLicense.getLicenseType())) {
            return true;
        }
        return Boolean.parseBoolean(productLicense.getProperty(String.format("%s.Starter", productNamespace)));
    }

    public boolean isEvaluation();

    public boolean userCanRequestTechnicalSupport();

    public boolean isWithinMaintenanceFor(Date var1);

    public boolean isLicensedForDataCenter();

    public boolean isExpired();
}

