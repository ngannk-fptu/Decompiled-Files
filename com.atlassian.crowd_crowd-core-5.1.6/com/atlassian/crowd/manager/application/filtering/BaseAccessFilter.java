/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.DirectoryProperties
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.impl.IdentifierSet
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ApplicationDirectoryMapping
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.Combine
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ListMultimap
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.manager.application.filtering;

import com.atlassian.crowd.directory.DirectoryProperties;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.impl.IdentifierSet;
import com.atlassian.crowd.manager.application.filtering.AccessFilter;
import com.atlassian.crowd.manager.application.filtering.GroupFilter;
import com.atlassian.crowd.manager.application.search.DirectoryManagerSearchWrapper;
import com.atlassian.crowd.manager.application.search.DirectoryQueryWithFilter;
import com.atlassian.crowd.manager.application.search.NamesUtil;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ApplicationDirectoryMapping;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.Combine;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.NullRestriction;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.annotation.Nonnull;

public class BaseAccessFilter
implements AccessFilter {
    private static final int QUERY_FOR_ALL_USERS_THRESHOLD = 1000;
    private final DirectoryManagerSearchWrapper directoryManagerSearchWrapper;
    private final Application application;
    private final Map<Long, GroupFilter> groupsWithAccess = new HashMap<Long, GroupFilter>();
    private final Map<Long, IdentifierSet> usersWithAccess = new HashMap<Long, IdentifierSet>();
    private final int queryForAllUsersThreshold;

    protected BaseAccessFilter(DirectoryManager directoryManager, Application application, boolean queryForAllUsers) {
        Preconditions.checkArgument((application.isFilteringUsersWithAccessEnabled() || application.isFilteringGroupsWithAccessEnabled() ? 1 : 0) != 0);
        this.directoryManagerSearchWrapper = new DirectoryManagerSearchWrapper(directoryManager);
        this.application = application;
        this.queryForAllUsersThreshold = queryForAllUsers ? -1 : 1000;
    }

    @Override
    public boolean requiresFiltering(Entity entityType) {
        return BaseAccessFilter.runForType(entityType, () -> ((Application)this.application).isFilteringUsersWithAccessEnabled(), () -> ((Application)this.application).isFilteringGroupsWithAccessEnabled());
    }

    @Override
    public boolean hasAccess(long directoryId, Entity entity, String name) {
        if (!this.requiresFiltering(entity)) {
            return true;
        }
        ApplicationDirectoryMapping mapping = this.getMappingOrFail(directoryId);
        Predicate<String> namesFilter = this.namesFilter(mapping, entity, (Collection<String>)ImmutableSet.of((Object)name));
        return namesFilter.test(name);
    }

    @Override
    public <T> Optional<DirectoryQueryWithFilter<T>> getDirectoryQueryWithFilter(Directory directory, MembershipQuery<T> query) {
        ApplicationDirectoryMapping mapping = this.getMappingOrFail(directory.getId());
        if (mapping.isAllowAllToAuthenticate() || !this.requiresFiltering(query.getEntityToMatch().getEntityType()) && !this.requiresFiltering(query.getEntityToReturn().getEntityType())) {
            return Optional.of(new DirectoryQueryWithFilter<T>(directory, query, UnaryOperator.identity()));
        }
        if (this.getGroupFilter(mapping).isEmpty()) {
            return Optional.empty();
        }
        DirectoryQueryWithFilter<T> directoryQueryWithFilter = query.isFindChildren() ? this.filterChildrenQuery(mapping, query) : this.filterParentsQuery(mapping, query);
        return directoryQueryWithFilter.getMembershipQuery().getEntityNamesToMatch().isEmpty() ? Optional.empty() : Optional.of(directoryQueryWithFilter);
    }

    @Override
    public <T> Optional<DirectoryQueryWithFilter<T>> getDirectoryQueryWithFilter(Directory directory, EntityQuery<T> query) {
        ApplicationDirectoryMapping mapping = this.getMappingOrFail(directory.getId());
        if (mapping.isAllowAllToAuthenticate() || !this.requiresFiltering(query.getEntityDescriptor().getEntityType())) {
            return Optional.of(new DirectoryQueryWithFilter<T>(directory, query, UnaryOperator.identity()));
        }
        if (this.getGroupFilter(mapping).isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(this.filterEntityQuery(mapping, query));
    }

    private <T> DirectoryQueryWithFilter<T> filterEntityQuery(ApplicationDirectoryMapping mapping, EntityQuery<T> query) {
        if (query.getSearchRestriction() != null && !(query.getSearchRestriction() instanceof NullRestriction) && !DirectoryProperties.cachesAnyUsers((Directory)mapping.getDirectory())) {
            return new DirectoryQueryWithFilter(mapping.getDirectory(), query.withAllResults(), list -> this.filter(mapping, query.getEntityDescriptor().getEntityType(), (Collection)list));
        }
        if (query.getEntityDescriptor().getEntityType() == Entity.USER) {
            MembershipQuery membershipQuery = QueryBuilder.queryFor((Class)query.getReturnType(), (EntityDescriptor)query.getEntityDescriptor()).with(query.getSearchRestriction()).childrenOf(EntityDescriptor.group()).withNames(this.getGroupFilter(mapping).getAllWithAccess()).startingAt(query.getStartIndex()).returningAtMost(query.getMaxResults());
            return new DirectoryQueryWithFilter(mapping.getDirectory(), membershipQuery, UnaryOperator.identity());
        }
        SearchRestriction restriction = Restriction.on((Property)GroupTermKeys.NAME).exactlyMatchingAny(this.getGroupFilter(mapping).getAllWithAccess());
        return new DirectoryQueryWithFilter(mapping.getDirectory(), query.withSearchRestriction(Combine.optionalAllOf((SearchRestriction[])new SearchRestriction[]{query.getSearchRestriction(), restriction})), UnaryOperator.identity());
    }

    private <T> DirectoryQueryWithFilter<T> filterChildrenQuery(ApplicationDirectoryMapping mapping, MembershipQuery<T> original) {
        MembershipQuery<T> filtered = this.filterToMatch(original, mapping);
        if (this.requiresFiltering(mapping, original.getEntityToMatch().getEntityType()) || filtered.equals(original)) {
            return new DirectoryQueryWithFilter<T>(mapping.getDirectory(), filtered, UnaryOperator.identity());
        }
        return new DirectoryQueryWithFilter(mapping.getDirectory(), original.withAllResults(), list -> this.filter(mapping, original.getEntityToReturn().getEntityType(), (Collection)list));
    }

    private <T> DirectoryQueryWithFilter<T> filterParentsQuery(ApplicationDirectoryMapping mapping, MembershipQuery<T> original) {
        Entity toReturn = original.getEntityToReturn().getEntityType();
        if (this.requiresFiltering(mapping, toReturn)) {
            return new DirectoryQueryWithFilter(mapping.getDirectory(), original.withAllResults(), list -> this.filter(mapping, toReturn, (Collection)list));
        }
        if (this.isSimpleUserParentsQuery(original)) {
            return new DirectoryQueryWithFilter<T>(mapping.getDirectory(), original.withAllResults(), this.allGroupsOrNone(mapping));
        }
        return new DirectoryQueryWithFilter<T>(mapping.getDirectory(), this.filterToMatch(original, mapping), UnaryOperator.identity());
    }

    private boolean requiresFiltering(ApplicationDirectoryMapping mapping, Entity entityType) {
        return this.requiresFiltering(entityType) && !mapping.isAllowAllToAuthenticate();
    }

    private Predicate<String> namesFilter(ApplicationDirectoryMapping mapping, Entity entityType, Collection<String> names) {
        if (mapping.isAllowAllToAuthenticate()) {
            return name -> true;
        }
        if (names.isEmpty() || mapping.getAuthorisedGroupNames().isEmpty()) {
            return name -> false;
        }
        return BaseAccessFilter.runForType(entityType, () -> arg_0 -> ((IdentifierSet)this.getUsersWithAccess(mapping, names)).contains(arg_0), () -> this.getGroupFilter(mapping)::hasAccess);
    }

    private IdentifierSet getUsersWithAccess(ApplicationDirectoryMapping mapping, Collection<String> names) {
        Long directoryId = mapping.getDirectory().getId();
        if (names.size() >= this.queryForAllUsersThreshold || this.usersWithAccess.containsKey(directoryId)) {
            return this.usersWithAccess.computeIfAbsent(directoryId, id -> this.computeUsersWithAccess(mapping));
        }
        MembershipQuery query = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group()).parentsOf(EntityDescriptor.user()).withNames(names).returningAtMost(-1);
        ListMultimap userGroups = this.directoryManagerSearchWrapper.searchDirectGroupRelationshipsGroupedByName(directoryId, query);
        return new IdentifierSet(Maps.filterValues((Map)userGroups.asMap(), this.getGroupFilter(mapping)::anyHasAccess).keySet());
    }

    private IdentifierSet computeUsersWithAccess(ApplicationDirectoryMapping mapping) {
        MembershipQuery membershipQuery = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.user()).childrenOf(EntityDescriptor.group()).withNames(this.getGroupFilter(mapping).getAllWithAccess()).returningAtMost(-1);
        return new IdentifierSet(this.directoryManagerSearchWrapper.searchDirectGroupRelationships(mapping.getDirectory().getId(), membershipQuery));
    }

    private <T> UnaryOperator<List<T>> allGroupsOrNone(ApplicationDirectoryMapping mapping) {
        GroupFilter groupFilter = this.getGroupFilter(mapping);
        return results -> groupFilter.anyHasAccess(NamesUtil.namesOf(results)) ? results : ImmutableList.of();
    }

    @Nonnull
    private ApplicationDirectoryMapping getMappingOrFail(long directoryId) {
        return Objects.requireNonNull(this.application.getApplicationDirectoryMapping(directoryId));
    }

    private GroupFilter getGroupFilter(ApplicationDirectoryMapping mapping) {
        return this.groupsWithAccess.computeIfAbsent(mapping.getDirectory().getId(), id -> new GroupFilter(this.directoryManagerSearchWrapper, mapping));
    }

    private <T> MembershipQuery<T> filterToMatch(MembershipQuery<T> original, ApplicationDirectoryMapping mapping) {
        return original.withEntityNames(this.filter(mapping, original.getEntityToMatch().getEntityType(), original.getEntityNamesToMatch()));
    }

    private <T> List<T> filter(ApplicationDirectoryMapping mapping, Entity entityType, Collection<T> entities) {
        Predicate<String> namesFilter = this.namesFilter(mapping, entityType, NamesUtil.namesOf(entities));
        return NamesUtil.filterByName(entities, namesFilter);
    }

    private <T> boolean isSimpleUserParentsQuery(MembershipQuery<T> original) {
        return (original.getSearchRestriction() == null || original.getSearchRestriction() instanceof NullRestriction) && original.getEntityNamesToMatch().size() == 1 && original.getEntityToMatch().getEntityType() == Entity.USER;
    }

    private static <T> T runForType(Entity entityType, Supplier<T> supplierWhenUser, Supplier<T> supplierWhenGroup) {
        switch (entityType) {
            case USER: {
                return supplierWhenUser.get();
            }
            case GROUP: {
                return supplierWhenGroup.get();
            }
        }
        throw new IllegalArgumentException("Unsupported entity type " + entityType);
    }
}

