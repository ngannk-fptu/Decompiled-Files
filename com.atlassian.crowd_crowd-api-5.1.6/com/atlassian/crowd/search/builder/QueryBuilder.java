/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.crowd.search.builder;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.query.entity.AliasQuery;
import com.atlassian.crowd.search.query.entity.ApplicationQuery;
import com.atlassian.crowd.search.query.entity.AuthenticatedTokenQuery;
import com.atlassian.crowd.search.query.entity.DirectoryQuery;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.GroupQuery;
import com.atlassian.crowd.search.query.entity.UserQuery;
import com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl;
import com.atlassian.crowd.search.query.membership.GroupMembersOfGroupQuery;
import com.atlassian.crowd.search.query.membership.GroupMembershipQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.search.query.membership.UserMembersOfGroupQuery;
import com.atlassian.crowd.search.query.membership.UserMembershipQuery;
import java.util.Collection;
import org.apache.commons.lang3.Validate;

public class QueryBuilder {
    public static final SearchRestriction NULL_RESTRICTION = NullRestrictionImpl.INSTANCE;
    private static final int DEFAULT_START_INDEX = 0;

    public static <T> PartialEntityQuery<T> queryFor(Class<T> returnType, EntityDescriptor entity) {
        Validate.notNull(returnType, (String)"returnType", (Object[])new Object[0]);
        Validate.notNull((Object)entity, (String)"entity", (Object[])new Object[0]);
        return new PartialEntityQuery<T>(returnType, entity);
    }

    public static <T> EntityQuery<T> queryFor(Class<T> returnType, EntityDescriptor entity, SearchRestriction searchRestriction, int startIndex, int maxResults) {
        if (Entity.USER.equals((Object)entity.getEntityType())) {
            return new UserQuery<T>(returnType, searchRestriction, startIndex, maxResults);
        }
        if (Entity.GROUP.equals((Object)entity.getEntityType())) {
            return new GroupQuery<T>(returnType, entity.getGroupType(), searchRestriction, startIndex, maxResults);
        }
        if (Entity.ALIAS.equals((Object)entity.getEntityType())) {
            return new AliasQuery(searchRestriction, startIndex, maxResults);
        }
        if (Entity.APPLICATION.equals((Object)entity.getEntityType())) {
            return new ApplicationQuery(searchRestriction, startIndex, maxResults);
        }
        if (Entity.DIRECTORY.equals((Object)entity.getEntityType())) {
            return new DirectoryQuery(searchRestriction, startIndex, maxResults);
        }
        if (Entity.TOKEN.equals((Object)entity.getEntityType())) {
            return new AuthenticatedTokenQuery(searchRestriction, startIndex, maxResults);
        }
        throw new IllegalStateException("Unknown entity type <" + entity + "> is not supported by the builder");
    }

    @Deprecated
    public static <T> MembershipQuery<T> createMembershipQuery(int maxResults, int startIndex, boolean findMembers, EntityDescriptor entityToReturn, Class<T> returnType, EntityDescriptor entityToMatch, String nameToMatch) {
        return QueryBuilder.createMembershipQuery(maxResults, startIndex, findMembers, entityToReturn, returnType, entityToMatch, NULL_RESTRICTION, nameToMatch);
    }

    @Deprecated
    public static <T> MembershipQuery<T> createMembershipQuery(int maxResults, int startIndex, boolean findMembers, EntityDescriptor entityToReturn, Class<T> returnType, EntityDescriptor entityToMatch, String ... namesToMatch) {
        return QueryBuilder.createMembershipQuery(maxResults, startIndex, findMembers, entityToReturn, returnType, entityToMatch, NULL_RESTRICTION, namesToMatch);
    }

    public static <T> MembershipQuery<T> createMembershipQuery(int maxResults, int startIndex, boolean findMembers, EntityDescriptor entityToReturn, Class<T> returnType, EntityDescriptor entityToMatch, SearchRestriction searchRestriction, String ... namesToMatch) {
        if (findMembers && (entityToReturn.equals(EntityDescriptor.group()) || entityToReturn.equals(EntityDescriptor.role()))) {
            return new GroupMembersOfGroupQuery<T>(returnType, findMembers, entityToMatch, entityToReturn, startIndex, maxResults, searchRestriction, namesToMatch);
        }
        if (findMembers && entityToReturn.equals(EntityDescriptor.user())) {
            return new UserMembersOfGroupQuery<T>(returnType, findMembers, entityToMatch, entityToReturn, startIndex, maxResults, searchRestriction, namesToMatch);
        }
        if (!findMembers && (entityToReturn.equals(EntityDescriptor.group()) || entityToReturn.equals(EntityDescriptor.role()))) {
            return new GroupMembershipQuery<T>(returnType, findMembers, entityToMatch, entityToReturn, startIndex, maxResults, searchRestriction, namesToMatch);
        }
        if (!findMembers && entityToReturn.equals(EntityDescriptor.user())) {
            return new UserMembershipQuery<T>(returnType, findMembers, entityToMatch, entityToReturn, startIndex, maxResults, searchRestriction, namesToMatch);
        }
        throw new IllegalStateException("What the f**k happened!");
    }

