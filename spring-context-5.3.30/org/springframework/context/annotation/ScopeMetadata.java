/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.context.annotation;

import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.util.Assert;

public class ScopeMetadata {
    private String scopeName = "singleton";
    private ScopedProxyMode scopedProxyMode = ScopedProxyMode.NO;

    public void setScopeName(String scopeName) {
        Assert.notNull((Object)scopeName, (String)"'scopeName' must not be null");
        this.scopeName = scopeName;
    }

    public String getScopeName() {
        return this.scopeName;
    }

    public void setScopedProxyMode(ScopedProxyMode scopedProxyMode) {
        Assert.notNull((Object)((Object)scopedProxyMode), (String)"'scopedProxyMode' must not be null");
        this.scopedProxyMode = scopedProxyMode;
    }

    public ScopedProxyMode getScopedProxyMode() {
        return this.scopedProxyMode;
    }
}

