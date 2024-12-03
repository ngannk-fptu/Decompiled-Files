/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction$BooleanLogic
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  org.springframework.ldap.filter.AndFilter
 *  org.springframework.ldap.filter.EqualsFilter
 *  org.springframework.ldap.filter.Filter
 *  org.springframework.ldap.filter.LikeFilter
 *  org.springframework.ldap.filter.NotPresentFilter
 *  org.springframework.ldap.filter.OrFilter
 *  org.springframework.ldap.filter.PresentFilter
 */
package com.atlassian.crowd.search.ldap;

import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.ldap.EverythingResult;
import com.atlassian.crowd.search.ldap.LDAPQuery;
import com.atlassian.crowd.search.ldap.LDAPQueryTranslater;
import com.atlassian.crowd.search.ldap.NothingResult;
import com.atlassian.crowd.search.ldap.NullResultException;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction;
import com.atlassian.crowd.search.query.entity.restriction.NullRestriction;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import java.util.ArrayList;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.ldap.filter.NotPresentFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.ldap.filter.PresentFilter;

public class LDAPQueryTranslaterImpl
implements LDAPQueryTranslater {
    @Override
    public LDAPQuery asLDAPFilter(EntityQuery query, LDAPPropertiesMapper ldapPropertiesMapper) throws NullResultException {
        LDAPQuery ldapQuery = new LDAPQuery(this.getObjectFilter(query.getEntityDescriptor(), ldapPropertiesMapper));
        Filter ldapFilter = this.searchRestrictionAsFilter(query.getEntityDescriptor(), query.getSearchRestriction(), ldapPropertiesMapper);
        if (!(ldapFilter instanceof EverythingResult)) {
            if (ldapFilter instanceof NothingResult) {
                throw new NullResultException();
            }
            ldapQuery.addFilter(ldapFilter);
        }
        return ldapQuery;
    }

    private Filter searchRestrictionAsFilter(EntityDescriptor entityDescriptor, SearchRestriction restriction, LDAPPropertiesMapper ldapPropertiesMapper) {
        if (restriction instanceof NullRestriction) {
            return new EverythingResult();
        }
        if (restriction instanceof PropertyRestriction) {
            PropertyRestriction propertyRestriction = (PropertyRestriction)restriction;
            if (String.class.equals((Object)propertyRestriction.getProperty().getPropertyType())) {
                return this.stringTermRestrictionAsFilter(entityDescriptor, (PropertyRestriction<String>)propertyRestriction, ldapPropertiesMapper);
            }
            if (Boolean.class.equals((Object)propertyRestriction.getProperty().getPropertyType())) {
                return this.booleanTermRestrictionAsFilter(entityDescriptor, (PropertyRestriction<Boolean>)propertyRestriction, ldapPropertiesMapper);
            }
            throw new IllegalArgumentException("Search restriction on property '" + propertyRestriction.getProperty().getPropertyName() + "' not supported");
        }
        if (restriction instanceof BooleanRestriction) {
            return this.multiTermRestrictionAsFilter(entityDescriptor, (BooleanRestriction)restriction, ldapPropertiesMapper);
        }
        throw new IllegalArgumentException("SearchRestriction not supported: " + restriction.getClass());
    }

    private Filter multiTermRestrictionAsFilter(EntityDescriptor entityDescriptor, BooleanRestriction restriction, LDAPPropertiesMapper ldapPropertiesMapper) {
        AndFilter multiFilter;
        ArrayList<Filter> filters = new ArrayList<Filter>();
        for (SearchRestriction subRestriction : restriction.getRestrictions()) {
            Filter filter = this.searchRestrictionAsFilter(entityDescriptor, subRestriction, ldapPropertiesMapper);
            if (restriction.getBooleanLogic() == BooleanRestriction.BooleanLogic.OR && filter instanceof EverythingResult) {
                return filter;
            }
            if (restriction.getBooleanLogic() == BooleanRestriction.BooleanLogic.AND && filter instanceof NothingResult) {
                return filter;
            }
            filters.add(filter);
        }
        switch (restriction.getBooleanLogic()) {
            case AND: {
                multiFilter = new AndFilter();
                break;
            }
            case OR: {
                multiFilter = new OrFilter();
                break;
            }
            default: {
                throw new IllegalArgumentException("BooleanLogic not supported: " + restriction.getBooleanLogic());
            }
        }
        boolean allNothingResult = true;
        boolean allEverythingResult = true;
        for (Filter filter : filters) {
            if (filter instanceof NothingResult) {
                allEverythingResult = false;
                continue;
            }
            if (filter instanceof EverythingResult) {
                allNothingResult = false;
                continue;
            }
            allEverythingResult = false;
            allNothingResult = false;
            multiFilter.append(filter);
        }
        if (allNothingResult) {
            return new NothingResult();
        }
        if (allEverythingResult) {
            return new EverythingResult();
        }
        return multiFilter;
    }

    protected Filter booleanTermRestrictionAsFilter(EntityDescriptor entityDescriptor, PropertyRestriction<Boolean> termRestriction, LDAPPropertiesMapper ldapPropertiesMapper) {
        if (!termRestriction.getProperty().equals(GroupTermKeys.ACTIVE) && !termRestriction.getProperty().equals(UserTermKeys.ACTIVE)) {
            throw new IllegalArgumentException("Boolean restrictions for property " + termRestriction.getProperty().getPropertyName() + " are not supported");
        }
        if (((Boolean)termRestriction.getValue()).booleanValue()) {
            return new EverythingResult();
        }
        return new NothingResult();
    }

    private Filter stringTermRestrictionAsFilter(EntityDescriptor entityDescriptor, PropertyRestriction<String> termRestriction, LDAPPropertiesMapper ldapPropertiesMapper) {
        String propertyName = this.getLDAPAttributeName(entityDescriptor, termRestriction.getProperty(), ldapPropertiesMapper);
        switch (termRestriction.getMatchMode()) {
            case STARTS_WITH: {
                return new LikeFilter(propertyName, (String)termRestriction.getValue() + "*");
            }
            case ENDS_WITH: {
                return new LikeFilter(propertyName, "*" + (String)termRestriction.getValue());
            }
            case CONTAINS: {
                if (((String)termRestriction.getValue()).length() > 0) {
                    return new LikeFilter(propertyName, "*" + (String)termRestriction.getValue() + "*");
                }
                return new PresentFilter(propertyName);
            }
            case NULL: {
                return new NotPresentFilter(propertyName);
            }
        }
        return this.getStringTermEqualityFilter(propertyName, termRestriction);
    }

    protected Filter getStringTermEqualityFilter(String propertyName, PropertyRestriction<String> termRestriction) {
        return new EqualsFilter(propertyName, (String)termRestriction.getValue());
    }

    private String getLDAPAttributeName(EntityDescriptor entityDescriptor, Property<?> property, LDAPPropertiesMapper ldapPropertiesMapper) {
        switch (entityDescriptor.getEntityType()) {
            case USER: {
                return this.getUserLDAPAttributeName(property, ldapPropertiesMapper);
            }
            case GROUP: {
                switch (entityDescriptor.getGroupType()) {
                    case GROUP: {
                        return this.getGroupLDAPAttributeName(property, ldapPropertiesMapper);
                    }
                    case LEGACY_ROLE: {
                        return this.getRoleLDAPAttributeName(property, ldapPropertiesMapper);
                    }
                }
                throw new IllegalArgumentException("Cannot transform group type <" + entityDescriptor.getGroupType() + ">");
            }
        }
        throw new IllegalArgumentException("Cannot transform entity of type <" + entityDescriptor.getEntityType() + ">");
    }

    private String getUserLDAPAttributeName(Property<?> property, LDAPPropertiesMapper ldapPropertiesMapper) {
        if (UserTermKeys.USERNAME.equals(property)) {
            return ldapPropertiesMapper.getUserNameAttribute();
        }
        if (UserTermKeys.FIRST_NAME.equals(property)) {
            return ldapPropertiesMapper.getUserFirstNameAttribute();
        }
        if (UserTermKeys.LAST_NAME.equals(property)) {
            return ldapPropertiesMapper.getUserLastNameAttribute();
        }
        if (UserTermKeys.DISPLAY_NAME.equals(property)) {
            return ldapPropertiesMapper.getUserDisplayNameAttribute();
        }
        if (UserTermKeys.EMAIL.equals(property)) {
            return ldapPropertiesMapper.getUserEmailAttribute();
        }
        if (UserTermKeys.EXTERNAL_ID.equals(property)) {
            return ldapPropertiesMapper.getExternalIdAttribute();
        }
        if (UserTermKeys.ACTIVE.equals(property)) {
            return null;
        }
        return property.getPropertyName();
    }

    private String getGroupLDAPAttributeName(Property<?> property, LDAPPropertiesMapper ldapPropertiesMapper) {
        if (GroupTermKeys.NAME.equals(property)) {
            return ldapPropertiesMapper.getGroupNameAttribute();
        }
        return property.getPropertyName();
    }

    private String getRoleLDAPAttributeName(Property<?> property, LDAPPropertiesMapper ldapPropertiesMapper) {
        if (GroupTermKeys.NAME.equals(property)) {
            return ldapPropertiesMapper.getRoleNameAttribute();
        }
        return property.getPropertyName();
    }

    private String getObjectFilter(EntityDescriptor entityDescriptor, LDAPPropertiesMapper ldapPropertiesMapper) {
        switch (entityDescriptor.getEntityType()) {
            case USER: {
                return ldapPropertiesMapper.getUserFilter();
            }
            case GROUP: {
                if (entityDescriptor.getGroupType() == null) {
                    throw new IllegalArgumentException("Cannot search for groups where the GroupType has not been specified");
                }
                switch (entityDescriptor.getGroupType()) {
                    case GROUP: {
                        return ldapPropertiesMapper.getGroupFilter();
                    }
                    case LEGACY_ROLE: {
                        return ldapPropertiesMapper.getRoleFilter();
                    }
                }
                throw new IllegalArgumentException("Cannot transform group type <" + entityDescriptor.getGroupType() + ">");
            }
        }
        throw new IllegalArgumentException("Cannot transform entity of type <" + entityDescriptor.getEntityType() + ">");
    }
}

