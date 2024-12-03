/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.license;

import com.atlassian.license.LicenseException;
import com.atlassian.license.LicenseRegistry;
import com.atlassian.license.LicenseType;
import java.util.Collection;

@Deprecated
public abstract class AbstractLicenseRegistry
implements LicenseRegistry {
    protected abstract Collection getAllLicenseTypes();

    public LicenseType getLicenseType(String type) throws LicenseException {
        for (LicenseType licenseType : this.getAllLicenseTypes()) {
            String licenseTypeDesc = licenseType.getDescription().toLowerCase();
            if (licenseTypeDesc.indexOf(type.toLowerCase()) == -1) continue;
            return licenseType;
        }
        throw new LicenseException("The license type (" + type + ") specified is invalid.");
    }

    public LicenseType getLicenseType(int type) throws LicenseException {
        for (LicenseType licenseType : this.getAllLicenseTypes()) {
            if (licenseType.getType() != type) continue;
            return licenseType;
        }
        throw new LicenseException("The license type (" + type + ") specified is invalid.");
    }
}

