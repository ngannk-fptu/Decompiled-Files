/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bitbucket.license.LicenseService
 */
package com.atlassian.upm.core.impl;

import com.atlassian.bitbucket.license.LicenseService;
import com.atlassian.upm.core.HostApplicationDescriptor;
import java.util.Objects;

public class BitbucketApplicationDescriptor
implements HostApplicationDescriptor {
    private final LicenseService licenseService;

    public BitbucketApplicationDescriptor(LicenseService licenseService) {
        this.licenseService = Objects.requireNonNull(licenseService, "licenseService");
    }

    @Override
    public int getActiveEditionCount() {
        return this.getActiveUserCount();
    }

    @Override
    public int getActiveUserCount() {
        return this.licenseService.getLicensedUsersCount();
    }
}

