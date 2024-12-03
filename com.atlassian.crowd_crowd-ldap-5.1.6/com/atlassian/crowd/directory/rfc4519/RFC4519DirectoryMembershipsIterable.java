/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.model.group.ImmutableMembership
 *  com.atlassian.crowd.model.group.Membership
 *  com.atlassian.crowd.model.group.Membership$MembershipIterationException
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction$BooleanLogic
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestrictionImpl
 *  com.atlassian.crowd.search.query.entity.restriction.MatchMode
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.PropertyImpl
 *  com.atlassian.crowd.search.query.entity.restriction.TermRestriction
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.rfc4519;

import com.atlassian.crowd.directory.NamedLdapEntity;
import com.atlassian.crowd.directory.RFC4519Directory;
import com.atlassian.crowd.directory.ldap.mapper.ContextMapperWithRequiredAttributes;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.model.group.ImmutableMembership;
import com.atlassian.crowd.model.group.Membership;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestrictionImpl;
import com.atlassian.crowd.search.query.entity.restriction.MatchMode;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.PropertyImpl;
import com.atlassian.crowd.search.query.entity.restriction.TermRestriction;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RFC4519DirectoryMembershipsIterable
implements Iterable<Membership> {
    private static final Logger logger = LoggerFactory.getLogger(RFC4519DirectoryMembershipsIterable.class);
    private static final int MISSING_NAMES_PARTITION_SIZE = Integer.getInteger("com.atlassian.crowd.directory.RFC4519DirectoryMembershipsIterable.MISSING_NAMES_PARTITION_SIZE", 1000);
    private final RFC4519Directory connector;
    private final Map<LdapName, Optional<String>> users;
    private final Map<LdapName, Optional<String>> groups;
    private final Map<LdapName, String> groupsToInclude;
    private final int membershipBatchSize;
    private final ContextMapperWithRequiredAttributes<LdapName> dnMapper;

    RFC4519DirectoryMembershipsIterable(RFC4519Directory connector, Map<LdapName, String> users, Map<LdapName, String> groups, Map<LdapName, String> groupsToInclude, int membershipBatchSize, ContextMapperWithRequiredAttributes<LdapName> dnMapper) {
        this.connector = connector;
        this.users = users.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> Optional.ofNullable(entry.getValue())));
        this.groups = groups.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> Optional.ofNullable(entry.getValue())));
        this.groupsToInclude = groupsToInclude;
        this.membershipBatchSize = membershipBatchSize;
        this.dnMapper = dnMapper;
    }

    @Override
    public Iterator<Membership> iterator() {
        Iterable partitioned = Iterables.partition(this.groupsToInclude.entrySet(), (int)this.membershipBatchSize);
        return StreamSupport.stream(partitioned.spliterator(), false).map(partition -> {
            try {
                return this.getMemberships((Collection<Map.Entry<LdapName, String>>)partition);
            }
            catch (OperationFailedException e) {
                throw new Membership.MembershipIterationException((Throwable)e);
            }
        }).flatMap(iterable -> StreamSupport.stream(iterable.spliterator(), false)).iterator();
    }

    private Iterable<Membership> getMemberships(Collection<Map.Entry<LdapName, String>> groups) throws OperationFailedException {
        if (!groups.iterator().hasNext()) {
            return Collections.emptyList();
        }
        List<MembershipHolder> memberships = this.searchChildrenDns(groups);
        this.lookupMissingNames(memberships);
        return this.resolveMemberships(memberships);
    }

    protected void lookupMissingNames(List<MembershipHolder> memberships) throws OperationFailedException {
        Set<LdapName> missingLdapNames = this.findMissingEntriesInCacheAndCreateRestrictionsForThem(memberships);
        if (!missingLdapNames.isEmpty()) {
            this.updateCache(missingLdapNames);
        }
    }

    private List<MembershipHolder> searchChildrenDns(Collection<Map.Entry<LdapName, String>> groups) {
        long start = System.currentTimeMillis();
        logger.info("Searching for children of {} groups", (Object)groups.size());
        ArrayList<MembershipHolder> memberships = new ArrayList<MembershipHolder>();
        for (Map.Entry<LdapName, String> groupDnToName : groups) {
            try {
                memberships.add(new MembershipHolder(groupDnToName.getValue(), (Set<LdapName>)ImmutableSet.copyOf(this.connector.findDirectMembersOfGroup(groupDnToName.getKey(), this.dnMapper))));
            }
            catch (OperationFailedException e) {
                throw new Membership.MembershipIterationException((Throwable)e);
            }
        }
        logger.info("Found {} children for {} groups in {} ms", new Object[]{this.countMembershipsChildren(memberships), groups.size(), System.currentTimeMillis() - start});
        return memberships;
    }

    @VisibleForTesting
    long countMembershipsChildren(List<MembershipHolder> memberships) {
        return memberships.stream().map(MembershipHolder::getChildren).mapToLong(Collection::size).sum();
    }

    private Set<LdapName> findMissingEntriesInCacheAndCreateRestrictionsForThem(List<MembershipHolder> memberships) {
        return memberships.stream().map(holder -> holder.getChildren().stream().filter(child -> !this.users.containsKey(child) && !this.groups.containsKey(child)).collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toSet());
    }

    @SuppressFBWarnings(value={"LDAP_INJECTION"}, justification="No user input - the String was just retrieved from the LdapName")
    private void updateCache(Collection<LdapName> missingNames) throws OperationFailedException {
        long start = System.currentTimeMillis();
        logger.info("Fetching details for {} entities for membership resolution", (Object)missingNames.size());
        Set<TermRestriction<String>> restrictionsForMissingNames = missingNames.stream().map(n -> new TermRestriction((Property)new PropertyImpl("distinguishedName", String.class), MatchMode.EXACTLY_MATCHES, (Object)n.toString())).collect(Collectors.toSet());
        Collection<NamedLdapEntity> userMembers = this.searchUsers(restrictionsForMissingNames, MISSING_NAMES_PARTITION_SIZE);
        Collection<String> usersDns = this.getDns(userMembers);
        List<TermRestriction<String>> restrictionsForChildrenGroups = this.filterOutRestrictionsForDns(restrictionsForMissingNames, usersDns);
        Collection<NamedLdapEntity> groupsMembers = this.searchGroups(restrictionsForChildrenGroups, MISSING_NAMES_PARTITION_SIZE);
        for (TermRestriction<String> restrictionForNotFoundChild : this.filterOutRestrictionsForDns(restrictionsForChildrenGroups, this.getDns(groupsMembers))) {
            try {
                this.users.put(new LdapName((String)restrictionForNotFoundChild.getValue()), Optional.empty());
                this.groups.put(new LdapName((String)restrictionForNotFoundChild.getValue()), Optional.empty());
            }
            catch (InvalidNameException e) {
                throw new OperationFailedException((Throwable)e);
            }
        }
        userMembers.forEach(n -> this.users.put(n.getDn(), Optional.of(n.getName())));
        groupsMembers.forEach(n -> this.groups.put(n.getDn(), Optional.of(n.getName())));
        logger.debug("Updating cache took {} ms", (Object)(System.currentTimeMillis() - start));
    }

    private List<TermRestriction<String>> filterOutRestrictionsForDns(Collection<TermRestriction<String>> restrictions, Collection<String> dns) {
        return restrictions.stream().filter(r -> !dns.contains(r.getValue())).collect(Collectors.toList());
    }

    private Collection<NamedLdapEntity> searchUsers(Collection<TermRestriction<String>> restrictions, int partitionSize) throws OperationFailedException {
        logger.debug("Searching for {} users in directory", (Object)restrictions.size());
        long start = System.currentTimeMillis();
        ArrayList<NamedLdapEntity> result = new ArrayList<NamedLdapEntity>();
        for (List batchedRestrictions : Iterables.partition(restrictions, (int)partitionSize)) {
            EntityQuery query = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.user()).with((SearchRestriction)this.prepareBooleanRestrictionForTermRestrictions(batchedRestrictions)).returningAtMost(-1);
            if (logger.isTraceEnabled()) {
                logger.trace("Searching user objects using query {}", (Object)query.toString());
            }
            result.addAll(this.connector.searchUserObjects(query, new NamedLdapEntity.NamedEntityMapper(this.connector.getLdapPropertiesMapper().getUserNameAttribute())));
        }
        logger.debug("Found {} users in directory in {} ms", (Object)result.size(), (Object)(System.currentTimeMillis() - start));
        return result;
    }

    private Collection<NamedLdapEntity> searchGroups(Collection<TermRestriction<String>> restrictions, int partitionSize) throws OperationFailedException {
        logger.debug("Searching for {} groups in directory", (Object)restrictions.size());
        long start = System.currentTimeMillis();
        ArrayList<NamedLdapEntity> result = new ArrayList<NamedLdapEntity>();
        for (List batchedRestrictions : Iterables.partition(restrictions, (int)partitionSize)) {
            result.addAll(this.connector.searchGroupObjects(QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group()).with((SearchRestriction)this.prepareBooleanRestrictionForTermRestrictions(batchedRestrictions)).returningAtMost(-1), new NamedLdapEntity.NamedEntityMapper(this.connector.getLdapPropertiesMapper().getGroupNameAttribute())));
        }
        logger.debug("Found {} groups in directory in {} ms", (Object)result.size(), (Object)(System.currentTimeMillis() - start));
        return result;
    }

    private BooleanRestrictionImpl prepareBooleanRestrictionForTermRestrictions(List<TermRestriction<String>> batchedRestrictions) {
        return new BooleanRestrictionImpl(BooleanRestriction.BooleanLogic.OR, batchedRestrictions.toArray(new SearchRestriction[batchedRestrictions.size()]));
    }

    private Collection<Membership> resolveMemberships(Collection<MembershipHolder> memberships) {
        return memberships.stream().map(this::mapHolderToMembership).collect(Collectors.toList());
    }

    private ImmutableMembership mapHolderToMembership(MembershipHolder m) {
        return new ImmutableMembership(m.getName(), (Iterable)m.getChildren().stream().map(this.users::get).filter(Objects::nonNull).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()), (Iterable)m.getChildren().stream().map(this.groups::get).filter(Objects::nonNull).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }

    private Collection<String> getDns(Collection<NamedLdapEntity> ldapEntities) {
        return ldapEntities.stream().map(NamedLdapEntity::getDn).map(LdapName::toString).collect(Collectors.toList());
    }

    @VisibleForTesting
    ContextMapperWithRequiredAttributes<LdapName> getDnMapper() {
        return this.dnMapper;
    }

    static class MembershipHolder {
        private final String name;
        private final Set<LdapName> children;

        MembershipHolder(String name, Set<LdapName> children) {
            this.name = name;
            this.children = (Set)Preconditions.checkNotNull(children, (Object)"children cannot be null");
        }

        public String getName() {
            return this.name;
        }

        public Set<LdapName> getChildren() {
            return this.children;
        }
    }
}