    public static class PartialEntityQueryWithStartIndex<T> {
        private final Class<T> returnType;
        private final EntityDescriptor entity;
        private final SearchRestriction restriction;
        private final int startIndex;

        public PartialEntityQueryWithStartIndex(Class<T> returnType, EntityDescriptor entity, SearchRestriction restriction, int startIndex) {
            this.returnType = returnType;
            this.entity = entity;
            this.startIndex = startIndex;
            this.restriction = restriction;
        }

        public EntityQuery<T> returningAtMost(int maxResults) {
            return QueryBuilder.queryFor(this.returnType, this.entity, this.restriction, this.startIndex, maxResults);
        }
    }

    public static class PartialEntityQueryWithRestriction<T> {
        private final Class<T> returnType;
        private final EntityDescriptor entity;
        private final SearchRestriction restriction;

        public PartialEntityQueryWithRestriction(Class<T> returnType, EntityDescriptor entity, SearchRestriction restriction) {
            this.returnType = returnType;
            this.entity = entity;
            this.restriction = restriction;
        }

        public PartialEntityQueryWithStartIndex<T> startingAt(int index) {
            return new PartialEntityQueryWithStartIndex<T>(this.returnType, this.entity, this.restriction, index);
        }

        public EntityQuery<T> returningAtMost(int maxResults) {
            return QueryBuilder.queryFor(this.returnType, this.entity, this.restriction, 0, maxResults);
        }

        public PartialMembershipQueryWithEntityToMatch<T> childrenOf(EntityDescriptor entityToMatch) {
            return new PartialMembershipQueryWithEntityToMatch<T>(this.returnType, this.entity, entityToMatch, true, this.restriction);
        }

        public PartialMembershipQueryWithEntityToMatch<T> parentsOf(EntityDescriptor entityToMatch) {
            return new PartialMembershipQueryWithEntityToMatch<T>(this.returnType, this.entity, entityToMatch, false, this.restriction);
        }
    }

    public static class PartialMembershipQueryWithStartIndex<T> {
        private final Class<T> returnType;
        private final EntityDescriptor entityToReturn;
        private final EntityDescriptor entityToMatch;
        private final boolean findMembers;
        private final int startIndex;
        private final SearchRestriction searchRestriction;
        private final String[] namesToMatch;

        @Deprecated
        public PartialMembershipQueryWithStartIndex(Class<T> returnType, EntityDescriptor entityToReturn, EntityDescriptor entityToMatch, boolean findMembers, String nameToMatch, int startIndex) {
            this(returnType, entityToReturn, entityToMatch, findMembers, startIndex, (SearchRestriction)NullRestrictionImpl.INSTANCE, nameToMatch);
        }

        public PartialMembershipQueryWithStartIndex(Class<T> returnType, EntityDescriptor entityToReturn, EntityDescriptor entityToMatch, boolean findMembers, int startIndex, SearchRestriction searchRestriction, String ... namesToMatch) {
            this.returnType = returnType;
            this.entityToReturn = entityToReturn;
            this.entityToMatch = entityToMatch;
            this.findMembers = findMembers;
            this.startIndex = startIndex;
            this.searchRestriction = searchRestriction;
            this.namesToMatch = namesToMatch;
        }

        public MembershipQuery<T> returningAtMost(int maxResults) {
            return QueryBuilder.createMembershipQuery(maxResults, this.startIndex, this.findMembers, this.entityToReturn, this.returnType, this.entityToMatch, this.searchRestriction, this.namesToMatch);
        }
    }

    public static class PartialMembershipQueryWithNameToMatch<T> {
        private final Class<T> returnType;
        private final EntityDescriptor entityToReturn;
        private final EntityDescriptor entityToMatch;
        private final boolean findMembers;
        private final SearchRestriction searchRestriction;
        private final String[] namesToMatch;

