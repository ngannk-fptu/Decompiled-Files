/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.filter.OptOutEntity
 *  com.atlassian.business.insights.api.filter.OptOutEntityIdentifier
 *  com.atlassian.business.insights.api.filter.OptOutEntityType
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.service.api;

import com.atlassian.business.insights.api.filter.OptOutEntity;
import com.atlassian.business.insights.api.filter.OptOutEntityIdentifier;
import com.atlassian.business.insights.api.filter.OptOutEntityType;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;

public interface EntityOptOutService {
    public void addEntityOptOuts(@Nonnull List<OptOutEntityIdentifier> var1);

    @Nonnull
    public Optional<OptOutEntity> enrichOptOutEntity(@Nonnull OptOutEntityType var1, @Nonnull String var2);

    @Nonnull
    public List<OptOutEntityIdentifier> getOptedOutEntityIdentifiers();

    @Nonnull
    public List<OptOutEntity> getOptOutEntities();

    @Nonnull
    public Set<OptOutEntityType> getSupportedEntityTypes();

    public void removeEntityOptOuts(@Nonnull List<OptOutEntityIdentifier> var1);
}

