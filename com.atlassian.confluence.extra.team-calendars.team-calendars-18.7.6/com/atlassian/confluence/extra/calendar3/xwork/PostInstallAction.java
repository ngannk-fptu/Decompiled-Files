/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 */
package com.atlassian.confluence.extra.calendar3.xwork;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.extra.calendar3.license.LicenseAccessor;
import java.util.List;

public class PostInstallAction
extends ConfluenceActionSupport {
    private LicenseAccessor licenseAccessor;

    protected List<String> getPermissionTypes() {
        List requiredPermissions = super.getPermissionTypes();
        requiredPermissions.add("ADMINISTRATECONFLUENCE");
        return requiredPermissions;
    }

    public void setLicenseAccessor(LicenseAccessor licenseAccessor) {
        this.licenseAccessor = licenseAccessor;
    }

    public boolean isUnlicensed() {
        return this.licenseAccessor.isLicenseInvalidated();
    }
}

