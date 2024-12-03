/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.license.LicenseHandler
 */
package com.atlassian.upm.license.internal.host;

import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.license.internal.HostApplicationLicense;
import com.atlassian.upm.license.internal.HostApplicationLicenseFactory;
import com.atlassian.upm.license.internal.host.AbstractHostLicenseProvider;

public class RefappHostLicenseProvider
extends AbstractHostLicenseProvider {
    public RefappHostLicenseProvider(LicenseHandler licenseHandler, HostApplicationLicenseFactory hostApplicationLicenseFactory, UpmAppManager appManager) {
        super(licenseHandler, hostApplicationLicenseFactory, appManager);
    }

    @Override
    protected Option<HostApplicationLicense> getSingleHostLicenseInternal() {
        return Option.none();
    }
}

