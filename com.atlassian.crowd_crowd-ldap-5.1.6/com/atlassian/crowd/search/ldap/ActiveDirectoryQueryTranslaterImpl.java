/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  org.springframework.ldap.filter.Filter
 *  org.springframework.ldap.filter.NotFilter
 */
package com.atlassian.crowd.search.ldap;

import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.ldap.BitwiseFilter;
import com.atlassian.crowd.search.ldap.EverythingResult;
import com.atlassian.crowd.search.ldap.LDAPQueryTranslaterImpl;
import com.atlassian.crowd.search.ldap.NothingResult;
import com.atlassian.crowd.search.ldap.filter.EqualsExternalIdFilter;
import com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.NotFilter;

public class ActiveDirectoryQueryTranslaterImpl
extends LDAPQueryTranslaterImpl {
    private static final int UF_ACCOUNTDISABLE_MASK = 2;

    @Override
    protected Filter booleanTermRestrictionAsFilter(EntityDescriptor entityDescriptor, PropertyRestriction<Boolean> termRestriction, LDAPPropertiesMapper ldapPropertiesMapper) {
        if (entityDescriptor.getEntityType() == Entity.USER && termRestriction.getProperty().equals(UserTermKeys.ACTIVE)) {
            if (((Boolean)termRestriction.getValue()).booleanValue()) {
                return new NotFilter((Filter)BitwiseFilter.or("userAccountControl", 2));
            }
            return BitwiseFilter.and("userAccountControl", 2);
        }
        if (entityDescriptor.getEntityType() == Entity.GROUP && termRestriction.getProperty().equals(GroupTermKeys.ACTIVE)) {
            if (((Boolean)termRestriction.getValue()).booleanValue()) {
                return new EverythingResult();
            }
            return new NothingResult();
        }
        throw new IllegalArgumentException("Boolean restrictions for property " + termRestriction.getProperty().getPropertyName() + " are not supported");
    }

    @Override
    protected Filter getStringTermEqualityFilter(String propertyName, PropertyRestriction<String> termRestriction) {
        if ("objectGUID".equalsIgnoreCase(propertyName)) {
            return new EqualsExternalIdFilter(propertyName, (String)termRestriction.getValue());
        }
        return super.getStringTermEqualityFilter(propertyName, termRestriction);
    }
}

