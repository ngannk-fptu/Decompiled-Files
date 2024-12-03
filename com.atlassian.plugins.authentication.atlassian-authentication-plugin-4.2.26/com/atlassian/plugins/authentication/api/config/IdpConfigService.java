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
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpSearchParameters;
import java.util.List;
import javax.annotation.Nonnull;

@Transactional
@Internal
public interface IdpConfigService {
    public List<IdpConfig> getIdpConfigs();

    public List<IdpConfig> getIdpConfigs(IdpSearchParameters var1);

    public IdpConfig getIdpConfig(Long var1);

    public IdpConfig updateIdpConfig(@Nonnull IdpConfig var1);

    public IdpConfig addIdpConfig(@Nonnull IdpConfig var1);

    public IdpConfig removeIdpConfig(Long var1);

    public IdpConfig refreshIdpConfig(IdpConfig var1);
}

