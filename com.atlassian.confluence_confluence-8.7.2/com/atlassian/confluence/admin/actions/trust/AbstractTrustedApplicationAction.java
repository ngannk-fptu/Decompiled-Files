/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.admin.actions.trust;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.security.trust.TrustedApplicationsManager;

public abstract class AbstractTrustedApplicationAction
extends ConfluenceActionSupport {
    protected TrustedApplicationsManager trustedApplicationsManager;

    public void setTrustedApplicationsManager(TrustedApplicationsManager trustedApplicationsManager) {
        this.trustedApplicationsManager = trustedApplicationsManager;
    }
}

