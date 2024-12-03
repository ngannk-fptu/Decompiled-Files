/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugins.authentication.impl.config;

import com.atlassian.plugins.authentication.api.config.SsoConfig;
import com.atlassian.plugins.authentication.api.config.ValidationError;
import com.google.common.collect.Multimap;
import javax.annotation.Nonnull;

public interface SsoConfigValidator {
    @Nonnull
    public Multimap<String, ValidationError> validate(@Nonnull SsoConfig var1);
}

