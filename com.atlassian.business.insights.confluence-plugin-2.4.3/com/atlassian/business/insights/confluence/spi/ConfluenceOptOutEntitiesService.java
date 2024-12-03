/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.filter.OptOutEntitiesLookupService
 *  com.atlassian.business.insights.api.filter.OptOutEntitiesTransformationService
 *  com.atlassian.business.insights.api.filter.OptOutEntity
 *  com.atlassian.business.insights.api.filter.OptOutEntityIdentifier
 *  com.atlassian.business.insights.api.filter.OptOutEntityType
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.ContentSearch
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchManager$EntityVersionPolicy
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.ContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.InSpaceQuery
 *  com.atlassian.confluence.spaces.SpaceDescription
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.business.insights.confluence.spi;

import com.atlassian.business.insights.api.filter.OptOutEntitiesLookupService;
import com.atlassian.business.insights.api.filter.OptOutEntitiesTransformationService;
import com.atlassian.business.insights.api.filter.OptOutEntity;
import com.atlassian.business.insights.api.filter.OptOutEntityIdentifier;
import com.atlassian.business.insights.api.filter.OptOutEntityType;
import com.atlassian.business.insights.confluence.prefetch.EntityPrefetchProvider;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceOptOutEntitiesService
implements OptOutEntitiesLookupService,
OptOutEntitiesTransformationService {
    private final Logger log = LoggerFactory.getLogger(ConfluenceOptOutEntitiesService.class);
    private final ApplicationProperties applicationProperties;
    private final SearchManager searchManager;
    private final EntityPrefetchProvider entityPrefetchProvider;

    public ConfluenceOptOutEntitiesService(@Nonnull ApplicationProperties applicationProperties, @Nonnull SearchManager searchManager, @Nonnull EntityPrefetchProvider entityPrefetchProvider) {
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.searchManager = Objects.requireNonNull(searchManager, "searchManager");
        this.entityPrefetchProvider = Objects.requireNonNull(entityPrefetchProvider, "entityPrefetchProvider");
    }

    @Nonnull
    public Optional<OptOutEntity> lookupEntity(@Nonnull OptOutEntityType entityType, @Nonnull String entityKey) {
        Objects.requireNonNull(entityType, "entityType");
        Objects.requireNonNull(entityKey, "entityKey");
        if (entityType != OptOutEntityType.SPACE) {
            return Optional.empty();
        }
        return this.getSpaceByKey(entityKey).map(space -> OptOutEntity.builder((OptOutEntityType)OptOutEntityType.SPACE, (String)space.getSpaceKey()).displayName(space.getDisplayTitle()).key(space.getSpaceKey()).uri(this.spaceUrl((SpaceDescription)space)).build());
    }

    @Nonnull
    public Set<OptOutEntityType> getSupportedEntityTypes() {
        return Collections.singleton(OptOutEntityType.SPACE);
    }

    @Nonnull
    public List<OptOutEntity> transform(@Nonnull List<OptOutEntityIdentifier> optOutEntityIdentifiers) {
        return optOutEntityIdentifiers.stream().filter(optOutResourceIdentifier -> OptOutEntityType.SPACE == optOutResourceIdentifier.getType()).map(optOutResourceIdentifier -> this.getSpaceByKey(optOutResourceIdentifier.getIdentifier()).map(space -> OptOutEntity.builder((OptOutEntityType)OptOutEntityType.SPACE, (String)space.getSpaceKey()).displayName(space.getDisplayTitle()).key(space.getSpaceKey()).uri(this.spaceUrl((SpaceDescription)space)).build()).orElse(OptOutEntity.builder((OptOutEntityIdentifier)optOutResourceIdentifier).build())).collect(Collectors.toList());
    }

    private String spaceUrl(SpaceDescription space) {
        return this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + space.getUrlPath();
    }

    private Optional<SpaceDescription> getSpaceByKey(String spaceKey) {
        try {
            ContentSearch search = this.entityPrefetchProvider.contentSearch((SearchQuery)BooleanQuery.builder().addMust((Object)new InSpaceQuery(spaceKey)).addMust((Object)new ContentTypeQuery((Collection)ImmutableList.of((Object)ContentTypeEnum.SPACE_DESCRIPTION, (Object)ContentTypeEnum.PERSONAL_SPACE_DESCRIPTION))).build(), null, 0, 1);
            List spaceEntities = this.searchManager.searchEntities((ISearch)search, SearchManager.EntityVersionPolicy.INDEXED_VERSION);
            if (spaceEntities.size() == 1 && spaceEntities.get(0) instanceof SpaceDescription) {
                return Optional.of((SpaceDescription)spaceEntities.get(0));
            }
        }
        catch (InvalidSearchException e) {
            this.log.error("Fail to search space by key {} ", (Object)spaceKey, (Object)e);
        }
        return Optional.empty();
    }
}

