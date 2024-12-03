/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.crowd.directory.ldap;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction;
import com.atlassian.crowd.search.query.entity.restriction.NullRestriction;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class QueryAnalyser {
    public static final Set<Property> USER_LDAP_PROPERTIES = ImmutableSet.of((Object)UserTermKeys.USERNAME, (Object)UserTermKeys.DISPLAY_NAME, (Object)UserTermKeys.EMAIL, (Object)UserTermKeys.FIRST_NAME, (Object)UserTermKeys.LAST_NAME, (Object)UserTermKeys.ACTIVE, (Object[])new Property[0]);
    public static final Set<Property> GROUP_LDAP_PROPERTIES = ImmutableSet.of((Object)GroupTermKeys.NAME, (Object)GroupTermKeys.ACTIVE);

    public static <T> boolean isQueryOnLdapFieldsOnly(EntityQuery<T> query) {
        return QueryAnalyser.doesRestrictionFollowPath(query.getEntityDescriptor().getEntityType(), query.getSearchRestriction(), QueryPath.LDAP);
    }

    public static <T> boolean isQueryOnInternalFieldsOnly(EntityQuery<T> query) {
        return QueryAnalyser.doesRestrictionFollowPath(query.getEntityDescriptor().getEntityType(), query.getSearchRestriction(), QueryPath.INTERNAL);
    }

    private static boolean doesRestrictionFollowPath(Entity entity, SearchRestriction restriction, QueryPath queryPath) {
        if (restriction instanceof NullRestriction) {
            return true;
        }
        if (restriction instanceof PropertyRestriction) {
            boolean ldapQuery;
            PropertyRestriction propertyRestriction = (PropertyRestriction)restriction;
            switch (entity) {
                case USER: {
                    ldapQuery = USER_LDAP_PROPERTIES.contains(propertyRestriction.getProperty());
                    break;
                }
                case GROUP: {
                    ldapQuery = GROUP_LDAP_PROPERTIES.contains(propertyRestriction.getProperty());
                    break;
                }
                default: {
                    return false;
                }
            }
            return ldapQuery && queryPath == QueryPath.LDAP || !ldapQuery && queryPath == QueryPath.INTERNAL;
        }
        if (restriction instanceof BooleanRestriction) {
            BooleanRestriction booleanRestriction = (BooleanRestriction)restriction;
            boolean followsPath = true;
            for (SearchRestriction subRestriction : booleanRestriction.getRestrictions()) {
                followsPath = QueryAnalyser.doesRestrictionFollowPath(entity, subRestriction, queryPath);
                if (followsPath) continue;
                return false;
            }
            return followsPath;
        }
        return false;
    }

    private static enum QueryPath {
        LDAP,
        INTERNAL;

    }
}

