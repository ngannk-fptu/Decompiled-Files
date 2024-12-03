/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.tx.Transactional
 *  com.atlassian.annotations.Internal
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugins.authentication.api.config;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.annotations.Internal;
import com.atlassian.plugins.authentication.api.config.SsoConfig;
import javax.annotation.Nonnull;

@Transactional
@Internal
public interface SsoConfigService {
    public SsoConfig getSsoConfig();

    public SsoConfig updateSsoConfig(@Nonnull SsoConfig var1);

    public void resetConfig();
}

