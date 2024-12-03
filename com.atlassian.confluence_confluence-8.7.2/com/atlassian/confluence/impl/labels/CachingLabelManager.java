/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.Lists
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.atlassian.confluence.impl.labels;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.PartialList;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostMovedEvent;
import com.atlassian.confluence.event.events.content.page.PageMoveCompletedEvent;
import com.atlassian.confluence.event.events.space.SpaceRemoveEvent;
import com.atlassian.confluence.event.events.types.Restore;
import com.atlassian.confluence.event.events.types.Trashed;
import com.atlassian.confluence.impl.labels.ReadThroughMostPopularCache;
import com.atlassian.confluence.impl.labels.adaptivelabelcache.AdaptiveMostUsedLabelsCache;
import com.atlassian.confluence.internal.labels.LabelManagerInternal;
import com.atlassian.confluence.labels.EditableLabelable;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.Labelling;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.labels.dto.LiteLabelSearchResult;
import com.atlassian.confluence.labels.dto.RankedLiteLabelSearchResult;
import com.atlassian.confluence.labels.persistence.dao.LabelSearchResult;
import com.atlassian.confluence.labels.persistence.dao.RankedLabelSearchResult;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class CachingLabelManager
implements LabelManagerInternal {
    private final AdaptiveMostUsedLabelsCache adaptiveLabelManagerCache;
    private final LabelManagerInternal delegate;
    private final EventListenerRegistrar eventListenerRegistrar;
    private final ReadThroughMostPopularCache<LabelSearchResult> mostPopularCache;

    public CachingLabelManager(LabelManagerInternal delegate, CacheFactory cacheFactory, EventListenerRegistrar eventListenerRegistrar, AdaptiveMostUsedLabelsCache adaptiveLabelManagerCache) {
        this.adaptiveLabelManagerCache = adaptiveLabelManagerCache;
        this.delegate = Objects.requireNonNull(delegate);
        this.eventListenerRegistrar = Objects.requireNonNull(eventListenerRegistrar);
        this.mostPopularCache = ReadThroughMostPopularCache.create(cacheFactory, CoreCache.MOST_POPULAR_BY_SPACE);
    }

    @PostConstruct
    public void init() {
        this.eventListenerRegistrar.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.eventListenerRegistrar.unregister((Object)this);
    }

    @EventListener
    public void onTrash(Trashed trashed) {
        this.invalidateCaches();
    }

    @EventListener
    public void onRestore(Restore restore) {
        this.invalidateCaches();
    }

    @EventListener
    public void onSpaceRemove(SpaceRemoveEvent event) {
        this.adaptiveLabelManagerCache.deletePersistedRecord(event.getSpace().getId());
        this.adaptiveLabelManagerCache.deletePersistedRecordForSite();
    }

    @EventListener
    public void onPagesMoved(PageMoveCompletedEvent event) {
        Space newSpace = event.getMovedPageList().get(0).getSpace();
        if (!newSpace.equals(event.getOldSpace())) {
            this.invalidateCaches();
        }
    }

    @EventListener
    public void onPagesMoved(BlogPostMovedEvent event) {
        if (!event.getOriginalSpace().equals(event.getCurrentSpace())) {
            this.invalidateCaches();
        }
    }

    @Override
    public int addLabel(Labelable content, Label label) {
        int result = this.delegate.addLabel(content, label);
        if (result != 0) {
            this.invalidateCaches();
        }
        return result;
    }

    @Override
    public int removeLabel(Labelable object, Label label) {
        int result = this.delegate.removeLabel(object, label);
        if (result != 0) {
            this.invalidateCaches();
        }
        return result;
    }

    @Override
    public void removeLabels(Labelable object, List labels) {
        this.delegate.removeLabels(object, labels);
        this.invalidateCaches();
    }

    @Override
    public void removeAllLabels(Labelable content) {
        this.delegate.removeAllLabels(content);
        this.invalidateCaches();
    }

    @Override
    public boolean deleteLabel(long id) {
        boolean deleted = this.delegate.deleteLabel(id);
        if (deleted) {
            this.invalidateCaches();
        }
        return deleted;
    }

    @Override
    public boolean deleteLabel(Label l) {
        boolean deleted = this.delegate.deleteLabel(l);
        if (deleted) {
            this.invalidateCaches();
        }
        return deleted;
    }

    @Override
    public Label getLabel(long id) {
        return this.delegate.getLabel(id);
    }

    @Override
    public Label getLabel(ParsedLabelName parsedLabelName) {
        return this.delegate.getLabel(parsedLabelName);
    }

    @Override
    public Label getLabel(String unparsedLabelName) {
        return this.delegate.getLabel(unparsedLabelName);
    }

    @Override
    public List<Label> getLabels(Collection<String> unparsedLabelNames) {
        return this.delegate.getLabels(unparsedLabelNames);
    }

    @Override
    public Label getLabel(Label label) {
        return this.delegate.getLabel(label);
    }

    @Override
    public Label getLabel(String labelName, Namespace namespace) {
        return this.delegate.getLabel(labelName, namespace);
    }

    @Override
    public List<Label> getLabelsByDetail(String labelName, String namespace, String spaceKey, String owner) {
        return this.delegate.getLabelsByDetail(labelName, namespace, spaceKey, owner);
    }

    @Override
    public List<Label> getLabelsInSpace(String key) {
        return this.delegate.getLabelsInSpace(key);
    }

    @Override
    public List<LabelSearchResult> getMostPopularLabels() {
        return this.getMostPopularLabels(DEFAULT_LABEL_COUNT);
    }

    @Override
    public List<LabelSearchResult> getMostPopularLabels(int maxResults) {
        return Lists.newArrayList(this.mostPopularCache.getMostPopularGlobal(CachingLabelManager.optional(maxResults), limit -> this.delegate.getMostPopularLabels(limit.orElse(0))));
    }

    @Override
    public List<LabelSearchResult> getMostPopularLabelsInSpace(String key) {
        return this.getMostPopularLabelsInSpace(key, DEFAULT_LABEL_COUNT);
    }

    @Override
    public List<LabelSearchResult> getMostPopularLabelsInSpace(String spaceKey, int maxResults) {
        return Lists.newArrayList(this.mostPopularCache.getMostPopularInSpace(spaceKey, CachingLabelManager.optional(maxResults), limit -> this.delegate.getMostPopularLabelsInSpace(spaceKey, limit.orElse(0))));
    }

    private static Optional<Integer> optional(int maxResults) {
        return maxResults == 0 ? Optional.empty() : Optional.of(maxResults);
    }

    @Override
    public Set<RankedLabelSearchResult> getMostPopularLabelsWithRanks(Comparator<? super RankedLabelSearchResult> comparator) {
        return this.delegate.getMostPopularLabelsWithRanks(comparator);
    }

    @Override
    public Set<RankedLabelSearchResult> getMostPopularLabelsWithRanks(int maxResults, Comparator<? super RankedLabelSearchResult> comparator) {
        return this.delegate.getMostPopularLabelsWithRanks(maxResults, comparator);
    }

    @Override
    public Set<RankedLabelSearchResult> getMostPopularLabelsWithRanksInSpace(String key, int maxResults, Comparator<? super RankedLabelSearchResult> comparator) {
        return this.delegate.getMostPopularLabelsWithRanksInSpace(key, maxResults, comparator);
    }

    @Override
    public List<Label> getRecentlyUsedLabels() {
        return this.delegate.getRecentlyUsedLabels();
    }

    @Override
    public List<Label> getRecentlyUsedLabels(int maxResults) {
        return this.delegate.getRecentlyUsedLabels(maxResults);
    }

    @Override
    public List<Labelling> getRecentlyUsedLabellings(int maxResults) {
        return this.delegate.getRecentlyUsedLabellings(maxResults);
    }

    @Override
    public List<Label> getRecentlyUsedLabelsInSpace(String spaceKey) {
        return this.delegate.getRecentlyUsedLabelsInSpace(spaceKey);
    }

    @Override
    public List<Label> getRecentlyUsedLabelsInSpace(String spaceKey, int maxResults) {
        return this.delegate.getRecentlyUsedLabelsInSpace(spaceKey, maxResults);
    }

    @Override
    public List<Labelling> getRecentlyUsedLabellingsInSpace(String spaceKey, int maxResults) {
        return this.delegate.getRecentlyUsedLabellingsInSpace(spaceKey, maxResults);
    }

    @Override
    public List<Label> getSuggestedLabels(Labelable content) {
        return this.delegate.getSuggestedLabels(content);
    }

    @Override
    public List<Label> getSuggestedLabels(Labelable content, int maxResults) {
        return this.delegate.getSuggestedLabels(content, maxResults);
    }

    @Override
    public List<Label> getSuggestedLabelsInSpace(Labelable content, String spaceKey) {
        return this.delegate.getSuggestedLabelsInSpace(content, spaceKey);
    }

    @Override
    public List<Label> getSuggestedLabelsInSpace(Labelable content, String spaceKey, int maxResults) {
        return this.delegate.getSuggestedLabelsInSpace(content, spaceKey, maxResults);
    }

    @Override
    public List<Label> getRelatedLabels(Label label) {
        return this.delegate.getRelatedLabels(label);
    }

    @Override
    public List<Label> getRelatedLabels(Label label, int maxResults) {
        return this.delegate.getRelatedLabels(label, maxResults);
    }

    @Override
    public List<Label> getRelatedLabels(List<? extends Label> labels, String spaceKey, int maxResultsPerLabel) {
        return this.delegate.getRelatedLabels(labels, spaceKey, maxResultsPerLabel);
    }

    @Override
    public List<Label> getRelatedLabelsInSpace(Label label, String spaceKey) {
        return this.delegate.getRelatedLabelsInSpace(label, spaceKey);
    }

    @Override
    public List<Label> getRelatedLabelsInSpace(Label label, String spaceKey, int maxResults) {
        return this.delegate.getRelatedLabelsInSpace(label, spaceKey, maxResults);
    }

    @Override
    public List<Space> getSpacesContainingContentWithLabel(Label label) {
        return this.delegate.getSpacesContainingContentWithLabel(label);
    }

    @Override
    public List<Label> getUsersLabels(String owner) {
        return this.delegate.getUsersLabels(owner);
    }

    @Override
    public List<Label> getTeamLabels() {
        return this.delegate.getTeamLabels();
    }

    @Override
    public List<Label> getTeamLabels(String name) {
        return this.delegate.getTeamLabels(name);
    }

    @Override
    public List<Label> getTeamLabelsForSpace(String spaceKey) {
        return this.delegate.getTeamLabelsForSpace(spaceKey);
    }

    @Override
    public List<Label> getTeamLabelsForSpaces(Collection<Space> spaces) {
        return this.delegate.getTeamLabelsForSpaces(spaces);
    }

    @Override
    public List<? extends Labelable> getCurrentContentForLabel(Label label) {
        return this.delegate.getCurrentContentForLabel(label);
    }

    @Override
    public List<? extends Labelable> getCurrentContentForLabelAndSpace(Label label, String spaceKey) {
        return this.delegate.getCurrentContentForLabelAndSpace(label, spaceKey);
    }

    @Override
    public List<? extends Labelable> getCurrentContentWithPersonalLabel(String username) {
        return this.delegate.getCurrentContentWithPersonalLabel(username);
    }

    @Override
    public List<Space> getSpacesWithLabel(Label label) {
        return this.delegate.getSpacesWithLabel(label);
    }

    @Override
    public List<Space> getFavouriteSpaces(String username) {
        return this.delegate.getFavouriteSpaces(username);
    }

    @Override
    public List<Labelling> getFavouriteLabellingsByContentIds(Collection<ContentId> contentIds, UserKey userKey) {
        return this.delegate.getFavouriteLabellingsByContentIds(contentIds, userKey);
    }

    @Override
    public List<Label> getRecentlyUsedPersonalLabels(String username) {
        return this.delegate.getRecentlyUsedPersonalLabels(username);
    }

    @Override
    public List<Label> getRecentlyUsedPersonalLabels(String username, int maxResults) {
        return this.delegate.getRecentlyUsedPersonalLabels(username, maxResults);
    }

    @Override
    public List<Labelling> getRecentlyUsedPersonalLabellings(String username, int maxResults) {
        return this.delegate.getRecentlyUsedPersonalLabellings(username, maxResults);
    }

    @Override
    public List getContent(Label label) {
        return this.delegate.getContent(label);
    }

    @Override
    public int getContentCount(Label label) {
        return this.delegate.getContentCount(label);
    }

    @Override
    public Label createLabel(Label label) {
        Label createdLabel = this.delegate.createLabel(label);
        this.invalidateCaches();
        return createdLabel;
    }

    @Override
    public List<? extends Labelable> getContentForAllLabels(Collection<Label> labels, int maxResults, int offset) {
        return this.delegate.getContentForAllLabels(labels, maxResults, offset);
    }

    @Override
    public PartialList<ContentEntityObject> getContentForLabel(int offset, int maxResults, Label label) {
        return this.delegate.getContentForLabel(offset, maxResults, label);
    }

    @Override
    public <T extends EditableLabelable> PartialList<T> getForLabel(Class<T> labelableClass, int offset, int maxResults, Label label) {
        return this.delegate.getForLabel(labelableClass, offset, maxResults, label);
    }

    @Override
    public <T extends EditableLabelable> PartialList<T> getForLabels(Class<T> labelableClass, int offset, int maxResults, Label ... labels) {
        return this.delegate.getForLabels(labelableClass, offset, maxResults, labels);
    }

    @Override
    public PartialList<EditableLabelable> getForLabels(int offset, int maxResults, Label ... labels) {
        return this.delegate.getForLabels(offset, maxResults, labels);
    }

    @Override
    public PartialList<ContentEntityObject> getContentForAllLabels(int offset, int maxResults, Label ... labels) {
        return this.delegate.getContentForAllLabels(offset, maxResults, labels);
    }

    @Override
    public PartialList<ContentEntityObject> getContentInSpaceForLabel(int offset, int maxResults, String spaceKey, Label label) {
        return this.delegate.getContentInSpaceForLabel(offset, maxResults, spaceKey, label);
    }

    @Override
    public PartialList<ContentEntityObject> getContentInSpaceForAllLabels(int offset, int maxResults, String spaceKey, Label ... labels) {
        return this.delegate.getContentInSpaceForAllLabels(offset, maxResults, spaceKey, labels);
    }

    @Override
    public PartialList<ContentEntityObject> getContentInSpacesForAllLabels(int offset, int maxResults, Set<String> spaceKeys, Label ... labels) {
        return this.delegate.getContentInSpacesForAllLabels(offset, maxResults, spaceKeys, labels);
    }

    @Override
    public PartialList<ContentEntityObject> getAllContentForLabel(int offset, int maxResults, Label label) {
        return this.delegate.getAllContentForLabel(offset, maxResults, label);
    }

    @Override
    public PartialList<ContentEntityObject> getAllContentForAllLabels(int offset, int maxResults, Label ... labels) {
        return this.delegate.getAllContentForAllLabels(offset, maxResults, labels);
    }

    @Override
    public PageResponse<Label> findGlobalLabelsByNamePrefix(String namePrefix, LimitedRequest pageRequest) {
        return this.delegate.findGlobalLabelsByNamePrefix(namePrefix, pageRequest);
    }

    @Override
    public PageResponse<Label> findTeamLabelsByNamePrefix(String namePrefix, LimitedRequest pageRequest) {
        return this.delegate.findTeamLabelsByNamePrefix(namePrefix, pageRequest);
    }

    @Override
    public List<Label> getLabelsInSpace(String key, LimitedRequest pageRequest) {
        return this.delegate.getLabelsInSpace(key, pageRequest);
    }

    @Override
    public long getTotalLabelInSpace(String key) {
        return this.delegate.getTotalLabelInSpace(key);
    }

    @Override
    public List<LiteLabelSearchResult> getMostPopularLabelsInSpaceLite(String spaceKey, int maxResults) {
        return this.adaptiveLabelManagerCache.getSpaceRecord(spaceKey, maxResults);
    }

    @Override
    public List<LiteLabelSearchResult> getMostPopularLabelsInSiteLite(int maxResults) {
        return this.adaptiveLabelManagerCache.getSiteRecord(maxResults);
    }

    @Override
    public Set<RankedLiteLabelSearchResult> calculateRanksForLiteLabels(List<LiteLabelSearchResult> labelList, Comparator<? super RankedLiteLabelSearchResult> comparator) {
        return this.delegate.calculateRanksForLiteLabels(labelList, comparator);
    }

    private void invalidateCaches() {
        this.mostPopularCache.clear();
    }
}

