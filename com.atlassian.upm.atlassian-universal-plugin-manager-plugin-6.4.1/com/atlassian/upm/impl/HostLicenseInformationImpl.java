/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.impl;

import com.atlassian.upm.api.license.HostLicenseInformation;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.HostLicenseProvider;
import com.atlassian.upm.test.rest.resources.UpmSysResource;
import java.util.Objects;

public class HostLicenseInformationImpl
implements HostLicenseInformation {
    private final HostLicenseProvider hostLicenseProvider;

    public HostLicenseInformationImpl(HostLicenseProvider hostLicenseProvider) {
        this.hostLicenseProvider = Objects.requireNonNull(hostLicenseProvider, "hostLicenseProvider");
    }

    @Override
    public Option<String> hostSen() {
        return UpmSysResource.getSen().getOrElse(this.hostLicenseProvider.getHostApplicationLicenseAttributes().getSen());
    }

    @Override
    public boolean isDataCenter() {
        return this.hostLicenseProvider.getHostApplicationLicenseAttributes().isDataCenter();
    }

    @Override
    public Option<Integer> getEdition() {
        return this.hostLicenseProvider.getHostApplicationLicenseAttributes().getEdition();
    }

    @Override
    public boolean isEvaluation() {
        return this.hostLicenseProvider.getHostApplicationLicenseAttributes().isEvaluation();
    }
}

