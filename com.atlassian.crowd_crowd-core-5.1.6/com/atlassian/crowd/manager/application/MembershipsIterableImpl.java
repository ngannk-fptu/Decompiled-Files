/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.common.util.ProxyUtil
 *  com.atlassian.crowd.manager.application.ApplicationService$MembershipsIterable
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.Applications
 *  com.atlassian.crowd.model.group.ImmutableMembership
 *  com.atlassian.crowd.model.group.Membership
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.google.common.collect.AbstractIterator
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.ListMultimap
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.manager.application;

import com.atlassian.crowd.common.util.ProxyUtil;
import com.atlassian.crowd.manager.application.ApplicationService;
import com.atlassian.crowd.manager.application.canonicality.OptimizedCanonicalityChecker;
import com.atlassian.crowd.manager.application.filtering.AccessFilter;
import com.atlassian.crowd.manager.application.filtering.AccessFilterFactory;
import com.atlassian.crowd.manager.application.search.MembershipSearchStrategy;
import com.atlassian.crowd.manager.application.search.SearchStrategyFactory;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.Applications;
import com.atlassian.crowd.model.group.ImmutableMembership;
import com.atlassian.crowd.model.group.Membership;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MembershipsIterableImpl
implements ApplicationService.MembershipsIterable {
    private static final Logger logger = LoggerFactory.getLogger(MembershipsIterableImpl.class);
    private static final int GET_MEMBERSHIPS_BATCH_SIZE = 1000;
    private static final EntityQuery<String> ALL_GROUPS_QUERY = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group()).returningAtMost(-1);
    private final List<String> allGroups;
    private final MembershipSearchStrategy membershipSearchStrategy;

    protected MembershipsIterableImpl(DirectoryManager directoryManager, SearchStrategyFactory searchStrategyFactory, Application application, AccessFilterFactory accessFilterFactory) {
        List activeDirectories = Applications.getActiveDirectories((Application)application);
        AccessFilter accessFilter = accessFilterFactory.create(application, true);
        OptimizedCanonicalityChecker canonicalityChecker = new OptimizedCanonicalityChecker(directoryManager, activeDirectories);
        this.allGroups = searchStrategyFactory.createGroupSearchStrategy(true, activeDirectories, accessFilter).searchGroups(ALL_GROUPS_QUERY);
        this.membershipSearchStrategy = searchStrategyFactory.createMembershipSearchStrategy(application.isMembershipAggregationEnabled(), activeDirectories, canonicalityChecker, accessFilter);
    }

    public int groupCount() {
        return this.allGroups.size();
    }

    public Iterator<Membership> iterator() {
        return new AbstractIterator<Membership>(){
            final long start = System.currentTimeMillis();
            final Iterator<List<String>> batchIterator = Iterables.partition((Iterable)MembershipsIterableImpl.access$000(MembershipsIterableImpl.this), (int)1000).iterator();
            Iterator<? extends Membership> current = Collections.emptyIterator();

            protected Membership computeNext() {
                while (!this.current.hasNext() && this.batchIterator.hasNext()) {
                    this.current = MembershipsIterableImpl.this.getMemberships(this.batchIterator.next());
                }
                if (this.current.hasNext()) {
                    return this.current.next();
                }
                logger.debug("Memberships iteration took {}s", (Object)((double)(System.currentTimeMillis() - this.start) / 1000.0));
                return (Membership)this.endOfData();
            }
        };
    }

    private Iterator<? extends Membership> getMemberships(List<String> groupNames) {
        MembershipQuery userNamesQuery = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.user()).childrenOf(EntityDescriptor.group()).withNames(groupNames).startingAt(0).returningAtMost(-1);
        MembershipQuery childGroupNames = userNamesQuery.withEntityToReturn(EntityDescriptor.group());
        ListMultimap users = this.membershipSearchStrategy.searchDirectGroupRelationshipsGroupedByName(userNamesQuery);
        ListMultimap groups = this.membershipSearchStrategy.searchDirectGroupRelationshipsGroupedByName(childGroupNames);
        return groupNames.stream().map(name -> new ImmutableMembership(name, (Iterable)users.get(name), (Iterable)groups.get(name))).iterator();
    }

    public static ApplicationService.MembershipsIterable runWithClassLoader(final ClassLoader classLoader, final ApplicationService.MembershipsIterable original) {
        ApplicationService.MembershipsIterable iterable = new ApplicationService.MembershipsIterable(){

            public int groupCount() {
                return original.groupCount();
            }

            public Iterator<Membership> iterator() {
                return (Iterator)ProxyUtil.runWithContextClassLoader((ClassLoader)classLoader, (Object)original.iterator());
            }
        };
        return (ApplicationService.MembershipsIterable)ProxyUtil.runWithContextClassLoader((ClassLoader)classLoader, (Object)iterable);
    }

    static /* synthetic */ List access$000(MembershipsIterableImpl x0) {
        return x0.allGroups;
    }
}

