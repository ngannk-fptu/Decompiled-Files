/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.jira.api.services;

public interface TrustedApplicationConfig {
    public void setTrustWarningsEnabled(boolean var1);

    public void setUseTrustTokens(boolean var1);

    public boolean isTrustWarningsEnabled();

    public boolean isUseTrustTokens();
}

