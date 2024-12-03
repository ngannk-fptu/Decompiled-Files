/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.business.insights.api.config.PropertiesProvider
 *  com.atlassian.business.insights.api.filter.OptOutEntity
 *  com.atlassian.business.insights.api.filter.OptOutEntityIdentifier
 *  com.atlassian.business.insights.api.filter.OptOutEntityType
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.service;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.api.config.PropertiesProvider;
import com.atlassian.business.insights.api.filter.OptOutEntity;
import com.atlassian.business.insights.api.filter.OptOutEntityIdentifier;
import com.atlassian.business.insights.api.filter.OptOutEntityType;
import com.atlassian.business.insights.core.service.api.EntityOptOutService;
import com.atlassian.business.insights.core.service.exception.EntityOptOutExceedCountLimitException;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public class CountLimitedEntityOptOutService
implements EntityOptOutService {
    @VisibleForTesting
    static final String MAX_OPT_OUT_ENTITIES_KEY = "plugin.data.pipeline.optout.count.limit";
    @VisibleForTesting
    static final String MAX_OPT_OUT_ENTITIES_DEFAULT = "1000";
    private static final String MAX_OPT_OUT_ENTITIES_EXCEEDS_LIMIT_MESSAGE_KEY = "data-pipeline.api.rest.config.optout.count.exceeds.limit";
    private final EntityOptOutService delegate;
    private final PropertiesProvider propertiesProvider;
    private final I18nResolver i18nResolver;

    public CountLimitedEntityOptOutService(@Nonnull EntityOptOutService entityOptOutService, @Nonnull PropertiesProvider propertiesProvider, @Nonnull I18nResolver i18nResolver) {
        this.delegate = Objects.requireNonNull(entityOptOutService);
        this.propertiesProvider = Objects.requireNonNull(propertiesProvider);
        this.i18nResolver = Objects.requireNonNull(i18nResolver);
    }

    @Override
    public void addEntityOptOuts(@Nonnull List<OptOutEntityIdentifier> entityOptOuts) {
        Objects.requireNonNull(entityOptOuts, "entityOptOuts");
        List<OptOutEntityIdentifier> existingIdentifiers = this.delegate.getOptedOutEntityIdentifiers();
        int totalCountAfterAdd = Stream.concat(existingIdentifiers.stream(), entityOptOuts.stream()).collect(Collectors.toSet()).size();
        int maxCountAllowed = Integer.parseInt(this.propertiesProvider.getProperty(MAX_OPT_OUT_ENTITIES_KEY, MAX_OPT_OUT_ENTITIES_DEFAULT));
        if (totalCountAfterAdd > maxCountAllowed) {
            throw new EntityOptOutExceedCountLimitException(this.i18nResolver.getText(MAX_OPT_OUT_ENTITIES_EXCEEDS_LIMIT_MESSAGE_KEY, new Serializable[]{Integer.valueOf(totalCountAfterAdd), Integer.valueOf(maxCountAllowed)}));
        }
        this.delegate.addEntityOptOuts(entityOptOuts);
    }

    @Override
    @Nonnull
    public Optional<OptOutEntity> enrichOptOutEntity(@Nonnull OptOutEntityType optOutEntityType, @Nonnull String entityKey) {
        return this.delegate.enrichOptOutEntity(optOutEntityType, entityKey);
    }

    @Override
    @Nonnull
    public List<OptOutEntityIdentifier> getOptedOutEntityIdentifiers() {
        return this.delegate.getOptedOutEntityIdentifiers();
    }

    @Override
    @Nonnull
    public List<OptOutEntity> getOptOutEntities() {
        return this.delegate.getOptOutEntities();
    }

    @Override
    @Nonnull
    public Set<OptOutEntityType> getSupportedEntityTypes() {
        return this.delegate.getSupportedEntityTypes();
    }

    @Override
    public void removeEntityOptOuts(@Nonnull List<OptOutEntityIdentifier> entityOptOuts) {
        this.delegate.removeEntityOptOuts(entityOptOuts);
    }
}

