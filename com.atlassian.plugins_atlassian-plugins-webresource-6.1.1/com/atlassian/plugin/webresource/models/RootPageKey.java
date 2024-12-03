/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.models;

import com.atlassian.plugin.webresource.models.WebResourceKey;
import javax.annotation.Nonnull;

public class RootPageKey
extends WebResourceKey {
    public RootPageKey(@Nonnull String key) {
        super(key);
    }

    @Override
    public String toString() {
        return String.format("<root-page!%s>", this.getKey());
    }
}

