/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.checks;

import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import com.atlassian.troubleshooting.healthcheck.checks.FontManagerChecker;
import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;

public class FontHealthCheck
implements SupportHealthCheck {
    private final SupportHealthStatusBuilder builder;
    private final FontManagerChecker fontManagerChecker;

    @Autowired
    public FontHealthCheck(SupportHealthStatusBuilder supportHealthStatusBuilder, FontManagerChecker fontManagerChecker) {
        this.builder = supportHealthStatusBuilder;
        this.fontManagerChecker = fontManagerChecker;
    }

    @Override
    public boolean isNodeSpecific() {
        return true;
    }

    @Override
    public SupportHealthStatus check() {
        return this.fontManagerChecker.fontCheck() ? this.builder.ok(this, "healthcheck.font.pass", new Serializable[0]) : this.builder.warning(this, "healthcheck.font.warning", new Serializable[0]);
    }
}

