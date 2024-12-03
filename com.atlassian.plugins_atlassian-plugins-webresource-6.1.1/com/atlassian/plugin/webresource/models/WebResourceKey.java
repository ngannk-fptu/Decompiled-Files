/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.models;

import com.atlassian.plugin.webresource.models.Requestable;
import javax.annotation.Nonnull;

public class WebResourceKey
extends Requestable {
    public WebResourceKey(@Nonnull String key) {
        super(key);
    }

    public String toString() {
        return String.format("<wr!%s>", this.getKey());
    }
}

