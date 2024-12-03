/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.MatchMode
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.query;

import com.atlassian.crowd.directory.query.FetchMode;
import com.atlassian.crowd.directory.query.GraphQuery;
import com.atlassian.crowd.directory.query.ODataExpand;
import com.atlassian.crowd.directory.query.ODataFilter;
import com.atlassian.crowd.directory.query.ODataSelect;
import com.atlassian.crowd.directory.query.ODataTop;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction;
import com.atlassian.crowd.search.query.entity.restriction.MatchMode;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MicrosoftGraphQueryTranslator {
    public static final String USERNAME = "userPrincipalName";
    public static final String FIRST_NAME = "givenName";
    public static final String LAST_NAME = "surname";
    public static final String DISPLAY_NAME = "displayName";
    public static final String MAIL = "mail";
    public static final String ID = "id";
    public static final String ACCOUNT_ENABLED = "accountEnabled";
    static final ODataSelect ID_SELECT = new ODataSelect("id");
    static final ODataSelect USERNAME_SELECT = new ODataSelect("userPrincipalName");
    static final ODataSelect MINIMAL_USER_SELECT = new ODataSelect("userPrincipalName", "id");
    public static final ODataSelect USER_SELECT = new ODataSelect("userPrincipalName", "givenName", "surname", "displayName", "mail", "id", "accountEnabled");
    static final String GROUPNAME = "displayName";
    static final String DESCRIPTION = "description";
    static final String MEMBERS = "members";
    public static final ODataExpand MEMBERS_EXPAND = new ODataExpand("members");
    public static final ODataSelect GROUPNAME_SELECT = new ODataSelect("displayName");
    static final ODataSelect MINIMAL_GROUP_SELECT = new ODataSelect("displayName", "id");
    public static final ODataSelect GROUP_SELECT = new ODataSelect("displayName", "description", "id");
    public static final ODataSelect DELTA_QUERY_GROUP_SELECT = GROUP_SELECT.addColumns("members");
    static final ODataSelect NAMES_SELECT = new ODataSelect("userPrincipalName", "displayName");
    static final ODataSelect USER_AND_GROUP_SELECT = USER_SELECT.merge(GROUP_SELECT);
    static final ODataSelect MINIMAL_USER_AND_GROUP_SELECT = MINIMAL_USER_SELECT.merge(MINIMAL_GROUP_SELECT);
    private static final String GRAPH_EQUALS_OPERATOR = "eq";
    private static final String GRAPH_LESS_THAN_OPERATOR = "lt";
    private static final String GRAPH_LESS_THAN_OR_EQUAL_OPERATOR = "lte";
    private static final String GRAPH_GREATER_THAN_OPERATOR = "gt";
    private static final String GRAPH_GREATER_THAN_OR_EQUAL_OPERATOR = "gte";
    private static final String GRAPH_STARTS_WITH_FUNCTION = "startswith";
    private static final Logger log = LoggerFactory.getLogger(MicrosoftGraphQueryTranslator.class);

    public GraphQuery convert(EntityQuery query, String usernameAttribute) {
        SearchRestriction searchRestriction = query.getSearchRestriction();
        ODataFilter oDataFilter = this.translateSearchRestriction(query.getEntityDescriptor(), searchRestriction, usernameAttribute);
        ODataSelect oDataSelect = this.resolveAzureAdColumnsForSingleEntityTypeQuery(query.getEntityDescriptor(), query.getReturnType());
        ODataTop limit = this.resolveQueryLimit(query.getMaxResults());
        return new GraphQuery(oDataFilter, oDataSelect, query.getStartIndex(), limit);
    }

    private ODataTop resolveQueryLimit(int maxResults) {
        return maxResults == -1 ? ODataTop.FULL_PAGE : ODataTop.forSize(maxResults);
    }

    public ODataSelect resolveAzureAdColumnsForSingleEntityTypeQuery(EntityDescriptor entity, Class<?> returnType) {
        return this.resolveAzureAdColumnsForSingleEntityTypeQuery(entity, returnType == String.class ? FetchMode.NAME : FetchMode.FULL);
    }

    public ODataFilter translateSearchRestriction(EntityDescriptor entityDescriptor, SearchRestriction restriction, String usernameAttribute) {
        if (restriction instanceof PropertyRestriction) {
            return new ODataFilter(this.translatePropertyRestriction(entityDescriptor, (PropertyRestriction)restriction, usernameAttribute));
        }
        if (restriction instanceof BooleanRestriction) {
            return new ODataFilter(this.translateBooleanRestriction(entityDescriptor, (BooleanRestriction)restriction, usernameAttribute));
        }
        return ODataFilter.EMPTY;
    }

    public ODataSelect resolveAzureAdColumnsForSingleEntityTypeQuery(EntityDescriptor entity, FetchMode fetchMode) {
        if (entity == EntityDescriptor.user()) {
            switch (fetchMode) {
                case FULL: 
                case DELTA_QUERY: {
                    return USER_SELECT;
                }
                case NAME_AND_ID: {
                    return MINIMAL_USER_SELECT;
                }
                case NAME: {
                    return USERNAME_SELECT;
                }
                case ID: {
                    return ID_SELECT;
                }
            }
            throw new IllegalArgumentException(String.format("Cannot translate query for entity %s, fetch mode %s", new Object[]{entity, fetchMode}));
        }
        if (entity == EntityDescriptor.group()) {
            switch (fetchMode) {
                case FULL: {
                    return GROUP_SELECT;
                }
                case DELTA_QUERY: {
                    return DELTA_QUERY_GROUP_SELECT;
                }
                case NAME_AND_ID: {
                    return MINIMAL_GROUP_SELECT;
                }
                case NAME: {
                    return GROUPNAME_SELECT;
                }
                case ID: {
                    return ID_SELECT;
                }
            }
            throw new IllegalArgumentException(String.format("Cannot translate query for entity %s, fetch mode %s", new Object[]{entity, fetchMode}));
        }
        throw new IllegalArgumentException(String.format("Cannot translate query for entity %s, fetch mode %s", new Object[]{entity, fetchMode}));
    }

    public ODataExpand resolveAzureAdNavigationPropertiesForSingleEntityTypeQuery(EntityDescriptor entity, FetchMode fetchMode) {
        if (entity == EntityDescriptor.group()) {
            switch (fetchMode) {
                case DELTA_QUERY: {
                    return MEMBERS_EXPAND;
                }
            }
            throw new IllegalArgumentException(String.format("Cannot translate query for entity %s, fetch mode %s", new Object[]{entity, fetchMode}));
        }
        throw new IllegalArgumentException(String.format("Cannot translate query for entity %s, fetch mode %s", new Object[]{entity, fetchMode}));
    }

    public ODataSelect translateColumnsForUsersAndGroupsQuery(FetchMode fetchMode) {
        switch (fetchMode) {
            case FULL: {
                return USER_AND_GROUP_SELECT;
            }
            case NAME_AND_ID: {
                return MINIMAL_USER_AND_GROUP_SELECT;
            }
            case NAME: {
                return NAMES_SELECT;
            }
            case ID: {
                return ID_SELECT;
            }
        }
        throw new IllegalArgumentException(String.format("Cannot translate query for users and groups query, fetch mode %s", new Object[]{fetchMode}));
    }

    private String translateBooleanRestriction(EntityDescriptor entityDescriptor, BooleanRestriction restriction, String usernameAttribute) {
        String result = restriction.getRestrictions().stream().map(subrestriction -> this.translateSearchRestriction(entityDescriptor, (SearchRestriction)subrestriction, usernameAttribute).asRawValue()).collect(Collectors.joining(" " + restriction.getBooleanLogic().name() + " "));
        return restriction.getRestrictions().size() > 1 ? "(" + result + ")" : result;
    }

    private String translatePropertyRestriction(EntityDescriptor entityDescriptor, PropertyRestriction restriction, String usernameAttribute) {
        StringBuilder stringBuilder = new StringBuilder();
        String azureAdPropertyName = this.resolvePropertyName(entityDescriptor, restriction.getProperty(), usernameAttribute);
        if (restriction.getMatchMode() == MatchMode.CONTAINS || restriction.getMatchMode() == MatchMode.STARTS_WITH || restriction.getMatchMode() == MatchMode.ENDS_WITH) {
            this.appendODataFunction(restriction, stringBuilder, azureAdPropertyName);
        } else {
            this.appendODataComparison(restriction, stringBuilder, azureAdPropertyName);
        }
        return stringBuilder.toString();
    }

    private void appendODataComparison(PropertyRestriction restriction, StringBuilder stringBuilder, String azureAdPropertyName) {
        stringBuilder.append(azureAdPropertyName).append(" ").append(this.resolveOperator(restriction.getMatchMode())).append(" ");
        if (this.shouldBeQuoted(restriction)) {
            stringBuilder.append("'");
        }
        if (restriction.getMatchMode() == MatchMode.NULL) {
            stringBuilder.append("null");
        } else {
            stringBuilder.append(this.sanitizeOdataValue(restriction.getValue()));
        }
        if (this.shouldBeQuoted(restriction)) {
            stringBuilder.append("'");
        }
    }

    private boolean shouldBeQuoted(PropertyRestriction restriction) {
        return restriction.getProperty().getPropertyType() == String.class && restriction.getMatchMode() != MatchMode.NULL;
    }

    private void appendODataFunction(PropertyRestriction restriction, StringBuilder stringBuilder, String azureAdPropertyName) {
        boolean isStringRestriction;
        stringBuilder.append(this.resolveOperatorFunction(restriction.getMatchMode())).append("(").append(this.sanitizeOdataValue(azureAdPropertyName)).append(",");
        boolean bl = isStringRestriction = restriction.getProperty().getPropertyType() == String.class;
        if (isStringRestriction) {
            stringBuilder.append("'");
        }
        stringBuilder.append(this.sanitizeOdataValue(restriction.getValue()));
        if (isStringRestriction) {
            stringBuilder.append("'");
        }
        stringBuilder.append(")");
    }

    private String resolveOperatorFunction(MatchMode matchMode) {
        switch (matchMode) {
            case CONTAINS: {
                log.warn("Contains query is not supported for Azure AD directories, using 'starts with' instead");
                return GRAPH_STARTS_WITH_FUNCTION;
            }
            case STARTS_WITH: {
                return GRAPH_STARTS_WITH_FUNCTION;
            }
            case ENDS_WITH: {
                log.warn("'Ends with' query is not supported for Azure AD directories, using 'starts with' instead");
                return GRAPH_STARTS_WITH_FUNCTION;
            }
        }
        throw new IllegalArgumentException("Cannot query by match mode " + matchMode);
    }

    private String resolveOperator(MatchMode matchMode) {
        switch (matchMode) {
            case EXACTLY_MATCHES: 
            case NULL: {
                return GRAPH_EQUALS_OPERATOR;
            }
            case GREATER_THAN: {
                return GRAPH_GREATER_THAN_OPERATOR;
            }
            case GREATER_THAN_OR_EQUAL: {
                return GRAPH_GREATER_THAN_OR_EQUAL_OPERATOR;
            }
            case LESS_THAN: {
                return GRAPH_LESS_THAN_OPERATOR;
            }
            case LESS_THAN_OR_EQUAL: {
                return GRAPH_LESS_THAN_OR_EQUAL_OPERATOR;
            }
        }
        throw new IllegalArgumentException("Cannot query by match mode " + matchMode);
    }

    private String resolvePropertyName(EntityDescriptor entityDescriptor, Property property, String usernameAttribute) {
        switch (entityDescriptor.getEntityType()) {
            case USER: {
                return this.getUserAttributeName(property, usernameAttribute);
            }
            case GROUP: {
                return this.getGroupAttributeName(property);
            }
        }
        throw new IllegalArgumentException("Cannot transform entity of type <" + entityDescriptor.getEntityType() + ">");
    }

    private String getGroupAttributeName(Property property) {
        if (GroupTermKeys.NAME.equals(property)) {
            return "displayName";
        }
        if (GroupTermKeys.DESCRIPTION.equals(property)) {
            return DESCRIPTION;
        }
        throw new IllegalArgumentException("Cannot query by property " + property);
    }

    private String getUserAttributeName(Property property, String usernameAttribute) {
        if (UserTermKeys.USERNAME.equals(property)) {
            return usernameAttribute;
        }
        if (UserTermKeys.FIRST_NAME.equals(property)) {
            return FIRST_NAME;
        }
        if (UserTermKeys.LAST_NAME.equals(property)) {
            return LAST_NAME;
        }
        if (UserTermKeys.DISPLAY_NAME.equals(property)) {
            return "displayName";
        }
        if (UserTermKeys.EMAIL.equals(property)) {
            return MAIL;
        }
        if (UserTermKeys.EXTERNAL_ID.equals(property)) {
            return ID;
        }
        if (UserTermKeys.ACTIVE.equals(property)) {
            return ACCOUNT_ENABLED;
        }
        throw new IllegalArgumentException("Cannot query by property " + property);
    }

    private <T> T sanitizeOdataValue(T value) {
        if (value instanceof String) {
            return (T)((String)value).replace("'", "''");
        }
        return value;
    }
}

