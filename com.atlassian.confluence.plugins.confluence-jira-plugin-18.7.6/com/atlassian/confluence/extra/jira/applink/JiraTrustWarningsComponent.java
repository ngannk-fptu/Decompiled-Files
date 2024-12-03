/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.StateAware
 */
package com.atlassian.confluence.extra.jira.applink;

import com.atlassian.confluence.extra.jira.api.services.TrustedApplicationConfig;
import com.atlassian.plugin.StateAware;

public class JiraTrustWarningsComponent
implements StateAware {
    private TrustedApplicationConfig trustedApplicationConfig;

    public void setTrustedApplicationConfig(TrustedApplicationConfig trustedApplicationConfig) {
        this.trustedApplicationConfig = trustedApplicationConfig;
    }

    public synchronized void enabled() {
        if (null != this.trustedApplicationConfig) {
            this.trustedApplicationConfig.setTrustWarningsEnabled(true);
        }
    }

    public synchronized void disabled() {
        if (null != this.trustedApplicationConfig) {
            this.trustedApplicationConfig.setTrustWarningsEnabled(false);
        }
    }
}

