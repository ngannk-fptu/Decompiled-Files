/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.authentication.api.config;

import com.atlassian.annotations.Internal;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Internal
public enum SsoType {
    NONE,
    SAML,
    OIDC;


    @Nonnull
    public static Optional<SsoType> fromName(@Nullable String value) {
        return Arrays.stream(SsoType.values()).filter(type -> Objects.equals(type.name(), value)).findFirst();
    }
}

