/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.spi.application.IconizedType
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.application;

import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.spi.application.IconizedType;
import java.net.URI;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class IconUriResolver {
    private IconUriResolver() {
        throw new AssertionError((Object)("Do not instantiate " + this.getClass().getSimpleName()));
    }

    @Nullable
    public static URI resolveIconUri(@Nonnull ApplicationType type) {
        Objects.requireNonNull(type, "type");
        if (type instanceof IconizedType) {
            return ((IconizedType)type).getIconUri();
        }
        return type.getIconUrl();
    }

    @Nullable
    public static URI resolveIconUri(@Nonnull EntityType type) {
        Objects.requireNonNull(type, "type");
        if (type instanceof IconizedType) {
            return ((IconizedType)type).getIconUri();
        }
        return type.getIconUrl();
    }
}

