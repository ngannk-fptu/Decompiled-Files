/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.rest.applink.data;

import com.atlassian.applinks.internal.rest.applink.data.RestApplinkDataProvider;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;

public abstract class AbstractRestApplinkDataProvider
implements RestApplinkDataProvider {
    protected final Set<String> supportedKeys;

    protected AbstractRestApplinkDataProvider(@Nonnull Set<String> supportedKeys) {
        this.supportedKeys = Objects.requireNonNull(supportedKeys, "supportedKeys");
    }

    @Override
    @Nonnull
    public final Set<String> getSupportedKeys() {
        return this.supportedKeys;
    }
}

