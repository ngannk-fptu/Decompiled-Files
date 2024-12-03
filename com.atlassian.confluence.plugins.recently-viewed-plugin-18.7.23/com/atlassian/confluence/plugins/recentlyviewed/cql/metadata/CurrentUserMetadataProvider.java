/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.extension.MetadataProperty
 *  com.atlassian.confluence.api.extension.ModelMetadataProvider
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.reference.ModelMapBuilder
 *  com.atlassian.confluence.api.service.people.PersonService
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.datetime.DateFormatterFactory
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  org.joda.time.DateTime
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.recentlyviewed.cql.metadata;

import com.atlassian.confluence.api.extension.MetadataProperty;
import com.atlassian.confluence.api.extension.ModelMetadataProvider;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.reference.ModelMapBuilder;
import com.atlassian.confluence.api.service.people.PersonService;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.datetime.DateFormatterFactory;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewed;
import com.atlassian.confluence.plugins.recentlyviewed.cql.metadata.ContributionStatusSummary;
import com.atlassian.confluence.plugins.recentlyviewed.cql.metadata.FavouritedSummary;
import com.atlassian.confluence.plugins.recentlyviewed.cql.metadata.LastModifiedSummary;
import com.atlassian.confluence.plugins.recentlyviewed.cql.metadata.RecentlyViewedSummary;
import com.atlassian.confluence.plugins.recentlyviewed.dao.RecentlyViewedDao;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Qualifier;

