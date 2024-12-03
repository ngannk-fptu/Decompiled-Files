/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.common.properties.SystemProperties
 *  com.atlassian.crowd.dao.membership.InternalMembershipDao
 *  com.atlassian.crowd.dao.tombstone.TombstoneDao
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.embedded.spi.GroupDao
 *  com.atlassian.crowd.embedded.spi.UserDao
 *  com.atlassian.crowd.event.DirectoryEvent
 *  com.atlassian.crowd.event.EventStore
 *  com.atlassian.crowd.event.EventTokenExpiredException
 *  com.atlassian.crowd.event.Events
 *  com.atlassian.crowd.event.application.ApplicationDirectoryAddedEvent
 *  com.atlassian.crowd.event.application.ApplicationDirectoryOrderUpdatedEvent
 *  com.atlassian.crowd.event.application.ApplicationDirectoryRemovedEvent
 *  com.atlassian.crowd.event.application.ApplicationUpdatedEvent
 *  com.atlassian.crowd.event.directory.DirectoryDeletedEvent
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.Applications
 *  com.atlassian.crowd.model.event.AliasEvent
 *  com.atlassian.crowd.model.event.GroupEvent
 *  com.atlassian.crowd.model.event.GroupMembershipEvent
 *  com.atlassian.crowd.model.event.Operation
 *  com.atlassian.crowd.model.event.OperationEvent
 *  com.atlassian.crowd.model.event.UserEvent
 *  com.atlassian.crowd.model.event.UserMembershipEvent
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.ImmutableGroup
 *  com.atlassian.crowd.model.membership.InternalMembership
 *  com.atlassian.crowd.model.tombstone.AliasTombstone
 *  com.atlassian.crowd.model.tombstone.ApplicationUpdatedTombstone
 *  com.atlassian.crowd.model.tombstone.EventStreamTombstone
 *  com.atlassian.crowd.model.tombstone.GroupMembershipTombstone
 *  com.atlassian.crowd.model.tombstone.GroupTombstone
 *  com.atlassian.crowd.model.tombstone.MembershipTombstone
 *  com.atlassian.crowd.model.tombstone.UserMembershipTombstone
 *  com.atlassian.crowd.model.tombstone.UserTombstone
 *  com.atlassian.crowd.model.user.ImmutableUser
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.Combine
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.QueryBuilder$PartialEntityQuery
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Multimap
 *  org.apache.commons.lang3.tuple.Pair
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.crowd.event;

