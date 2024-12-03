/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.soy.renderer;

import com.atlassian.annotations.PublicApi;
import com.atlassian.soy.renderer.SanitizationType;
import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;

@PublicApi
public class SanitizedString {
    private final String value;
    private final SanitizationType type;

    public SanitizedString(@Nonnull String value) {
        this(value, SanitizationType.HTML);
    }

    public SanitizedString(@Nonnull String value, @Nonnull SanitizationType type) {
        this.value = (String)Preconditions.checkNotNull((Object)value, (Object)"value");
        this.type = (SanitizationType)((Object)Preconditions.checkNotNull((Object)((Object)type), (Object)"type"));
    }

    @Nonnull
    public SanitizationType getType() {
        return this.type;
    }

    @Nonnull
    public String getValue() {
        return this.value;
    }
}

