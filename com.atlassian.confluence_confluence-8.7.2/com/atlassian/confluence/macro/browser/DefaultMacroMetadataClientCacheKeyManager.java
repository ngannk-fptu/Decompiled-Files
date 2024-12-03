/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.browser;

import com.atlassian.confluence.macro.browser.MacroMetadataClientCacheKeyManager;

public class DefaultMacroMetadataClientCacheKeyManager
implements MacroMetadataClientCacheKeyManager {
    private volatile String key;

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public void refreshKey() {
        this.key = String.valueOf(System.currentTimeMillis());
    }
}

