/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.setup.BootstrapManager;

public class DefaultContextPathHolder
implements ContextPathHolder {
    private final BootstrapManager bootstrapManager;

    public DefaultContextPathHolder(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    @Override
    public String getContextPath() {
        return this.bootstrapManager.getWebAppContextPath();
    }
}

