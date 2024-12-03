/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.api;

import com.atlassian.annotations.PublicSpi;
import com.atlassian.applinks.api.ApplicationTypeVisitor;
import java.net.URI;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PublicSpi
public interface ApplicationType {
    @Nonnull
    public String getI18nKey();

    @Deprecated
    @Nullable
    public URI getIconUrl();

    @Nullable
    default public <T> T accept(@Nonnull ApplicationTypeVisitor<T> visitor) {
        return visitor.visitDefault(this);
    }
}

