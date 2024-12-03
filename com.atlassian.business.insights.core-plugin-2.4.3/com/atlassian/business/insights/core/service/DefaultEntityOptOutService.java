/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.business.insights.api.filter.OptOutEntitiesLookupService
 *  com.atlassian.business.insights.api.filter.OptOutEntitiesTransformationService
 *  com.atlassian.business.insights.api.filter.OptOutEntity
 *  com.atlassian.business.insights.api.filter.OptOutEntityIdentifier
 *  com.atlassian.business.insights.api.filter.OptOutEntityType
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.service;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.api.filter.OptOutEntitiesLookupService;
import com.atlassian.business.insights.api.filter.OptOutEntitiesTransformationService;
import com.atlassian.business.insights.api.filter.OptOutEntity;
import com.atlassian.business.insights.api.filter.OptOutEntityIdentifier;
import com.atlassian.business.insights.api.filter.OptOutEntityType;
import com.atlassian.business.insights.core.ao.dao.AoEntityOptOutIdentifierDao;
import com.atlassian.business.insights.core.ao.dao.entity.AoEntityOptOutIdentifier;
import com.atlassian.business.insights.core.service.api.EntityOptOutService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class DefaultEntityOptOutService
implements EntityOptOutService {
    @VisibleForTesting
    static final int DEFAULT_PAGE_SIZE = 5000;
    private final AoEntityOptOutIdentifierDao aoEntityOptOutIdentifierDao;
    private final OptOutEntitiesLookupService lookupService;
    private final OptOutEntitiesTransformationService transformationService;

    public DefaultEntityOptOutService(AoEntityOptOutIdentifierDao aoEntityOptOutIdentifierDao, OptOutEntitiesLookupService lookupService, OptOutEntitiesTransformationService transformationService) {
        this.aoEntityOptOutIdentifierDao = aoEntityOptOutIdentifierDao;
        this.lookupService = lookupService;
        this.transformationService = transformationService;
    }

    @Override
    public void addEntityOptOuts(@Nonnull List<OptOutEntityIdentifier> optOutEntityIdentifiers) {
        Objects.requireNonNull(optOutEntityIdentifiers, "optOutEntityIdentifiers");
        if (optOutEntityIdentifiers.isEmpty()) {
            return;
        }
        this.aoEntityOptOutIdentifierDao.save(optOutEntityIdentifiers);
    }

    @Override
    @Nonnull
    public Optional<OptOutEntity> enrichOptOutEntity(@Nonnull OptOutEntityType optOutEntityType, @Nonnull String entityKey) {
        Objects.requireNonNull(optOutEntityType, "optOutEntityType");
        Objects.requireNonNull(entityKey, "entityKey");
        return this.lookupService.lookupEntity(optOutEntityType, entityKey);
    }

    @Override
    @Nonnull
    public List<OptOutEntityIdentifier> getOptedOutEntityIdentifiers() {
        ArrayList<AoEntityOptOutIdentifier> identifiers = new ArrayList<AoEntityOptOutIdentifier>();
        int offset = 0;
        List<AoEntityOptOutIdentifier> pagedIdentifiers = this.aoEntityOptOutIdentifierDao.get(offset, 5000);
        while (!pagedIdentifiers.isEmpty()) {
            identifiers.addAll(pagedIdentifiers);
            pagedIdentifiers = this.aoEntityOptOutIdentifierDao.get(offset += 5000, 5000);
        }
        return identifiers.stream().map(aoEntityOptOutIdentifier -> new OptOutEntityIdentifier(aoEntityOptOutIdentifier.getResourceType(), aoEntityOptOutIdentifier.getResourceIdentifier())).collect(Collectors.toList());
    }

    @Override
    @Nonnull
    public List<OptOutEntity> getOptOutEntities() {
        return this.transformationService.transform(this.getOptedOutEntityIdentifiers());
    }

    @Override
    @Nonnull
    public Set<OptOutEntityType> getSupportedEntityTypes() {
        return this.lookupService.getSupportedEntityTypes();
    }

    @Override
    public void removeEntityOptOuts(@Nonnull List<OptOutEntityIdentifier> optOutEntityIdentifiers) {
        Objects.requireNonNull(optOutEntityIdentifiers, "optOutEntityIdentifiers");
        this.aoEntityOptOutIdentifierDao.delete(optOutEntityIdentifiers);
    }
}

