/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.service.network.NetworkService
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.spaces.SpaceDescription
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.streams.api.ActivityObjectType
 *  com.atlassian.streams.api.ActivityObjectTypes
 *  com.atlassian.streams.api.ActivityRequest
 *  com.atlassian.streams.api.ActivityVerb
 *  com.atlassian.streams.api.ActivityVerbs
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.Pair
 *  com.atlassian.streams.api.common.Pairs
 *  com.atlassian.streams.spi.Evictor
 *  com.atlassian.streams.spi.Filters
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Ordering
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.confluence.changereport;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.service.network.NetworkService;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.streams.api.ActivityObjectType;
import com.atlassian.streams.api.ActivityObjectTypes;
import com.atlassian.streams.api.ActivityRequest;
import com.atlassian.streams.api.ActivityVerb;
import com.atlassian.streams.api.ActivityVerbs;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.Pair;
import com.atlassian.streams.api.common.Pairs;
import com.atlassian.streams.confluence.changereport.ActivityItem;
import com.atlassian.streams.confluence.changereport.AttachmentActivityItem;
import com.atlassian.streams.confluence.changereport.AttachmentActivityItemFactory;
import com.atlassian.streams.confluence.changereport.BoundedActivityItemTreeSet;
import com.atlassian.streams.confluence.changereport.ContentEntityActivityItemFactory;
import com.atlassian.streams.spi.Evictor;
import com.atlassian.streams.spi.Filters;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivityItemFactory {
    private static final Logger log = LoggerFactory.getLogger(ActivityItemFactory.class);
    private static final SimplePageRequest PAGE_REQUEST = new SimplePageRequest(1, 1000);
    private final AttachmentActivityItemFactory attachmentActivityItemFactory;
    private final ContentEntityActivityItemFactory contentEntityActivityItemFactory;
    private final Evictor<ConfluenceEntityObject> evictor;
    private final NetworkService networkService;
    private final PageManager pageManager;
    private final UserManager userManager;
    static final Ordering<ActivityItem> activityItemSorter = new Ordering<ActivityItem>(){

        public int compare(ActivityItem i1, ActivityItem i2) {
            int cTime = Ordering.natural().compare((Object)i1.getModified().getTime(), (Object)i2.getModified().getTime());
            int cVersion = Ordering.natural().compare((Object)i1.getVersion(), (Object)i2.getVersion());
            if (cTime != 0) {
                return cTime;
            }
            if (cVersion != 0) {
                return cVersion;
            }
            if (i1.getId() != null && i2.getId() != null) {
                return Ordering.natural().compare((Object)i1.getId(), (Object)i2.getId());
            }
            return Ordering.natural().compare((Object)i1.hashCode(), (Object)i2.hashCode());
        }
    }.reverse();

    public ActivityItemFactory(ContentEntityActivityItemFactory contentEntityActivityItemFactory, AttachmentActivityItemFactory attachmentActivityItemFactory, PageManager pageManager, Evictor<ConfluenceEntityObject> evictor, UserManager userManager, NetworkService networkService) {
        this.contentEntityActivityItemFactory = Objects.requireNonNull(contentEntityActivityItemFactory, "contentEntityActivityItemFactory can't be null");
        this.attachmentActivityItemFactory = Objects.requireNonNull(attachmentActivityItemFactory, "attachmentActivityItemFactory can't be null");
        this.pageManager = Objects.requireNonNull(pageManager, "thumbnailManager can't be null");
        this.evictor = Objects.requireNonNull(evictor, "evictor can't be null");
        this.userManager = Objects.requireNonNull(userManager, "userManager can't be null");
        this.networkService = Objects.requireNonNull(networkService, "networkService can't be null");
    }

    public Iterable<ActivityItem> getActivityItems(Iterable<ConfluenceEntityObject> searchables, ActivityRequest request) {
        return this.getActivityItems((Iterable<ActivityItem>)ImmutableList.of(), searchables, request);
    }

    public Iterable<ActivityItem> getActivityItems(Iterable<ActivityItem> baseItems, Iterable<ConfluenceEntityObject> searchables, ActivityRequest request) {
        return new GetActivityItems(request, baseItems).add(searchables).getResults();
    }

    private Predicate<String> getFollowedUsersPredicate(ActivityRequest request, UserKey user) {
        Collection networkFilters = request.getProviderFilters().get((Object)"network");
        if (networkFilters.isEmpty()) {
            return s -> true;
        }
        List<String> followedUsers = this.getFollowers(user);
        if (!Filters.getIsValues((Iterable)networkFilters).isEmpty()) {
            return arg_0 -> ((com.google.common.base.Predicate)Predicates.in(followedUsers)).apply(arg_0);
        }
        if (!Filters.getNotValues((Iterable)networkFilters).isEmpty()) {
            return ((Predicate<String>)arg_0 -> ((com.google.common.base.Predicate)Predicates.in(followedUsers)).apply(arg_0)).negate();
        }
        return s -> true;
    }

    private List<String> getFollowers(UserKey user) {
        PageResponse pageResponse;
        ArrayList<String> result = new ArrayList<String>();
        do {
            pageResponse = this.networkService.getFollowers(user, (PageRequest)PAGE_REQUEST);
            pageResponse.getResults().stream().map(User::getUsername).forEach(result::add);
        } while (pageResponse.hasMore());
        return result;
    }

    private Option<AttachmentActivityItem> getMatchingAttachmentItem(Iterable<? extends ActivityItem> activityItems, Attachment attachment) {
        for (ActivityItem activityItem : activityItems) {
            AttachmentActivityItem attachmentItem;
            if (!(activityItem instanceof AttachmentActivityItem) || !(attachmentItem = (AttachmentActivityItem)activityItem).matches(attachment)) continue;
            return Option.some((Object)attachmentItem);
        }
        return Option.none();
    }

    private Iterable<Pair<ActivityObjectType, ActivityVerb>> getActivities(ActivityItem activityItem) {
        return Iterables.transform((Iterable)ActivityObjectTypes.getActivityObjectTypes(activityItem.getActivityObjects()), (Function)Pairs.pairWith((Object)activityItem.getVerb()));
    }

    private final class PageHistoryIterator
    implements Iterator<ActivityItem> {
        private AbstractPage page;
        private boolean haveNext;
        private final URI baseUri;

        PageHistoryIterator(URI baseUri, AbstractPage page) {
            this.page = page;
            this.baseUri = baseUri;
            this.haveNext = true;
        }

        @Override
        public boolean hasNext() {
            return this.prepareNext();
        }

        @Override
        public ActivityItem next() {
            if (!this.prepareNext()) {
                return null;
            }
            ActivityItem ret = ActivityItemFactory.this.contentEntityActivityItemFactory.newActivityItem(this.baseUri, this.page);
            this.haveNext = false;
            return ret;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private boolean prepareNext() {
            if (!this.haveNext) {
                if (this.page != null) {
                    this.page = (AbstractPage)ActivityItemFactory.this.pageManager.getPreviousVersion((ContentEntityObject)this.page);
                }
                this.haveNext = this.page != null;
            }
            return this.haveNext;
        }
    }

    private final class GetActivityItems {
        private final BoundedActivityItemTreeSet activityItems;
        private final boolean shouldIncludeAttachments;
        private final boolean hasSpaceFilter;
        private final Option<Date> requestMinDate;
        private com.google.common.base.Predicate<Pair<ActivityObjectType, ActivityVerb>> inActivities;
        private com.google.common.base.Predicate<String> inProjectKeys;
        private com.google.common.base.Predicate<Iterable<String>> anyUsers;
        private com.google.common.base.Predicate<Iterable<String>> notInUsers;
        private Predicate<String> followedUsers;
        private com.google.common.base.Predicate<Date> inDateRange;
        private URI baseUri;

        GetActivityItems(ActivityRequest request, Iterable<ActivityItem> baseItems) {
            this.activityItems = new BoundedActivityItemTreeSet((Evictor<ConfluenceEntityObject>)ActivityItemFactory.this.evictor, request.getMaxResults(), (Comparator<ActivityItem>)activityItemSorter);
            Iterables.addAll((Collection)((Object)this.activityItems), baseItems);
            this.shouldIncludeAttachments = Iterables.any((Iterable)ImmutableList.of((Object)Pair.pair((Object)ActivityObjectTypes.file(), (Object)ActivityVerbs.post()), (Object)Pair.pair((Object)ActivityObjectTypes.file(), (Object)ActivityVerbs.update())), (com.google.common.base.Predicate)Filters.inActivities((ActivityRequest)request));
            this.hasSpaceFilter = !Iterables.isEmpty((Iterable)Filters.getIsValues((Iterable)request.getStandardFilters().get((Object)"key")));
            this.requestMinDate = Filters.getMinDate((ActivityRequest)request);
            this.inActivities = Filters.inActivities((ActivityRequest)request);
            this.inProjectKeys = Filters.inProjectKeys((ActivityRequest)request);
            this.anyUsers = Filters.anyInUsers((ActivityRequest)request);
            this.notInUsers = Filters.notInUsers((ActivityRequest)request);
            this.followedUsers = ActivityItemFactory.this.getFollowedUsersPredicate(request, ActivityItemFactory.this.userManager.getRemoteUserKey());
            this.inDateRange = Filters.inDateRange((ActivityRequest)request);
            this.baseUri = request.getContextUri();
        }

        public GetActivityItems add(Iterable<ConfluenceEntityObject> searchables) {
            block2: for (ConfluenceEntityObject searchable : searchables) {
                try {
                    for (ActivityItem item : this.toActivityItems(this.baseUri, searchable)) {
                        if (this.shouldIncludeItem(item)) {
                            if (this.activityItems.add(item)) continue;
                            continue block2;
                        }
                        ActivityItemFactory.this.evictor.apply((Object)item.getEntity());
                        if (!this.requestMinDate.isDefined() || !item.getModified().before((Date)this.requestMinDate.get())) continue;
                    }
                }
                catch (Exception e) {
                    log.warn("Error building ActivityItem from ConfluenceEntityObject", (Throwable)e);
                }
            }
            return this;
        }

        public Iterable<ActivityItem> getResults() {
            return Iterables.unmodifiableIterable((Iterable)((Object)this.activityItems));
        }

        private boolean shouldIncludeItem(ActivityItem item) {
            Option author = Option.option((Object)item.getChangedBy());
            return Iterables.any((Iterable)ActivityItemFactory.this.getActivities(item), this.inActivities) && (Boolean)item.getSpaceKey().map(Functions.forPredicate(this.inProjectKeys)).getOrElse((Object)(!this.hasSpaceFilter ? 1 : 0)) != false && this.anyUsers.apply((Object)author) && this.notInUsers.apply((Object)author) && this.followedUsers.test(item.getChangedBy()) && this.inDateRange.apply((Object)item.getModified());
        }

        private Iterable<ActivityItem> toActivityItems(URI baseUri, ConfluenceEntityObject entity) {
            if (entity instanceof Attachment) {
                if (this.shouldIncludeAttachments) {
                    return this.getAttachmentActivityItems((Attachment)entity);
                }
                return ImmutableList.of();
            }
            if (entity instanceof AbstractPage) {
                return this.getPageHistoryActivityItems((AbstractPage)entity);
            }
            if (entity instanceof SpaceDescription) {
                return this.getSpaceActivityItems((SpaceDescription)entity);
            }
            if (entity instanceof Comment) {
                return Option.some((Object)ActivityItemFactory.this.contentEntityActivityItemFactory.newActivityItem(baseUri, (Comment)entity));
            }
            throw new IllegalArgumentException("Unsupported entity type: " + entity);
        }

        private Iterable<ActivityItem> getAttachmentActivityItems(Attachment attachment) {
            Iterator iterator = ActivityItemFactory.this.getMatchingAttachmentItem((Iterable)((Object)this.activityItems), attachment).iterator();
            if (iterator.hasNext()) {
                AttachmentActivityItem attachmentItem = (AttachmentActivityItem)iterator.next();
                ActivityItem updatedItem = ActivityItemFactory.this.attachmentActivityItemFactory.newActivityItem(this.baseUri, attachment, attachmentItem);
                this.activityItems.remove(attachmentItem);
                this.activityItems.add(updatedItem);
                return ImmutableList.of();
            }
            return Option.some((Object)ActivityItemFactory.this.attachmentActivityItemFactory.newActivityItem(this.baseUri, attachment));
        }

        private Iterable<ActivityItem> getPageHistoryActivityItems(AbstractPage page) {
            return () -> new PageHistoryIterator(this.baseUri, page);
        }

        private Iterable<ActivityItem> getSpaceActivityItems(SpaceDescription space) {
            ActivityItem createdItem = ActivityItemFactory.this.contentEntityActivityItemFactory.newActivityItem(space, true);
            if (space.isNew()) {
                return Option.some((Object)createdItem);
            }
            ActivityItem editedItem = ActivityItemFactory.this.contentEntityActivityItemFactory.newActivityItem(space, false);
            return ImmutableList.of((Object)editedItem, (Object)createdItem);
        }
    }
}

