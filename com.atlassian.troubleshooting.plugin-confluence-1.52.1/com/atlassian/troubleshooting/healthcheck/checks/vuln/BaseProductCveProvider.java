/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.healthcheck.checks.vuln;

import com.atlassian.troubleshooting.healthcheck.checks.vuln.CveExternalResource;
import com.atlassian.troubleshooting.healthcheck.checks.vuln.CveProvider;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import java.util.Objects;
import java.util.Optional;

public class BaseProductCveProvider
implements CveProvider {
    private CveExternalResource resource;
    private SupportApplicationInfo info;

    public BaseProductCveProvider(CveExternalResource resource, SupportApplicationInfo info) {
        this.resource = Objects.requireNonNull(resource);
        this.info = Objects.requireNonNull(info);
    }

    @Override
    public CveExternalResource getResource() {
        return this.resource;
    }

    @Override
    public Optional<String> getCpeVersion() {
        return Optional.of(this.info.getApplicationVersion());
    }
}

