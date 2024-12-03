/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Container
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.service.content.ContentService$ContentFinder
 *  com.atlassian.confluence.api.service.content.ContentService$SingleContentFetcher
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.api.impl.service.content.finder;

import com.atlassian.confluence.api.impl.service.content.finder.AbstractFinder;
import com.atlassian.confluence.api.impl.service.content.finder.FinderPredicates;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

abstract class AbstractContentFinder
extends AbstractFinder<Content>
implements ContentService.ContentFinder {
    private List<Space> spaces = new ArrayList<Space>();
    private List<ContentType> contentTypes = ContentType.BUILT_IN;
    private LocalDate createdDate;
    private String title;
    private int version;
    private ContentId contentId;
    private ContentId contentContainerId;
    private List<ContentStatus> statuses = ImmutableList.of((Object)ContentStatus.CURRENT);
    private List<ContentId> contentIds = Collections.emptyList();

    public AbstractContentFinder(Expansion ... expansions) {
        super(expansions);
    }

    public ContentService.ContentFinder withSpace(Space ... space) {
        if (this.spaces.size() > 1) {
            throw new NotImplementedServiceException("Finding content for multiple spaces is not currently supported");
        }
        this.spaces = ImmutableList.copyOf((Object[])((Space[])Preconditions.checkNotNull((Object)space)));
        return this;
    }

    public ContentService.ContentFinder withContainer(Container container) {
        if (container instanceof Space) {
            return this.withSpace((Space)container);
        }
        if (container instanceof Content) {
            this.contentContainerId = ((Content)container).getId();
        }
        return this;
    }

    public ContentService.ContentFinder withId(ContentId first, ContentId ... tail) {
        this.contentIds = ImmutableList.builder().add((Object)first).add((Object[])tail).build();
        return this;
    }

    public ContentService.ContentFinder withId(Iterable<ContentId> contentIds) {
        this.contentIds = ImmutableList.copyOf(contentIds);
        return this;
    }

    public ContentService.ContentFinder withType(ContentType ... type) {
        this.contentTypes = ImmutableList.copyOf((Object[])((ContentType[])Preconditions.checkNotNull((Object)type)));
        return this;
    }

    public ContentService.ContentFinder withCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public ContentService.ContentFinder withTitle(String title) {
        this.title = title;
        return this;
    }

    public ContentService.ContentFinder withStatus(ContentStatus ... status) {
        this.statuses = Lists.newArrayList((Object[])((ContentStatus[])Preconditions.checkNotNull((Object)status)));
        return this;
    }

    public ContentService.ContentFinder withStatus(Iterable<ContentStatus> statuses) {
        this.statuses = Lists.newArrayList(statuses);
        return this;
    }

    public ContentService.ContentFinder withAnyStatus() {
        this.statuses = ImmutableList.of((Object)ContentStatus.CURRENT, (Object)ContentStatus.TRASHED, (Object)ContentStatus.DRAFT);
        return this;
    }

    public ContentService.SingleContentFetcher withIdAndVersion(ContentId contentId, int version) {
        this.contentId = contentId;
        this.version = version;
        return this;
    }

    public ContentService.SingleContentFetcher withId(ContentId contentId) {
        this.contentId = contentId;
        return this;
    }

    protected Predicate<? super ContentEntityObject> asPredicateWithContentType(ContentType type) {
        return t -> FinderPredicates.createContentTypePredicate(type).test((ContentEntityObject)t) && this.asPredicate().test((ContentEntityObject)t);
    }

    protected Predicate<? super ContentEntityObject> asPredicate() {
        ArrayList<Predicate<? super ContentEntityObject>> filterList = new ArrayList<Predicate<? super ContentEntityObject>>();
        filterList.add(Objects::nonNull);
        if (this.createdDate != null) {
            filterList.add(FinderPredicates.createCreationDatePredicate(this.createdDate));
        }
        if (this.spaces.size() > 0) {
            List<String> spaceKeys = this.spaces.stream().map(Space::getKey).collect(Collectors.toList());
            filterList.add(FinderPredicates.createSpaceKeysPredicate(spaceKeys));
        }
        if (this.title != null) {
            filterList.add(FinderPredicates.createTitlePredicate(this.title));
        }
        if (!this.contentTypes.isEmpty()) {
            filterList.add(FinderPredicates.createContentTypePredicate(this.contentTypes.toArray(new ContentType[0])));
        }
        if (!this.contentIds.isEmpty()) {
            filterList.add(FinderPredicates.createContentIdPredicate(this.contentIds));
        }
        if (!this.statuses.isEmpty()) {
            filterList.add(FinderPredicates.statusPredicate(this.statuses));
        }
        return arg -> filterList.stream().allMatch(p -> p.test(arg));
    }

    public List<Space> getSpaces() {
        return this.spaces;
    }

    public List<ContentType> getContentTypes() {
        return this.contentTypes;
    }

    public LocalDate getCreatedDate() {
        return this.createdDate;
    }

    public String getTitle() {
        return this.title;
    }

    public int getVersion() {
        return this.version;
    }

    public ContentId getContentId() {
        return this.contentId;
    }

    public ContentId getContentContainerId() {
        return this.contentContainerId;
    }

    public List<ContentStatus> getStatuses() {
        return this.statuses;
    }

    public List<ContentId> getContentIds() {
        return this.contentIds;
    }
}