        @Deprecated
        public PartialMembershipQueryWithNameToMatch(Class<T> returnType, EntityDescriptor entityToReturn, EntityDescriptor entityToMatch, boolean findMembers, String nameToMatch) {
            this(returnType, entityToReturn, entityToMatch, findMembers, NULL_RESTRICTION, nameToMatch);
        }

        public PartialMembershipQueryWithNameToMatch(Class<T> returnType, EntityDescriptor entityToReturn, EntityDescriptor entityToMatch, boolean findMembers, SearchRestriction searchRestriction, String ... namesToMatch) {
            this.returnType = returnType;
            this.entityToReturn = entityToReturn;
            this.entityToMatch = entityToMatch;
            this.findMembers = findMembers;
            this.searchRestriction = searchRestriction;
            this.namesToMatch = namesToMatch;
        }

        public PartialMembershipQueryWithStartIndex<T> startingAt(int index) {
            return new PartialMembershipQueryWithStartIndex<T>(this.returnType, this.entityToReturn, this.entityToMatch, this.findMembers, index, this.searchRestriction, this.namesToMatch);
        }

        public MembershipQuery<T> returningAtMost(int maxResults) {
            return QueryBuilder.createMembershipQuery(maxResults, 0, this.findMembers, this.entityToReturn, this.returnType, this.entityToMatch, this.searchRestriction, this.namesToMatch);
        }
    }

    public static class PartialMembershipQueryWithEntityToMatch<T> {
        private final Class<T> returnType;
        private final EntityDescriptor entityToReturn;
        private final EntityDescriptor entityToMatch;
        private final boolean findMembers;
        private final SearchRestriction searchRestriction;

        @Deprecated
        public PartialMembershipQueryWithEntityToMatch(Class<T> returnType, EntityDescriptor entityToReturn, EntityDescriptor entityToMatch, boolean findMembers) {
            this(returnType, entityToReturn, entityToMatch, findMembers, NULL_RESTRICTION);
        }

        public PartialMembershipQueryWithEntityToMatch(Class<T> returnType, EntityDescriptor entityToReturn, EntityDescriptor entityToMatch, boolean findMembers, SearchRestriction searchRestriction) {
            this.returnType = returnType;
            this.entityToReturn = entityToReturn;
            this.entityToMatch = entityToMatch;
            this.findMembers = findMembers;
            this.searchRestriction = searchRestriction;
        }

        public PartialMembershipQueryWithNameToMatch<T> withName(String name) {
            return this.withNames(name);
        }

        public PartialMembershipQueryWithNameToMatch<T> withNames(String ... names) {
            return new PartialMembershipQueryWithNameToMatch<T>(this.returnType, this.entityToReturn, this.entityToMatch, this.findMembers, this.searchRestriction, names);
        }

        public PartialMembershipQueryWithNameToMatch<T> withNames(Collection<String> names) {
            return this.withNames(names.toArray(new String[names.size()]));
        }
    }

    public static class PartialEntityQuery<T> {
        private final Class<T> returnType;
        private final EntityDescriptor entity;

        public PartialEntityQuery(Class<T> returnType, EntityDescriptor entity) {
            this.returnType = returnType;
            this.entity = entity;
        }

        public PartialEntityQueryWithRestriction<T> with(SearchRestriction restriction) {
            return new PartialEntityQueryWithRestriction<T>(this.returnType, this.entity, restriction);
        }

        public PartialEntityQueryWithStartIndex<T> startingAt(int index) {
            return new PartialEntityQueryWithStartIndex<T>(this.returnType, this.entity, NULL_RESTRICTION, index);
        }

        public EntityQuery<T> returningAtMost(int maxResults) {
            return QueryBuilder.queryFor(this.returnType, this.entity, NULL_RESTRICTION, 0, maxResults);
        }

        public PartialMembershipQueryWithEntityToMatch<T> childrenOf(EntityDescriptor entityToMatch) {
            return new PartialMembershipQueryWithEntityToMatch<T>(this.returnType, this.entity, entityToMatch, true, NULL_RESTRICTION);
        }

        public PartialMembershipQueryWithEntityToMatch<T> parentsOf(EntityDescriptor entityToMatch) {
            return new PartialMembershipQueryWithEntityToMatch<T>(this.returnType, this.entity, entityToMatch, false, NULL_RESTRICTION);
        }

        public Object ofType(GroupType groupType) {
            return null;
        }
    }
}

