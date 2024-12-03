/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.impl.hibernate.query.InExpressionBuilder
 *  com.google.common.base.Supplier
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.dialect.Dialect
 *  org.hibernate.query.Query
 */
package com.atlassian.confluence.impl.security.query;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.hibernate.query.InExpressionBuilder;
import com.atlassian.confluence.impl.security.access.SpacePermissionSubjectType;
import com.atlassian.confluence.impl.security.query.SpacePermissionQueryBuilder;
import com.atlassian.confluence.user.ConfluenceUser;
import com.google.common.base.Supplier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.dialect.Dialect;
import org.hibernate.query.Query;

@Internal
public class SpacePermissionQueryBuilderImpl
implements SpacePermissionQueryBuilder {
    private final ConfluenceUser user;
    private final Set<SpacePermissionSubjectType> permissionSubjectTypes;
    private final Supplier<List<String>> userGroupNames;
    private final String permissionType;
    private final Dialect hibernateDialect;
    private InExpressionBuilder inExpressionBuilder;

    SpacePermissionQueryBuilderImpl(ConfluenceUser user, Set<SpacePermissionSubjectType> spacePermissionSubjectTypes, Supplier<List<String>> userGroupNames, String permissionType, Dialect hibernateDialect) {
        this.user = user;
        this.permissionSubjectTypes = spacePermissionSubjectTypes;
        this.userGroupNames = userGroupNames;
        this.permissionType = permissionType;
        this.inExpressionBuilder = null;
        this.hibernateDialect = hibernateDialect;
    }

    private boolean includeGroupMembershipPermissionCheck() {
        return this.permissionSubjectTypes.contains((Object)SpacePermissionSubjectType.GROUP) && !((List)this.userGroupNames.get()).isEmpty();
    }

    private boolean includeUserPermissionCheck() {
        return this.permissionSubjectTypes.contains((Object)SpacePermissionSubjectType.USER) && this.user != null;
    }

    @Override
    public String getHqlPermissionFilterString(String spacePermissionTableAlias) {
        ArrayList<Object> clauses = new ArrayList<Object>();
        if (this.includeUserPermissionCheck()) {
            clauses.add(spacePermissionTableAlias + ".userSubject.id = :userKey");
        }
        if (this.includeGroupMembershipPermissionCheck()) {
            this.inExpressionBuilder = InExpressionBuilder.getInExpressionBuilderDefaultLimit((String)(spacePermissionTableAlias + ".group"), (String)"groups", (List)((List)this.userGroupNames.get()), (Dialect)this.hibernateDialect);
            clauses.add(this.inExpressionBuilder.buildInExpressionString());
        }
        if (this.permissionSubjectTypes.contains((Object)SpacePermissionSubjectType.ALL_AUTHENTICATED_USERS)) {
            clauses.add(spacePermissionTableAlias + ".allUsersSubject = 'authenticated-users'");
        }
        if (this.permissionSubjectTypes.contains((Object)SpacePermissionSubjectType.ANONYMOUS)) {
            clauses.add("(" + spacePermissionTableAlias + ".userSubject is null and " + spacePermissionTableAlias + ".group is null and " + spacePermissionTableAlias + ".allUsersSubject is null)");
        }
        if (clauses.isEmpty()) {
            throw new IllegalStateException("Expected to find at least 1 permission category clause");
        }
        String permissionCategoryConditions = "(" + StringUtils.join(clauses, (String)" or ") + ")";
        String permissionTypeCondition = spacePermissionTableAlias + ".type = :permission";
        return "(" + permissionCategoryConditions + " and " + permissionTypeCondition + ")";
    }

    @Override
    public void substituteHqlQueryParameters(Query query) {
        if (this.includeUserPermissionCheck()) {
            query.setParameter("userKey", (Object)this.user.getKey().getStringValue());
        }
        if (this.includeGroupMembershipPermissionCheck() && this.inExpressionBuilder != null) {
            this.inExpressionBuilder.substituteInExpressionParameters(query);
        }
        query.setParameter("permission", (Object)this.permissionType);
    }

    @Override
    public @Nullable ConfluenceUser getUser() {
        return this.user;
    }

    @Override
    public String getPermissionType() {
        return this.permissionType;
    }
}

