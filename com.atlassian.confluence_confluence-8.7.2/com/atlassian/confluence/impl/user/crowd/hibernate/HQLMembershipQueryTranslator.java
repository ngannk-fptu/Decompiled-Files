/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.membership.MembershipType
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.hibernate.HQLQuery
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 */
package com.atlassian.confluence.impl.user.crowd.hibernate;

import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.membership.MembershipType;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.hibernate.HQLQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import java.util.Collection;

public class HQLMembershipQueryTranslator {
    public <T> HQLQuery toHQL(long directoryId, MembershipQuery<T> query) {
        return this.toHQL(directoryId, query, false);
    }

    public <T> HQLQuery toHQL(long directoryId, MembershipQuery<T> query, boolean selectGroupName) {
        if (query.isFindChildren()) {
            return this.makeGroupMembershipQuery(directoryId, query, selectGroupName);
        }
        return this.makeUserMembershipQuery(directoryId, query, selectGroupName);
    }

    private <T> HQLQuery makeGroupMembershipQuery(long directoryId, MembershipQuery<T> query, boolean selectGroupName) {
        if (this.isGroupGroupQuery(query)) {
            return this.makeGroupGroupMembersQuery(directoryId, query, selectGroupName);
        }
        return this.makeUserGroupMembersQuery(directoryId, query);
    }

    private <T> HQLQuery makeUserMembershipQuery(long directoryId, MembershipQuery<T> query, boolean selectGroupName) {
        if (this.isGroupGroupQuery(query)) {
            return this.makeGroupGroupMembershipQuery(directoryId, query, selectGroupName);
        }
        return this.makeUserGroupMembershipQuery(directoryId, query);
    }

    private <T> HQLQuery makeGroupGroupMembersQuery(long directoryId, MembershipQuery<T> query, boolean selectGroupName) {
        HQLQuery hql = this.newQuery();
        if (selectGroupName) {
            this.prependGroupName(hql, true);
        }
        hql.appendOrderBy((CharSequence)"mem.groupMember.lowerName");
        if (query.getReturnType().equals(String.class)) {
            hql.appendSelect((CharSequence)"mem.groupMember.name");
        } else {
            hql.appendSelect((CharSequence)"mem.groupMember");
        }
        String batchParam = hql.addParameterPlaceholderForBatchedParam((Collection)IdentifierUtils.toLowerCase((Collection)query.getEntityNamesToMatch()));
        hql.safeAppendWhere((CharSequence)("mem.userMember is null and mem.parentGroup.lowerName in " + batchParam));
        this.appendDirectoryClause(directoryId, hql);
        this.appendGroupType(query, hql, MembershipType.GROUP_GROUP);
        return hql;
    }

    private <T> HQLQuery makeUserGroupMembersQuery(long directoryId, MembershipQuery<T> query) {
        HQLQuery hql = this.newQuery();
        hql.appendOrderBy((CharSequence)"mem.userMember.lowerName");
        if (query.getReturnType().equals(String.class)) {
            hql.appendSelect((CharSequence)"mem.userMember.name");
        } else {
            hql.appendSelect((CharSequence)"mem.userMember");
        }
        String batchParam = hql.addParameterPlaceholderForBatchedParam((Collection)IdentifierUtils.toLowerCase((Collection)query.getEntityNamesToMatch()));
        hql.safeAppendWhere((CharSequence)("mem.groupMember is null and mem.parentGroup.lowerName in " + batchParam));
        this.appendDirectoryClause(directoryId, hql);
        this.appendGroupType(query, hql, MembershipType.GROUP_USER);
        return hql;
    }

    private <T> HQLQuery makeGroupGroupMembershipQuery(long directoryId, MembershipQuery<T> query, boolean selectGroupName) {
        HQLQuery hql = this.newQuery();
        if (selectGroupName) {
            this.prependGroupName(hql, false);
        }
        hql.appendOrderBy((CharSequence)"mem.parentGroup.lowerName");
        if (query.getReturnType().equals(String.class)) {
            hql.appendSelect((CharSequence)"mem.parentGroup.name");
        } else {
            hql.appendSelect((CharSequence)"mem.parentGroup");
        }
        String batchParam = hql.addParameterPlaceholderForBatchedParam((Collection)IdentifierUtils.toLowerCase((Collection)query.getEntityNamesToMatch()));
        hql.safeAppendWhere((CharSequence)("mem.groupMember.lowerName in " + batchParam));
        this.appendDirectoryClause(directoryId, hql);
        this.appendGroupType(query, hql, MembershipType.GROUP_GROUP);
        return hql;
    }

    private <T> HQLQuery makeUserGroupMembershipQuery(long directoryId, MembershipQuery<T> query) {
        HQLQuery hql = this.newQuery();
        hql.appendOrderBy((CharSequence)"mem.parentGroup.lowerName");
        if (query.getReturnType().equals(String.class)) {
            hql.appendSelect((CharSequence)"mem.parentGroup.name");
        } else {
            hql.appendSelect((CharSequence)"mem.parentGroup");
        }
        String batchParam = hql.addParameterPlaceholderForBatchedParam((Collection)IdentifierUtils.toLowerCase((Collection)query.getEntityNamesToMatch()));
        hql.safeAppendWhere((CharSequence)("mem.userMember.lowerName in " + batchParam));
        this.appendDirectoryClause(directoryId, hql);
        this.appendGroupType(query, hql, MembershipType.GROUP_USER);
        return hql;
    }

    private <T> boolean isGroupGroupQuery(MembershipQuery<T> query) {
        return query.getEntityToMatch().getEntityType() == Entity.GROUP && query.getEntityToReturn().getEntityType() == Entity.GROUP;
    }

    private void prependGroupName(HQLQuery hql, boolean findChildren) {
        String fullName = "mem" + (findChildren ? ".parentGroup.name" : ".groupMember.name");
        hql.appendSelect((CharSequence)fullName).append(", ");
        hql.appendOrderBy((CharSequence)fullName).append(", ");
    }

    private <T> void appendGroupType(MembershipQuery<T> query, HQLQuery hql, MembershipType membershipType) {
        GroupType groupType = query.getEntityToMatch().getGroupType();
        if (groupType != null) {
            if (membershipType.equals((Object)MembershipType.GROUP_GROUP) && !groupType.equals((Object)query.getEntityToReturn().getGroupType())) {
                throw new IllegalArgumentException("Cannot search memberships of conflicting group types");
            }
            hql.safeAppendWhere((CharSequence)(" and mem.parentGroup.type = " + hql.addParameterPlaceholder((Object)query.getEntityToMatch().getGroupType())));
        }
    }

    private void appendDirectoryClause(long directoryId, HQLQuery hql) {
        hql.safeAppendWhere((CharSequence)(" and mem.parentGroup.directory.id = " + hql.addParameterPlaceholder((Object)directoryId)));
    }

    private HQLQuery newQuery() {
        HQLQuery hql = new HQLQuery();
        hql.appendFrom((CharSequence)"HibernateMembership mem");
        return hql;
    }
}