import com.atlassian.crowd.common.properties.SystemProperties;
import com.atlassian.crowd.dao.membership.InternalMembershipDao;
import com.atlassian.crowd.dao.tombstone.TombstoneDao;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.embedded.spi.GroupDao;
import com.atlassian.crowd.embedded.spi.UserDao;
import com.atlassian.crowd.event.DirectoryEvent;
import com.atlassian.crowd.event.EventStore;
import com.atlassian.crowd.event.EventTokenExpiredException;
import com.atlassian.crowd.event.Events;
import com.atlassian.crowd.event.TimestampBasedEventToken;
import com.atlassian.crowd.event.application.ApplicationDirectoryAddedEvent;
import com.atlassian.crowd.event.application.ApplicationDirectoryOrderUpdatedEvent;
import com.atlassian.crowd.event.application.ApplicationDirectoryRemovedEvent;
import com.atlassian.crowd.event.application.ApplicationUpdatedEvent;
import com.atlassian.crowd.event.directory.DirectoryDeletedEvent;
import com.atlassian.crowd.manager.tombstone.TombstoneManagerImpl;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.Applications;
import com.atlassian.crowd.model.event.AliasEvent;
import com.atlassian.crowd.model.event.GroupEvent;
import com.atlassian.crowd.model.event.GroupMembershipEvent;
import com.atlassian.crowd.model.event.Operation;
import com.atlassian.crowd.model.event.OperationEvent;
import com.atlassian.crowd.model.event.UserEvent;
import com.atlassian.crowd.model.event.UserMembershipEvent;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.ImmutableGroup;
import com.atlassian.crowd.model.membership.InternalMembership;
import com.atlassian.crowd.model.tombstone.AliasTombstone;
import com.atlassian.crowd.model.tombstone.ApplicationUpdatedTombstone;
import com.atlassian.crowd.model.tombstone.EventStreamTombstone;
import com.atlassian.crowd.model.tombstone.GroupMembershipTombstone;
import com.atlassian.crowd.model.tombstone.GroupTombstone;
import com.atlassian.crowd.model.tombstone.MembershipTombstone;
import com.atlassian.crowd.model.tombstone.UserMembershipTombstone;
import com.atlassian.crowd.model.tombstone.UserTombstone;
import com.atlassian.crowd.model.user.ImmutableUser;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.Combine;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class TimestampBasedEventStore
implements EventStore {
    public static final long TIMESTAMP_SLOP_TOLERANCE = TimeUnit.MINUTES.toMillis(5L);
    private static final Logger log = LoggerFactory.getLogger(TimestampBasedEventStore.class);
    private final Clock clock;
    private final UserDao userDao;
    private final TombstoneDao tombstoneDao;
    private final GroupDao groupDao;
    private final InternalMembershipDao membershipDao;
    private final int eventCountLimit;

    public TimestampBasedEventStore(UserDao userDao, TombstoneDao tombstoneDao, GroupDao groupDao, InternalMembershipDao membershipDao, int eventCountLimit) {
        this(userDao, groupDao, membershipDao, tombstoneDao, Clock.systemUTC(), eventCountLimit);
    }

    public TimestampBasedEventStore(UserDao userDao, GroupDao groupDao, InternalMembershipDao membershipDao, TombstoneDao tombstoneDao, Clock clock, int eventCountLimit) {
        this.userDao = userDao;
        this.groupDao = groupDao;
        this.membershipDao = membershipDao;
        this.tombstoneDao = tombstoneDao;
        this.clock = clock;
        this.eventCountLimit = eventCountLimit;
        log.debug("Created TimestampBasedEventStore with event limit: {}", (Object)eventCountLimit);
    }

    public String getCurrentEventToken(List<Long> directoryIds) {
        long nextCheckTimestamp = this.clock.millis() - TIMESTAMP_SLOP_TOLERANCE;
        String marshalledToken = new TimestampBasedEventToken(nextCheckTimestamp, directoryIds).marshall();
        log.debug("Returning event token '{}'", (Object)marshalledToken);
        return marshalledToken;
    }

    @Transactional
    public Events getNewEvents(String eventToken, List<Long> directoryIds) throws EventTokenExpiredException {
        log.debug("Got request with token '{}' for directories: {}", (Object)eventToken, directoryIds);
        TimestampBasedEventToken token = TimestampBasedEventToken.unmarshall(eventToken).orElseThrow(() -> new EventTokenExpiredException("Unrecognized token format"));
        this.checkIfTokenValid(token, directoryIds);
        long timestamp = token.timestamp;
        ImmutableList<OperationEvent> events = this.getDirectoryEvents(directoryIds, timestamp, true);
        return new Events(events, this.getCurrentEventToken(directoryIds));
    }

    @Transactional
    public Events getNewEvents(String eventToken, Application application) throws EventTokenExpiredException {
        log.debug("Got request with token '{}' for application: {}", (Object)eventToken, (Object)application);
        TimestampBasedEventToken token = TimestampBasedEventToken.unmarshall(eventToken).orElseThrow(() -> new EventTokenExpiredException("Unrecognized token format"));
        this.checkIfTokenValid(token, application);
        List<Long> directoryIds = Applications.getActiveDirectories((Application)application).stream().map(Directory::getId).collect(Collectors.toList());
        this.checkIfTokenValid(token, directoryIds);
        long timestamp = token.timestamp;
        ImmutableList<OperationEvent> events = this.getDirectoryEvents(directoryIds, timestamp, false);
        return new Events(events, this.getCurrentEventToken(directoryIds));
    }

    private ImmutableList<OperationEvent> getDirectoryEvents(List<Long> directoryIds, long timestamp, boolean addAliasEvents) throws EventTokenExpiredException {
        ImmutableList.Builder events = ImmutableList.builder();
        if (addAliasEvents) {
            List<OperationEvent> syntheticAliasEvents = this.getAliasEvents(timestamp);
            events.addAll(syntheticAliasEvents);
        }
        int eventCount = 0;
        for (long directoryId : directoryIds) {
            log.debug("Preparing events for directory {}", directoryIds);
            EventsHolder userEvents = this.getUserEvents(timestamp, directoryId);
            this.checkEventLimit(eventCount += userEvents.size());
            EventsHolder groupEvents = this.getGroupEvents(timestamp, directoryId);
            this.checkEventLimit(eventCount += groupEvents.size());
            EventsHolder membershipEvents = this.getMembershipEvents(timestamp, directoryId);
            this.checkEventLimit(eventCount += membershipEvents.size());
            events.addAll(membershipEvents.removals).addAll(userEvents.removals).addAll(groupEvents.removals).addAll(groupEvents.addsAndUpdates).addAll(userEvents.addsAndUpdates).addAll(membershipEvents.addsAndUpdates);
            log.debug("Finished processing directory {}, event count so far: {}", (Object)directoryId, (Object)eventCount);
        }
        return events.build();
    }

    private void checkIfTokenValid(TimestampBasedEventToken token, Application application) throws EventTokenExpiredException {
        List resetTombstones = this.tombstoneDao.getTombstonesAfter(token.timestamp, application.getId(), ApplicationUpdatedTombstone.class);
        if (!resetTombstones.isEmpty()) {
            log.debug("Found {} application updated tombstones after {}, reporting incremental sync unavailable", (Object)resetTombstones.size(), (Object)token.timestamp);
            throw new EventTokenExpiredException(String.format("Application configuration has changed", new Object[0]));
        }
        List aliasTombstones = this.tombstoneDao.getTombstonesAfter(token.timestamp, application.getId(), AliasTombstone.class);
        if (!aliasTombstones.isEmpty()) {
            log.debug("Found {} alias tombstones after {}, reporting incremental sync unavailable", (Object)resetTombstones.size(), (Object)token.timestamp);
            throw new EventTokenExpiredException(String.format("Aliasing configuration has changed", new Object[0]));
        }
    }

    private void checkIfTokenValid(TimestampBasedEventToken token, List<Long> directoryIds) throws EventTokenExpiredException {
        if (this.eventCountLimit <= 0) {
            log.debug("Event count limit is 0, reporting incremental sync unavailable");
            throw new EventTokenExpiredException("Incremental synchronisation is disabled for this server");
        }
        if (!Objects.equals(ImmutableList.copyOf(token.dirIds), ImmutableList.copyOf(directoryIds))) {
            log.debug("Requested events for a different directory set, reporting incremental sync unavailable");
            throw new EventTokenExpiredException("Application configuration has changed");
        }
        if (token.timestamp <= this.clock.millis() - TombstoneManagerImpl.TOMBSTONE_LIFETIME.toMillis() / 2L) {
            log.debug("Requested events older than the tombstone expiry threshold, reporting incremental sync unavailable");
            throw new EventTokenExpiredException("The token has expired");
        }
        List resetTombstones = this.tombstoneDao.getTombstonesAfter(token.timestamp, directoryIds, EventStreamTombstone.class);
        if (!resetTombstones.isEmpty()) {
            log.debug("Found {} event reset tombstones after {}, reporting incremental sync unavailable", (Object)resetTombstones.size(), (Object)token.timestamp);
            String firstReason = ((EventStreamTombstone)resetTombstones.get(0)).getReason();
            throw new EventTokenExpiredException(String.format("%s is not supported by incremental sync.", firstReason));
        }
    }

    private EventsHolder getUserEvents(long timestamp, long directoryId) throws EventTokenExpiredException {
        log.debug("Getting user events for directory {} since {}", (Object)directoryId, (Object)timestamp);
        QueryBuilder.PartialEntityQuery entityQuery = QueryBuilder.queryFor(User.class, (EntityDescriptor)EntityDescriptor.user());
        List removedUsers = this.tombstoneDao.getTombstonesAfter(timestamp, Collections.singleton(directoryId), UserTombstone.class).stream().map(UserTombstone::toUser).collect(Collectors.toList());
        EventsHolder userEvents = this.getEntityStream(directoryId, timestamp, removedUsers, entityQuery, (arg_0, arg_1) -> ((UserDao)this.userDao).search(arg_0, arg_1));
        log.debug("Got {} user adds/updates and {} user removals", (Object)userEvents.addsAndUpdates.size(), (Object)userEvents.removals.size());
        return userEvents;
    }

    private EventsHolder getGroupEvents(long timestamp, long directoryId) throws EventTokenExpiredException {
        log.debug("Getting group events for directory {} since {}", (Object)directoryId, (Object)timestamp);
        QueryBuilder.PartialEntityQuery entityQuery = QueryBuilder.queryFor(Group.class, (EntityDescriptor)EntityDescriptor.group());
        List removedGroups = this.tombstoneDao.getTombstonesAfter(timestamp, Collections.singleton(directoryId), GroupTombstone.class).stream().map(GroupTombstone::toGroup).collect(Collectors.toList());
        EventsHolder groupEvents = this.getEntityStream(directoryId, timestamp, removedGroups, entityQuery, (arg_0, arg_1) -> ((GroupDao)this.groupDao).search(arg_0, arg_1));
        log.debug("Got {} group adds/updates and {} group removals", (Object)groupEvents.addsAndUpdates.size(), (Object)groupEvents.removals.size());
        return groupEvents;
    }

    private EventsHolder getMembershipEvents(long timestamp, long directoryId) throws EventTokenExpiredException {
        boolean batching = (Boolean)SystemProperties.RECREATED_MEMBERSHIPS_BATCHING_ENABLED.getValue();
        long start = System.currentTimeMillis();
        log.debug("Getting membership events for directory {} since {}", (Object)directoryId, (Object)timestamp);
        Date cutoffDate = new Date(timestamp);
        List userMembershipTombstones = this.tombstoneDao.getTombstonesAfter(timestamp, Collections.singleton(directoryId), UserMembershipTombstone.class);
        this.checkEventLimit(userMembershipTombstones.size());
        List groupMembershipTombstones = this.tombstoneDao.getTombstonesAfter(timestamp, Collections.singleton(directoryId), GroupMembershipTombstone.class);
        this.checkEventLimit(groupMembershipTombstones.size() + userMembershipTombstones.size());
        Set<OperationEvent> addedMemberships = this.membershipDao.getMembershipsCreatedAfter(directoryId, cutoffDate, this.eventCountLimit + 1).stream().map(membership -> this.toEvent(Operation.CREATED, (InternalMembership)membership)).collect(Collectors.toSet());
        this.checkEventLimit(groupMembershipTombstones.size() + userMembershipTombstones.size() + addedMemberships.size());
        Stream<Object> recreatedMemberships = batching ? Stream.concat(this.getRecreatedMemberships(directoryId, userMembershipTombstones, t -> new UserMembershipEvent(Operation.CREATED, Long.valueOf(t.getDirectoryId()), t.getChildName(), t.getParentName()), EntityDescriptor.user(), addedMemberships), this.getRecreatedMemberships(directoryId, groupMembershipTombstones, t -> new GroupMembershipEvent(Operation.CREATED, Long.valueOf(t.getDirectoryId()), t.getChildName(), t.getParentName()), EntityDescriptor.group(), addedMemberships)) : Stream.concat(userMembershipTombstones.stream().filter(tombstone -> !addedMemberships.contains(new UserMembershipEvent(Operation.CREATED, Long.valueOf(tombstone.getDirectoryId()), tombstone.getChildName(), tombstone.getParentName()))).filter(tombstone -> this.membershipDao.isUserDirectMember(directoryId, tombstone.getChildName(), tombstone.getParentName())).map(membership -> new UserMembershipEvent(Operation.CREATED, Long.valueOf(membership.getDirectoryId()), membership.getChildName(), membership.getParentName())), groupMembershipTombstones.stream().filter(tombstone -> !addedMemberships.contains(new GroupMembershipEvent(Operation.CREATED, Long.valueOf(tombstone.getDirectoryId()), tombstone.getChildName(), tombstone.getParentName()))).filter(tombstone -> this.membershipDao.isGroupDirectMember(directoryId, tombstone.getChildName(), tombstone.getParentName())).map(membership -> new GroupMembershipEvent(Operation.CREATED, Long.valueOf(membership.getDirectoryId()), membership.getChildName(), membership.getParentName())));
        EventsHolder membershipEvents = new EventsHolder(Stream.concat(userMembershipTombstones.stream().map(tombstone -> new UserMembershipEvent(Operation.DELETED, Long.valueOf(directoryId), tombstone.getChildName(), tombstone.getParentName())), groupMembershipTombstones.stream().map(tombstone -> new GroupMembershipEvent(Operation.DELETED, Long.valueOf(directoryId), tombstone.getChildName(), tombstone.getParentName()))), Stream.concat(recreatedMemberships, addedMemberships.stream()));
        log.debug("Got {} membership adds/updates and {} membership removals", (Object)membershipEvents.addsAndUpdates.size(), (Object)membershipEvents.removals.size());
        log.debug("getMembershipEvents (batching={}, dirId={}) took {} ms", new Object[]{batching, directoryId, System.currentTimeMillis() - start});
        return membershipEvents;
    }

    private <T extends MembershipTombstone> Stream<OperationEvent> getRecreatedMemberships(long directoryId, List<T> tombstones, Function<T, OperationEvent> eventConstructor, EntityDescriptor entityDescriptor, Set<OperationEvent> addedMemberships) {
        ArrayList<Pair> candidates = new ArrayList<Pair>();
        HashSet<String> parents = new HashSet<String>();
        for (MembershipTombstone tombstone : tombstones) {
            OperationEvent event = eventConstructor.apply(tombstone);
            if (addedMemberships.contains(event)) continue;
            candidates.add(Pair.of((Object)tombstone, (Object)event));
            parents.add(tombstone.getParentName());
        }
        if (candidates.isEmpty()) {
            return Stream.empty();
        }
        MembershipQuery query = QueryBuilder.queryFor(String.class, (EntityDescriptor)entityDescriptor).childrenOf(EntityDescriptor.group()).withNames(parents).returningAtMost(-1);
        BiPredicate isMember = IdentifierUtils.containsIdentifierBiPredicate((Multimap)this.membershipDao.searchGroupedByName(directoryId, query));
        return candidates.stream().filter(candidate -> isMember.test(((MembershipTombstone)candidate.getLeft()).getParentName(), ((MembershipTombstone)candidate.getLeft()).getChildName())).map(Pair::getRight);
    }

    private <T extends DirectoryEntity> EventsHolder getEntityStream(long directoryId, long timestamp, List<T> removedEntities, QueryBuilder.PartialEntityQuery<T> entityQuery, BiFunction<Long, EntityQuery<T>, List<T>> searchFunction) throws EventTokenExpiredException {
        this.checkEventLimit(removedEntities.size());
        Date cutoffDate = new Date(timestamp);
        ImmutableSet created = ImmutableSet.copyOf((Collection)searchFunction.apply(directoryId, entityQuery.with((SearchRestriction)Restriction.on((Property)UserTermKeys.CREATED_DATE).greaterThan((Object)cutoffDate)).returningAtMost(this.eventCountLimit + 1)));
        this.checkEventLimit(removedEntities.size() + created.size());
        List potentiallyRecreatedEntities = removedEntities.stream().filter(arg_0 -> TimestampBasedEventStore.lambda$getEntityStream$14((Set)created, arg_0)).map(tombstone -> Restriction.on((Property)UserTermKeys.USERNAME).exactlyMatching((Object)tombstone.getName())).collect(Collectors.toList());
        List recreated = potentiallyRecreatedEntities.isEmpty() ? Collections.emptyList() : searchFunction.apply(directoryId, entityQuery.with((SearchRestriction)Combine.anyOf(potentiallyRecreatedEntities)).returningAtMost(-1));
        Stream<OperationEvent> createdStream = ((Stream)Stream.concat(created.stream(), recreated.stream()).unordered()).distinct().map(entity -> this.toEvent(Operation.CREATED, entity));
        List<T> updatedEntities = searchFunction.apply(directoryId, entityQuery.with((SearchRestriction)Combine.allOf((SearchRestriction[])new SearchRestriction[]{Restriction.on((Property)UserTermKeys.UPDATED_DATE).greaterThan((Object)cutoffDate), Combine.anyOf((SearchRestriction[])new SearchRestriction[]{Restriction.on((Property)UserTermKeys.CREATED_DATE).lessThan((Object)cutoffDate), Restriction.on((Property)UserTermKeys.CREATED_DATE).exactlyMatching((Object)cutoffDate)})})).returningAtMost(this.eventCountLimit + 1));
        this.checkEventLimit(removedEntities.size() + created.size() + updatedEntities.size());
        Stream<OperationEvent> updatedStream = updatedEntities.stream().map(user -> this.toEvent(Operation.UPDATED, user));
        Stream<OperationEvent> removedStream = removedEntities.stream().map(entity -> this.toEvent(Operation.DELETED, entity));
        return new EventsHolder(removedStream, Stream.concat(createdStream, updatedStream));
    }

    private <T extends DirectoryEntity> OperationEvent toEvent(Operation operation, T entity) {
        if (entity instanceof User) {
            User user = (User)entity;
            return new UserEvent(operation, Long.valueOf(user.getDirectoryId()), (User)ImmutableUser.from((User)user), null, null);
        }
        if (entity instanceof Group) {
            Group group = (Group)entity;
            return new GroupEvent(operation, Long.valueOf(group.getDirectoryId()), (Group)ImmutableGroup.from((Group)group), null, null);
        }
        throw new IllegalArgumentException("Unknown entity type " + entity);
    }

    private OperationEvent toEvent(Operation operation, InternalMembership membership) {
        switch (membership.getMembershipType()) {
            case GROUP_USER: {
                return new UserMembershipEvent(operation, membership.getDirectory().getId(), membership.getChildName(), membership.getParentName());
            }
            case GROUP_GROUP: {
                return new GroupMembershipEvent(operation, membership.getDirectory().getId(), membership.getChildName(), membership.getParentName());
            }
        }
        throw new IllegalArgumentException("Unknown membership type " + membership.getMembershipType());
    }

    private List<OperationEvent> getAliasEvents(long timestamp) {
        log.debug("Getting alias events since {}", (Object)timestamp);
        List aliasTombstones = this.tombstoneDao.getTombstonesAfter(timestamp, Collections.emptySet(), AliasTombstone.class);
        List<OperationEvent> events = aliasTombstones.stream().map(tomb -> AliasEvent.deleted((String)tomb.getUsername(), (long)tomb.getApplicationId())).collect(Collectors.toList());
        log.debug("Got {} alias events (tombstones)", (Object)events.size());
        return events;
    }

    private void checkEventLimit(int count) throws EventTokenExpiredException {
        if (count > this.eventCountLimit) {
            log.debug("Failed event limit check ({} of {}), returning failure", (Object)count, (Object)this.eventCountLimit);
            throw new EventTokenExpiredException("Too many events since previous incremental sync");
        }
    }

    public void storeOperationEvent(OperationEvent event) {
        if (event instanceof AliasEvent) {
            AliasEvent aliasEvent = (AliasEvent)event;
            this.tombstoneDao.storeAliasTombstone(aliasEvent.getApplicationId(), aliasEvent.getUsername());
        }
    }

    public void handleApplicationEvent(Object event) {
        Class<?> eventClass = event.getClass();
        if (ApplicationDirectoryRemovedEvent.class.isAssignableFrom(eventClass) || ApplicationDirectoryAddedEvent.class.isAssignableFrom(eventClass) || ApplicationDirectoryOrderUpdatedEvent.class.isAssignableFrom(eventClass) || DirectoryDeletedEvent.class.isAssignableFrom(eventClass)) {
            return;
        }
        if (event instanceof DirectoryEvent) {
            Long directoryId = ((DirectoryEvent)event).getDirectoryId();
            log.debug("Storing events tombstone for directory {} because of {}", (Object)directoryId, event);
            this.tombstoneDao.storeEventsTombstoneForDirectory(eventClass.getName(), directoryId.longValue());
        } else if (event instanceof ApplicationUpdatedEvent) {
            this.tombstoneDao.storeEventsTombstoneForApplication(((ApplicationUpdatedEvent)event).getApplicationId().longValue());
        } else {
            log.debug("Storing global events tombstone because of {}", event);
            this.tombstoneDao.storeEventsTombstone(eventClass.getName());
        }
    }

    private static /* synthetic */ boolean lambda$getEntityStream$14(Set created, DirectoryEntity entity) {
        return !created.contains(entity);
    }

    private static class EventsHolder {
        final Collection<OperationEvent> removals;
        final Collection<OperationEvent> addsAndUpdates;

        public EventsHolder(Stream<OperationEvent> removals, Stream<OperationEvent> addsAndUpdates) {
            this.removals = removals.collect(Collectors.toList());
            this.addsAndUpdates = addsAndUpdates.collect(Collectors.toList());
        }

        public int size() {
            return this.removals.size() + this.addsAndUpdates.size();
        }
    }
}

