/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.healthcheck.checks.vuln;

import com.atlassian.troubleshooting.healthcheck.checks.vuln.CveExternalResource;
import java.util.Optional;

public interface CveProvider {
    public CveExternalResource getResource();

    public Optional<String> getCpeVersion();
}

