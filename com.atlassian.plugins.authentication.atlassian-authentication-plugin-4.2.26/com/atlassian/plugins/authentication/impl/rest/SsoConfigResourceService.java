/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.plugins.authentication.impl.rest;

import com.atlassian.plugins.authentication.api.config.ImmutableSsoConfig;
import com.atlassian.plugins.authentication.api.config.SsoConfig;
import com.atlassian.plugins.authentication.api.config.SsoConfigService;
import com.atlassian.plugins.authentication.impl.rest.model.SsoConfigEntity;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class SsoConfigResourceService {
    private final SsoConfigService configService;

    @Inject
    public SsoConfigResourceService(SsoConfigService configService) {
        this.configService = configService;
    }

    @Nonnull
    public SsoConfigEntity getConfig() {
        return new SsoConfigEntity(this.configService.getSsoConfig());
    }

    @Nonnull
    public SsoConfigEntity updateConfig(@Nonnull SsoConfigEntity entity) {
        ImmutableSsoConfig configToStore = this.buildConfig(entity);
        SsoConfig updatedConfig = this.configService.updateSsoConfig(configToStore);
        return new SsoConfigEntity(updatedConfig);
    }

    private ImmutableSsoConfig buildConfig(SsoConfigEntity entity) {
        SsoConfig currentConfig = this.configService.getSsoConfig();
        ImmutableSsoConfig.Builder configBuilder = ImmutableSsoConfig.toBuilder(currentConfig);
        this.updateGenericConfig(configBuilder, entity);
        return configBuilder.build();
    }

    private void updateGenericConfig(@Nonnull ImmutableSsoConfig.Builder builder, @Nonnull SsoConfigEntity entity) {
        this.setIfNonNull(entity.getShowLoginForm(), builder::setShowLoginForm);
        this.setIfNonNull(entity.getDiscoveryRefreshCron(), builder::setDiscoveryRefreshCron);
        this.setIfNonNull(entity.getEnableAuthenticationFallback(), builder::setEnableAuthenticationFallback);
        this.setIfNonNull(entity.getShowLoginFormForJsm(), builder::setShowLoginFormForJsm);
    }

    private <T> void setIfNonNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }
}

