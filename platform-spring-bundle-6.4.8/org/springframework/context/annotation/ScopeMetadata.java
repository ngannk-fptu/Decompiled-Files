/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.util.Assert;

public class ScopeMetadata {
    private String scopeName = "singleton";
    private ScopedProxyMode scopedProxyMode = ScopedProxyMode.NO;

    public void setScopeName(String scopeName) {
        Assert.notNull((Object)scopeName, "'scopeName' must not be null");
        this.scopeName = scopeName;
    }

    public String getScopeName() {
        return this.scopeName;
    }

    public void setScopedProxyMode(ScopedProxyMode scopedProxyMode) {
        Assert.notNull((Object)scopedProxyMode, "'scopedProxyMode' must not be null");
        this.scopedProxyMode = scopedProxyMode;
    }

    public ScopedProxyMode getScopedProxyMode() {
        return this.scopedProxyMode;
    }
}