public class CurrentUserMetadataProvider
implements ModelMetadataProvider {
    private static final String CURRENT_USER_METADATA_KEY = "currentuser";
    private static final String FAVOURITED_METADATA_KEY = "favourited";
    private static final String LAST_MODIFIED_METADATA_KEY = "lastmodified";
    private static final String VIEWED_METADATA_KEY = "viewed";
    private static final String LAST_CONTRIBUTED_METADATA_KEY = "lastcontributed";
    private final RecentlyViewedDao recentlyViewedDao;
    private final DateFormatterFactory dateFormatterFactory;
    private final ContentEntityManager contentEntityManager;
    private final LabelManager labelManager;
    private final PersonService personService;
    private final I18NBeanFactory i18NBeanFactory;

    public CurrentUserMetadataProvider(RecentlyViewedDao recentlyViewedDao, @ComponentImport DateFormatterFactory dateFormatterFactory, @ComponentImport(value="contentEntityManager") @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, @ComponentImport LabelManager labelManager, @ComponentImport PersonService personService, @ComponentImport I18NBeanFactory i18NBeanFactory) {
        this.recentlyViewedDao = recentlyViewedDao;
        this.dateFormatterFactory = dateFormatterFactory;
        this.contentEntityManager = contentEntityManager;
        this.labelManager = labelManager;
        this.personService = personService;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    public Map<String, ?> getMetadata(Object entity, Expansions expansions) {
        if (AuthenticatedUserThreadLocal.isAnonymousUser()) {
            return Collections.emptyMap();
        }
        return this.getMetadataForAll((Iterable<Object>)Option.some((Object)entity), expansions).get(entity);
    }

    public Map<Object, Map<String, ?>> getMetadataForAll(Iterable<Object> entities, Expansions expansions) {
        if (AuthenticatedUserThreadLocal.isAnonymousUser()) {
            return Collections.emptyMap();
        }
        Expansions subExpansions = expansions.getSubExpansions(CURRENT_USER_METADATA_KEY);
        UserKey currentUserKey = AuthenticatedUserThreadLocal.get().getKey();
        Collection contentIds = StreamSupport.stream(entities.spliterator(), false).map(item -> {
            if (item instanceof Content) {
                Content content = (Content)item;
                return content.getId();
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        Map<Long, RecentlyViewedSummary> recentlyViewedSummaryByContentIdMap = null;
        if (subExpansions.canExpand(VIEWED_METADATA_KEY)) {
            recentlyViewedSummaryByContentIdMap = this.computeRecentlyViewedSummaryMap(currentUserKey, contentIds);
        }
        Map<Long, LastModifiedSummary> lastModifiedSummaryMap = null;
        if (subExpansions.canExpand(LAST_MODIFIED_METADATA_KEY)) {
            lastModifiedSummaryMap = this.computeLastModifiedSummaryMap(currentUserKey, contentIds);
        }
        Map<Long, FavouritedSummary> favouritedSummaryMap = null;
        if (subExpansions.canExpand(FAVOURITED_METADATA_KEY)) {
            favouritedSummaryMap = this.computeFavouritedSummaryMap(currentUserKey, contentIds);
        }
        Map<Long, ContributionStatusSummary> contributionStatusSummaryMap = null;
        if (subExpansions.canExpand(LAST_CONTRIBUTED_METADATA_KEY)) {
            contributionStatusSummaryMap = this.computeContributionStatusSummaryMap(currentUserKey, contentIds);
        }
        ModelMapBuilder result = ModelMapBuilder.newInstance();
        for (Object entity : entities) {
            if (!(entity instanceof Content)) continue;
            ModelMapBuilder metadataMap = ModelMapBuilder.newInstance();
            this.populateMap(result, (ModelMapBuilder<String, Object>)metadataMap, VIEWED_METADATA_KEY, recentlyViewedSummaryByContentIdMap, entity);
            this.populateMap(result, (ModelMapBuilder<String, Object>)metadataMap, LAST_MODIFIED_METADATA_KEY, lastModifiedSummaryMap, entity);
            this.populateMap(result, (ModelMapBuilder<String, Object>)metadataMap, FAVOURITED_METADATA_KEY, favouritedSummaryMap, entity);
            this.populateMap(result, (ModelMapBuilder<String, Object>)metadataMap, LAST_CONTRIBUTED_METADATA_KEY, contributionStatusSummaryMap, entity);
        }
        return result.build();
    }

    private Map<Long, FavouritedSummary> computeFavouritedSummaryMap(UserKey currentUserKey, Collection<ContentId> contentIds) {
        List versionsLastEditedByUser = this.labelManager.getFavouriteLabellingsByContentIds(contentIds, currentUserKey);
        ImmutableMap favouriteLabellingByContentIdMap = Maps.uniqueIndex((Iterable)versionsLastEditedByUser, labelling -> labelling.getLableable().getId());
        return Maps.transformValues((Map)favouriteLabellingByContentIdMap, labelling -> new FavouritedSummary(true, new DateTime((Object)labelling.getCreationDate())));
    }

    private Map<Long, LastModifiedSummary> computeLastModifiedSummaryMap(UserKey currentUserKey, Collection<ContentId> contentIds) {
        Option currentUserPerson = this.personService.find(new Expansion[0]).withUserKey(currentUserKey).fetchOne();
        Map versionsLastEditedByUser = this.contentEntityManager.getVersionsLastEditedByUser(contentIds, currentUserKey);
        return Maps.transformValues((Map)versionsLastEditedByUser, ceo -> {
            Version version = Version.builder().number(ceo.getVersion()).message(ceo.getVersionComment()).minorEdit(false).when(ceo.getLastModificationDate()).by((Person)currentUserPerson.getOrNull()).build();
            return new LastModifiedSummary(version, this.i18NBeanFactory.getI18NBean().getText(this.dateFormatterFactory.createFriendlyForUser().getFormatMessage(ceo.getLastModificationDate())));
        });
    }

    private Map<Long, RecentlyViewedSummary> computeRecentlyViewedSummaryMap(UserKey currentUserKey, Collection<ContentId> contentIds) {
        List<RecentlyViewed> recentlyViewedEntries = this.recentlyViewedDao.findRecentlyViewedEntries(contentIds, currentUserKey);
        ImmutableMap recentlyViewedByContentIdMap = Maps.uniqueIndex(recentlyViewedEntries, RecentlyViewed::getId);
        return Maps.transformValues((Map)recentlyViewedByContentIdMap, recentlyViewed -> {
            DateTime lastSeen = new DateTime(recentlyViewed.getLastSeen());
            return new RecentlyViewedSummary(lastSeen, this.i18NBeanFactory.getI18NBean().getText(this.dateFormatterFactory.createFriendlyForUser().getFormatMessage(lastSeen.toDate())));
        });
    }

    private Map<Long, ContributionStatusSummary> computeContributionStatusSummaryMap(UserKey currentUserKey, Collection<ContentId> contentIds) {
        Map contributionStatuses = this.contentEntityManager.getContributionStatusByUser(contentIds, currentUserKey);
        return Maps.transformValues((Map)contributionStatuses, contributionStatus -> new ContributionStatusSummary(contributionStatus.getStatus(), new DateTime((Object)contributionStatus.getLastModifiedDate())));
    }

    @Deprecated
    public List<String> getMetadataProperties() {
        return Collections.singletonList(CURRENT_USER_METADATA_KEY);
    }

    public List<MetadataProperty> getProperties() {
        MetadataProperty property = new MetadataProperty(CURRENT_USER_METADATA_KEY, Arrays.asList(new MetadataProperty(FAVOURITED_METADATA_KEY, Arrays.asList(new MetadataProperty("isFavourite", Boolean.class), new MetadataProperty("favouritedDate", DateTime.class))), new MetadataProperty(LAST_MODIFIED_METADATA_KEY, Arrays.asList(new MetadataProperty("version", Version.class), new MetadataProperty("friendlyLastModified", String.class))), new MetadataProperty(VIEWED_METADATA_KEY, Arrays.asList(new MetadataProperty("lastSeen", DateTime.class), new MetadataProperty("friendlyLastModified", String.class))), new MetadataProperty(LAST_CONTRIBUTED_METADATA_KEY, Arrays.asList(new MetadataProperty("status", String.class), new MetadataProperty("when", DateTime.class)))));
        return Collections.singletonList(property);
    }

    private <T> void populateMap(ModelMapBuilder<Object, Map<String, ?>> result, ModelMapBuilder<String, Object> metadataMap, String expandName, Map<Long, T> summaryByIdMap, Object entity) {
        Content content = (Content)entity;
        if (summaryByIdMap == null) {
            metadataMap.addCollapsedEntry((Object)expandName);
        } else {
            T summaryObject = summaryByIdMap.get(content.getId().asLong());
            if (summaryObject != null) {
                metadataMap.put((Object)expandName, summaryByIdMap.get(content.getId().asLong()));
            }
        }
        result.put(entity, (Object)ModelMapBuilder.newInstance().put((Object)CURRENT_USER_METADATA_KEY, (Object)metadataMap.build()).build());
    }
}

