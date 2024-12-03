/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.license;

import com.atlassian.license.LicenseException;
import com.atlassian.license.LicenseType;
import java.util.ArrayList;
import java.util.Collection;

@Deprecated
public abstract class LicenseTypeStore {
    protected ArrayList applicationLicenseTypes = new ArrayList();

    public abstract String getPublicKeyFileName();

    public abstract String getPrivateKeyFileName();

    public LicenseType getLicenseType(String licenseTypeString) throws LicenseException {
        if (licenseTypeString == null || "".equals(licenseTypeString)) {
            throw new LicenseException("License description must be specified; you used [" + licenseTypeString + "]");
        }
        for (LicenseType licenseType : this.applicationLicenseTypes) {
            if (licenseType.getDescription() == null || "".equals(licenseType.getDescription())) {
                throw new LicenseException("License type added with an invalid description; you used [" + licenseType.getDescription() + "]");
            }
            String licenseTypeDesc = licenseType.getDescription().toLowerCase();
            if (licenseTypeDesc.indexOf(licenseTypeString.toLowerCase()) == -1) continue;
            return licenseType;
        }
        throw new LicenseException("LicenseType not found with description [" + licenseTypeString + "]");
    }

    public LicenseType getLicenseType(int licenseCode) throws LicenseException {
        for (LicenseType licenseType : this.applicationLicenseTypes) {
            if (licenseType.getType() != licenseCode) continue;
            return licenseType;
        }
        throw new LicenseException("The license type (" + licenseCode + ") specified is invalid.");
    }

    public LicenseType lookupLicenseType(int licenseCode) {
        for (LicenseType licenseType : this.applicationLicenseTypes) {
            if (licenseType.getType() != licenseCode) continue;
            return licenseType;
        }
        return null;
    }

    public Collection getAllLicenses() {
        return this.applicationLicenseTypes;
    }
}

