/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.atlassian.crowd.embedded.api.Group
 *  com.atlassian.crowd.embedded.api.GroupWithAttributes
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.impl.DelegatingGroupWithAttributes
 *  com.atlassian.crowd.exception.InvalidGroupException
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.search.query.entity.GroupQuery
 *  com.atlassian.crowd.search.query.entity.UserQuery
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.google.common.collect.Lists
 */
package com.atlassian.crowd.embedded.core.util;

import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.impl.DelegatingGroupWithAttributes;
import com.atlassian.crowd.embedded.impl.ImmutableAttributes;
import com.atlassian.crowd.embedded.impl.ImmutableGroup;
import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.search.query.entity.GroupQuery;
import com.atlassian.crowd.search.query.entity.UserQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConversionUtils {
    private ConversionUtils() {
    }

    public static Group toEmbeddedGroup(com.atlassian.crowd.model.group.Group modelGroup) {
        return modelGroup == null ? null : new ImmutableGroup(modelGroup.getName());
    }

    public static com.atlassian.crowd.embedded.api.GroupWithAttributes toEmbeddedGroupWithAttributes(GroupWithAttributes modelGroup) {
        return modelGroup == null ? null : new DelegatingGroupWithAttributes(ConversionUtils.toEmbeddedGroup((com.atlassian.crowd.model.group.Group)modelGroup), (Attributes)new ImmutableAttributes((Attributes)modelGroup));
    }

    public static List<Group> toEmbeddedGroups(List<com.atlassian.crowd.model.group.Group> modelGroups) {
        if (modelGroups == null) {
            return null;
        }
        ArrayList<Group> groups = new ArrayList<Group>(modelGroups.size());
        for (com.atlassian.crowd.model.group.Group modelGroup : modelGroups) {
            groups.add(ConversionUtils.toEmbeddedGroup(modelGroup));
        }
        return groups;
    }

    public static Group getEmbeddedGroup(InvalidGroupException ex) {
        return ConversionUtils.toEmbeddedGroup(ex.getGroup());
    }

    public static UserQuery<com.atlassian.crowd.model.user.User> toModelUserQuery(UserQuery embeddedQuery) {
        return new UserQuery(com.atlassian.crowd.model.user.User.class, embeddedQuery.getSearchRestriction(), embeddedQuery.getStartIndex(), embeddedQuery.getMaxResults());
    }

    public static GroupQuery<com.atlassian.crowd.model.group.Group> toModelGroupQuery(GroupQuery embeddedQuery) {
        return new GroupQuery(com.atlassian.crowd.model.group.Group.class, embeddedQuery.getEntityDescriptor().getGroupType(), embeddedQuery.getSearchRestriction(), embeddedQuery.getStartIndex(), embeddedQuery.getMaxResults());
    }

    public static MembershipQuery<com.atlassian.crowd.model.user.User> toModelUserMembershipQuery(MembershipQuery embeddedQuery) {
        return new MembershipQuery(com.atlassian.crowd.model.user.User.class, embeddedQuery.isFindChildren(), embeddedQuery.getEntityToMatch(), embeddedQuery.getEntityToReturn(), embeddedQuery.getStartIndex(), embeddedQuery.getMaxResults(), embeddedQuery.getSearchRestriction(), (Collection)embeddedQuery.getEntityNamesToMatch());
    }

    public static MembershipQuery<com.atlassian.crowd.model.group.Group> toModelGroupMembershipQuery(MembershipQuery embeddedQuery) {
        return new MembershipQuery(com.atlassian.crowd.model.group.Group.class, embeddedQuery.isFindChildren(), embeddedQuery.getEntityToMatch(), embeddedQuery.getEntityToReturn(), embeddedQuery.getStartIndex(), embeddedQuery.getMaxResults(), embeddedQuery.getSearchRestriction(), (Collection)embeddedQuery.getEntityNamesToMatch());
    }

    public static List<User> toEmbeddedUsers(List<com.atlassian.crowd.model.user.User> modelUsers) {
        return Lists.newArrayList(modelUsers);
    }
}

