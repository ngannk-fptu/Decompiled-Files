/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.jira.applink;

import com.atlassian.confluence.extra.jira.api.services.TrustedApplicationConfig;

public class DefaultTrustedApplicationConfig
implements TrustedApplicationConfig {
    private boolean trustWarningsEnabled;
    private boolean useTrustTokens;

    public DefaultTrustedApplicationConfig() {
        this.setTrustWarningsEnabled(true);
        this.setUseTrustTokens(true);
    }

    @Override
    public boolean isTrustWarningsEnabled() {
        return this.trustWarningsEnabled;
    }

    @Override
    public void setTrustWarningsEnabled(boolean trustWarningsEnabled) {
        this.trustWarningsEnabled = trustWarningsEnabled;
    }

    @Override
    public boolean isUseTrustTokens() {
        return this.useTrustTokens;
    }

    @Override
    public void setUseTrustTokens(boolean useTrustTokens) {
        this.useTrustTokens = useTrustTokens;
    }
}

