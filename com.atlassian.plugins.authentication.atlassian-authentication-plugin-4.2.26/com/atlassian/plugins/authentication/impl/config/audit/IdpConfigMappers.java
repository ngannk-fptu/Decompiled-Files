/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.ChangedValue
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.plugins.authentication.impl.config.audit;

import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.impl.config.audit.CommonIdpConfigMapper;
import com.atlassian.plugins.authentication.impl.config.audit.IdpConfigMapper;
import com.atlassian.plugins.authentication.impl.config.audit.JustInTimeConfigMapper;
import com.atlassian.plugins.authentication.impl.config.audit.OidcConfigMapper;
import com.atlassian.plugins.authentication.impl.config.audit.SamlConfigMapper;
import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class IdpConfigMappers {
    private final List<IdpConfigMapper> configMappers;

    @Inject
    public IdpConfigMappers(CommonIdpConfigMapper commonIdpConfigMapper, SamlConfigMapper samlConfigMapper, OidcConfigMapper oidcConfigMapper, JustInTimeConfigMapper justInTimeConfigMapper) {
        this.configMappers = ImmutableList.of((Object)commonIdpConfigMapper, (Object)samlConfigMapper, (Object)oidcConfigMapper, (Object)justInTimeConfigMapper);
    }

    public List<ChangedValue> mapChanges(@Nullable IdpConfig oldConfig, @Nullable IdpConfig newConfig) {
        if (oldConfig == null && newConfig == null) {
            throw new IllegalArgumentException("Can't compare two null values, one of them has to be nonnull");
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        for (IdpConfigMapper configMapper : this.configMappers) {
            builder.addAll(configMapper.mapChanges(oldConfig, newConfig));
        }
        return builder.build();
    }
}

