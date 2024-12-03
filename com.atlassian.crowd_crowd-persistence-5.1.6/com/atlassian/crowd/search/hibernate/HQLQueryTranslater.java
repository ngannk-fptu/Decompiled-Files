/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.model.NameComparator
 *  com.atlassian.crowd.model.application.ApplicationImpl
 *  com.atlassian.crowd.model.directory.DirectoryImpl
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.membership.MembershipType
 *  com.atlassian.crowd.model.token.Token
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.Combine
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction$BooleanLogic
 *  com.atlassian.crowd.search.query.entity.restriction.MatchMode
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.constants.AliasTermKeys
 *  com.atlassian.crowd.search.query.entity.restriction.constants.DirectoryTermKeys
 *  com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys
 *  com.atlassian.crowd.search.query.entity.restriction.constants.TokenTermKeys
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.crowd.search.hibernate;

import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.model.NameComparator;
import com.atlassian.crowd.model.alias.Alias;
import com.atlassian.crowd.model.application.ApplicationImpl;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.InternalGroup;
import com.atlassian.crowd.model.membership.InternalMembership;
import com.atlassian.crowd.model.membership.MembershipType;
import com.atlassian.crowd.model.token.Token;
import com.atlassian.crowd.model.user.InternalUser;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.Combine;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.hibernate.CustomDataFetcher;
import com.atlassian.crowd.search.hibernate.CustomDataFetchers;
import com.atlassian.crowd.search.hibernate.HQLQuery;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction;
import com.atlassian.crowd.search.query.entity.restriction.MatchMode;
import com.atlassian.crowd.search.query.entity.restriction.NullRestriction;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.AliasTermKeys;
import com.atlassian.crowd.search.query.entity.restriction.constants.DirectoryTermKeys;
import com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys;
import com.atlassian.crowd.search.query.entity.restriction.constants.TokenTermKeys;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class HQLQueryTranslater {
    protected static final String HQL_USER_NAME = "lowerName";
    protected static final String HQL_USER_EMAIL_ADDRESS = "lowerEmailAddress";
    protected static final String HQL_USER_FIRST_NAME = "lowerFirstName";
    protected static final String HQL_USER_LAST_NAME = "lowerLastName";
    protected static final String HQL_USER_DISPLAY_NAME = "lowerDisplayName";
    protected static final String HQL_USER_ACTIVE = "active";
    protected static final String HQL_USER_EXTERNAL_ID = "externalId";
    protected static final String HQL_CREATED_DATE = "createdDate";
    protected static final String HQL_UPDATED_DATE = "updatedDate";
    protected static final String HQL_GROUP_NAME = "lowerName";
    protected static final String HQL_GROUP_DESCRIPTION = "description";
    protected static final String HQL_GROUP_ACTIVE = "active";
    protected static final String HQL_GROUP_TYPE = "type";
    protected static final String HQL_GROUP_LOCAL = "local";
    protected static final String HQL_GROUP_EXTERNAL_ID = "externalId";
    protected static final String HQL_TOKEN_NAME = "name";
    protected static final String HQL_TOKEN_LAST_ACCESSED_TIME = "lastAccessedTime";
    protected static final String HQL_TOKEN_DIRECTORY_ID = "directoryId";
    protected static final String HQL_TOKEN_RANDOM_NUMBER = "randomNumber";
    protected static final String HQL_DIRECTORY_NAME = "lowerName";
    protected static final String HQL_DIRECTORY_ACTIVE = "active";
    protected static final String HQL_DIRECTORY_TYPE = "type";
    protected static final String HQL_DIRECTORY_IMPLEMENTATION_CLASS = "lowerImplementationClass";
    protected static final String HQL_APPLICATION_NAME = "lowerName";
    protected static final String HQL_APPLICATION_ACTIVE = "active";
    protected static final String HQL_APPLICATION_TYPE = "type";
    protected static final String HQL_ALIAS_NAME = "lowerAlias";
    protected static final String HQL_ALIAS_APPLICATION_ID = "application.id";
    protected static final String HQL_ALIAS_USERNAME = "lowerName";
    protected static final String HQL_ATTRIBUTE_NAME = "name";
    protected static final String HQL_ATTRIBUTE_LOWER_VALUE = "lowerValue";
    protected static final String HQL_ATTRIBUTE_NUMERIC_VALUE = "numericValue";
    protected static final String HQL_ATTRIBUTE_ALIAS = "attr";
    protected static final String HQL_DIRECTORY_ID = ".directory.id";
    protected static final String HQL_MEMBERSHIP_ALIAS = "mem";
    protected static final String HQL_MEMBERSHIP_TYPE = "membershipType";
    protected static final String HQL_MEMBERSHIP_GROUP_TYPE = "groupType";
    protected static final int DEFAULT_OR_BATCH_SIZE = 1000;
    public static final String HQL_AND = " AND ";
    private int orBatchSize;

    public HQLQueryTranslater() {
        this.orBatchSize = 1000;
    }

    @VisibleForTesting
    public HQLQueryTranslater(int orBatchSize) {
        this.orBatchSize = orBatchSize;
    }

    public HQLQuery asHQL(long directoryID, MembershipQuery query) {
        return this.asHQL(directoryID, query, false);
    }

    public HQLQuery asHQL(long directoryID, MembershipQuery<?> query, boolean selectEntityToMatch) {
        int start;
        HQLQuery hql = this.newQuery();
        hql.offsetResults(query.getStartIndex());
        hql.limitResults(query.getMaxResults());
        if (selectEntityToMatch) {
            hql.appendSelect(this.matchAttribute(query, false)).append(", ");
        } else {
            if (query.getEntityNamesToMatch().size() > 1) {
                hql.requireDistinct();
            }
            hql.setComparatorForBatch(NameComparator.of((Class)query.getReturnType()));
        }
        this.appendWhere(query, hql, directoryID);
        int n = start = selectEntityToMatch ? 1 : 0;
        if (query.getReturnType() == String.class) {
            this.appendAttributes(hql, (List<String>)ImmutableList.of((Object)this.selectAttribute(query, false)), this.selectAttribute(query, true), start, values -> values[start]);
        } else {
            String alias = HQLQueryTranslater.transformEntityToAlias(query.getEntityToReturn().getEntityType());
            String orderBy = alias + "." + this.resolveDefaultOrderByFieldForEntity(query.getEntityToReturn().getEntityType());
            CustomDataFetcher fetcher = CustomDataFetchers.entityProducer(query.getReturnType());
            this.appendAttributes(hql, fetcher.attributes(alias), orderBy, start, fetcher.getTransformer(start));
        }
        return hql;
    }

    private void appendWhere(MembershipQuery<?> query, HQLQuery hql, long directoryID) {
        if (query.getReturnType() != String.class || this.hasRestriction((Query<?>)query)) {
            String alias = HQLQueryTranslater.transformEntityToAlias(query.getEntityToReturn().getEntityType());
            String persistedClass = this.transformEntityToPersistedClass(query.getEntityToReturn().getEntityType());
            hql.appendFrom(persistedClass).append(" ").append(alias).append(", ");
            hql.safeAppendWhere(alias).safeAppendWhere(".id = ").safeAppendWhere(HQL_MEMBERSHIP_ALIAS);
            hql.safeAppendWhere(query.isFindChildren() ? ".childId" : ".parentId").safeAppendWhere(HQL_AND);
        }
        hql.appendFrom(InternalMembership.class.getSimpleName()).append(" ").append(HQL_MEMBERSHIP_ALIAS);
        String placeholder = hql.addParameterPlaceholderForBatchedParam(IdentifierUtils.toLowerCase((Collection)query.getEntityNamesToMatch()));
        hql.safeAppendWhere(this.matchAttribute(query, true) + " in (" + placeholder + ")");
        this.appendPropertyRestrictionIfNeeded(hql, query.getEntityToReturn().getEntityType(), (Query<?>)query);
        this.appendMembershipTypeAndDirectoryIDAndGroupType(directoryID, query, hql);
    }

    protected HQLQuery newQuery() {
        return new HQLQuery();
    }

    private void appendMembershipTypeAndDirectoryIDAndGroupType(long directoryID, MembershipQuery query, HQLQuery hql) {
        MembershipType membershipType = query.getEntityToMatch().getEntityType() == Entity.GROUP && query.getEntityToReturn().getEntityType() == Entity.GROUP ? MembershipType.GROUP_GROUP : MembershipType.GROUP_USER;
        hql.appendWhere(HQL_AND).append(HQL_MEMBERSHIP_ALIAS).append(".").append(HQL_MEMBERSHIP_TYPE).append(" = ").append(hql.addParameterPlaceholder(membershipType));
        hql.appendWhere(HQL_AND).append(HQL_MEMBERSHIP_ALIAS).append(".directory.id = ").append(hql.addParameterPlaceholder(directoryID));
        GroupType groupType = null;
        if (query.getEntityToMatch().getEntityType() == Entity.GROUP) {
            groupType = query.getEntityToMatch().getGroupType();
        }
        if (query.getEntityToReturn().getEntityType() == Entity.GROUP) {
            if (groupType != null && groupType != query.getEntityToReturn().getGroupType()) {
                throw new IllegalArgumentException("Cannot search memberships of conflicting group types");
            }
            groupType = query.getEntityToReturn().getGroupType();
        }
        if (groupType != null) {
            hql.appendWhere(HQL_AND).append(HQL_MEMBERSHIP_ALIAS).append(".").append(HQL_MEMBERSHIP_GROUP_TYPE).append(" = ").append(hql.addParameterPlaceholder(groupType));
        }
    }

    private void appendAttributes(HQLQuery hql, List<String> select, String orderBy, int start, Function<Object[], ?> transformer) {
        ArrayList<String> allSelect = new ArrayList<String>(select);
        if (!allSelect.contains(orderBy) && !allSelect.contains(StringUtils.substringBefore((String)orderBy, (String)"."))) {
            allSelect.add(orderBy);
        }
        hql.appendSelect(String.join((CharSequence)", ", allSelect));
        hql.appendOrderBy(orderBy);
        hql.setResultTransform(HQLQueryTranslater.listTransformer(start == 0 ? transformer : values -> HQLQueryTranslater.transform(values, start, start + allSelect.size(), transformer)));
    }

    private static Function<List<Object[]>, List<?>> listTransformer(Function<Object[], ?> transformer) {
        return results -> results.stream().map(transformer).collect(Collectors.toList());
    }

    private static Object[] transform(Object[] arr, int start, int end, Function<Object[], ?> transformer) {
        Object[] transformed = new Object[arr.length - end + start + 1];
        System.arraycopy(arr, 0, transformed, 0, start);
        transformed[start] = transformer.apply(arr);
        System.arraycopy(arr, end, transformed, start + 1, arr.length - end);
        return transformed;
    }

    private String matchAttribute(MembershipQuery query, boolean lower) {
        return this.membershipAttribute(query.isFindChildren(), lower);
    }

    private String selectAttribute(MembershipQuery query, boolean lower) {
        return this.membershipAttribute(!query.isFindChildren(), lower);
    }

    private String membershipAttribute(boolean parent, boolean lower) {
        String attribute = parent ? "parentName" : "childName";
        return "mem." + (lower ? "lower" + StringUtils.capitalize((String)attribute) : attribute);
    }

    public HQLQuery asHQL(EntityQuery entityQuery) {
        HQLQuery hql = this.newQuery();
        this.appendQueryAsHQL(entityQuery, hql);
        return hql;
    }

    public List<HQLQuery> asHQL(long directoryID, EntityQuery entityQuery) {
        List<EntityQuery> queries = this.splitEntityQueryIntoBatches(entityQuery);
        ArrayList<HQLQuery> translatedQueries = new ArrayList<HQLQuery>(queries.size());
        for (EntityQuery query : queries) {
            HQLQuery hql = this.newQuery();
            String entityAlias = HQLQueryTranslater.transformEntityToAlias(query.getEntityDescriptor().getEntityType());
            hql.appendWhere(entityAlias).append(HQL_DIRECTORY_ID).append(" = ").append(hql.addParameterPlaceholder(directoryID));
            this.appendQueryAsHQL(query, hql);
            translatedQueries.add(hql);
        }
        return translatedQueries;
    }

    private List<EntityQuery> splitEntityQueryIntoBatches(EntityQuery entityQuery) {
        if (entityQuery.getSearchRestriction() instanceof BooleanRestriction && ((BooleanRestriction)entityQuery.getSearchRestriction()).getBooleanLogic() == BooleanRestriction.BooleanLogic.OR) {
            BooleanRestriction restriction = (BooleanRestriction)entityQuery.getSearchRestriction();
            Iterable partitions = Iterables.partition((Iterable)restriction.getRestrictions(), (int)this.orBatchSize);
            ArrayList<EntityQuery> queries = new ArrayList<EntityQuery>();
            for (List partitionedRestrictions : partitions) {
                EntityQuery partitionedQuery = QueryBuilder.queryFor((Class)entityQuery.getReturnType(), (EntityDescriptor)entityQuery.getEntityDescriptor(), (SearchRestriction)Combine.anyOf((Collection)partitionedRestrictions), (int)0, (int)this.calculateMaxResults(entityQuery));
                queries.add(partitionedQuery);
            }
            if (queries.size() > 1) {
                return queries;
            }
        }
        return Lists.newArrayList((Object[])new EntityQuery[]{entityQuery});
    }

    @VisibleForTesting
    int calculateMaxResults(EntityQuery entityQuery) {
        return EntityQuery.addToMaxResults((int)entityQuery.getMaxResults(), (int)entityQuery.getStartIndex());
    }

    protected void appendQueryAsHQL(EntityQuery<?> query, HQLQuery hql) {
        String persistedClass = this.transformEntityToPersistedClass(query.getEntityDescriptor().getEntityType());
        String alias = HQLQueryTranslater.transformEntityToAlias(query.getEntityDescriptor().getEntityType());
        hql.appendFrom(persistedClass).append(" ").append(alias);
        String orderBy = alias + "." + this.resolveOrderByField(query);
        if (query.getReturnType() == String.class) {
            this.appendAttributes(hql, (List<String>)ImmutableList.of((Object)(alias + ".name")), orderBy, 0, values -> values[0]);
        } else {
            CustomDataFetcher fetcher = CustomDataFetchers.entityProducer(query.getReturnType());
            this.appendAttributes(hql, fetcher.attributes(alias), orderBy, 0, fetcher.getTransformer(0));
        }
        if (query.getEntityDescriptor().getEntityType() == Entity.GROUP && query.getEntityDescriptor().getGroupType() != null) {
            if (hql.whereRequired) {
                hql.appendWhere(HQL_AND);
            }
            this.appendGroupTypeRestrictionAsHQL(hql, query.getEntityDescriptor().getGroupType());
        }
        this.appendPropertyRestrictionIfNeeded(hql, query.getEntityDescriptor().getEntityType(), (Query<?>)query);
        hql.offsetResults(query.getStartIndex());
        hql.limitResults(query.getMaxResults());
    }

    private boolean hasRestriction(Query<?> query) {
        return query.getSearchRestriction() != null && !(query.getSearchRestriction() instanceof NullRestriction);
    }

    private void appendPropertyRestrictionIfNeeded(HQLQuery hql, Entity entityType, Query<?> query) {
        if (this.hasRestriction(query)) {
            if (hql.whereRequired) {
                hql.appendWhere(HQL_AND);
            }
            this.appendPropertyRestrictionAsHQL(hql, entityType, query.getSearchRestriction(), null);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected void appendPropertyRestrictionAsHQL(HQLQuery hql, Entity entityType, SearchRestriction restriction, @Nullable String attributeSharedAlias) {
        if (restriction instanceof PropertyRestriction) {
            PropertyRestriction propertyRestriction = (PropertyRestriction)restriction;
            if (MatchMode.NULL == propertyRestriction.getMatchMode()) {
                this.appendIsNullTermRestrictionAsHSQL(hql, entityType, propertyRestriction, attributeSharedAlias);
                return;
            } else if (String.class.equals((Object)propertyRestriction.getProperty().getPropertyType())) {
                this.appendStringTermRestrictionAsHQL(hql, entityType, (PropertyRestriction<String>)propertyRestriction, attributeSharedAlias);
                return;
            } else if (Boolean.class.equals((Object)propertyRestriction.getProperty().getPropertyType())) {
                this.appendBooleanTermRestrictionAsHQL(hql, entityType, (PropertyRestriction<Boolean>)propertyRestriction, attributeSharedAlias);
                return;
            } else if (Enum.class.isAssignableFrom(propertyRestriction.getProperty().getPropertyType())) {
                this.appendEnumTermRestrictionAsHQL(hql, entityType, (PropertyRestriction<Enum>)propertyRestriction, attributeSharedAlias);
                return;
            } else if (Date.class.isAssignableFrom(propertyRestriction.getProperty().getPropertyType())) {
                this.appendDateTermRestriction(hql, entityType, (PropertyRestriction<? extends Date>)propertyRestriction, attributeSharedAlias);
                return;
            } else {
                if (!Number.class.isAssignableFrom(propertyRestriction.getProperty().getPropertyType())) throw new IllegalArgumentException("ProperyRestriction unsupported: " + restriction.getClass());
                this.appendNumberTermRestriction(hql, entityType, (PropertyRestriction<? extends Number>)propertyRestriction, attributeSharedAlias);
            }
            return;
        } else {
            if (!(restriction instanceof BooleanRestriction)) throw new IllegalArgumentException("ProperyRestriction unsupported: " + restriction.getClass());
            this.appendMultiTermRestrictionAsHQL(hql, entityType, (BooleanRestriction)restriction);
        }
    }

    protected void appendIsNullTermRestrictionAsHSQL(HQLQuery hql, Entity entityType, PropertyRestriction<?> restriction, @Nullable String attributeSharedAlias) {
        this.appendEntityPropertyAsHQL(hql, entityType, restriction, attributeSharedAlias);
        hql.appendWhere("IS NULL");
    }

    private void appendNumberTermRestriction(HQLQuery hql, Entity entityType, PropertyRestriction<? extends Number> restriction, @Nullable String attributeSharedAlias) {
        this.appendEntityPropertyAsHQL(hql, entityType, restriction, attributeSharedAlias);
        this.appendComparableValueAsHQL(hql, restriction);
    }

    protected void appendDateTermRestriction(HQLQuery hql, Entity entityType, PropertyRestriction<? extends Date> restriction, @Nullable String attributeSharedAlias) {
        this.appendEntityPropertyAsHQL(hql, entityType, restriction, attributeSharedAlias);
        this.appendComparableValueAsHQL(hql, restriction);
    }

    protected void appendBooleanTermRestrictionAsHQL(HQLQuery hql, Entity entityType, PropertyRestriction<Boolean> restriction, @Nullable String attributeSharedAlias) {
        this.appendEntityPropertyAsHQL(hql, entityType, restriction, attributeSharedAlias);
        hql.appendWhere("= ").append(hql.addParameterPlaceholder(restriction.getValue()));
    }

    protected void appendEnumTermRestrictionAsHQL(HQLQuery hql, Entity entityType, PropertyRestriction<Enum> restriction, @Nullable String attributeSharedAlias) {
        this.appendEntityPropertyAsHQL(hql, entityType, restriction, attributeSharedAlias);
        hql.appendWhere("= ").append(hql.addParameterPlaceholder(restriction.getValue()));
    }

    protected void appendMultiTermRestrictionAsHQL(HQLQuery hql, Entity entityType, BooleanRestriction booleanRestriction) {
        String attributeSharedAlias = HQLQueryTranslater.getAttributeSharedAlias(hql, entityType, booleanRestriction);
        this.appendBooleanLogicWhereClause(booleanRestriction.getRestrictions(), hql, booleanRestriction.getBooleanLogic(), restriction -> this.appendPropertyRestrictionAsHQL(hql, entityType, (SearchRestriction)restriction, attributeSharedAlias));
    }

    private String resolveBooleanOperator(BooleanRestriction.BooleanLogic booleanLogic) {
        switch (booleanLogic) {
            case AND: {
                return HQL_AND;
            }
            case OR: {
                return " OR ";
            }
        }
        throw new IllegalArgumentException("BooleanLogic unsupported: " + booleanLogic);
    }

    private <T> void appendBooleanLogicWhereClause(Iterable<T> restrictions, HQLQuery hqlQuery, BooleanRestriction.BooleanLogic logic, Consumer<T> partitionConsumer) {
        String booleanOperator = this.resolveBooleanOperator(logic);
        hqlQuery.appendWhere("(");
        Iterator<T> iterator = restrictions.iterator();
        while (iterator.hasNext()) {
            partitionConsumer.accept(iterator.next());
            if (!iterator.hasNext()) continue;
            hqlQuery.appendWhere(booleanOperator);
        }
        hqlQuery.appendWhere(")");
    }

    @Nullable
    @VisibleForTesting
    static String getAttributeSharedAlias(HQLQuery hql, Entity entityType, BooleanRestriction booleanRestriction) {
        Optional<EntityJoiner> joiner;
        if (booleanRestriction.getBooleanLogic() == BooleanRestriction.BooleanLogic.OR && (joiner = EntityJoiner.forEntity(entityType)).isPresent()) {
            return joiner.get().leftJoinAttributesIfSecondary(hql, booleanRestriction);
        }
        return null;
    }

    @VisibleForTesting
    static Predicate<SearchRestriction> isSecondaryPropertyRestriction(Set<Property<?>> primaryProperties) {
        return searchRestriction -> searchRestriction instanceof PropertyRestriction && !primaryProperties.contains(((PropertyRestriction)searchRestriction).getProperty());
    }

    protected void appendStringTermRestrictionAsHQL(HQLQuery hql, Entity entityType, PropertyRestriction<String> restriction, @Nullable String attributeSharedAlias) {
        this.appendEntityPropertyAsHQL(hql, entityType, restriction, attributeSharedAlias);
        this.appendStringValueAsHQL(hql, restriction);
    }

    protected void appendEntityPropertyAsHQL(HQLQuery hql, Entity entityType, PropertyRestriction restriction, @Nullable String attributeSharedAlias) {
        switch (entityType) {
            case USER: {
                this.appendUserPropertyAsHQL(hql, restriction, attributeSharedAlias);
                break;
            }
            case GROUP: {
                this.appendGroupPropertyAsHQL(hql, restriction, attributeSharedAlias);
                break;
            }
            case TOKEN: {
                this.appendTokenPropertyAsHQL(hql, restriction);
                break;
            }
            case DIRECTORY: {
                this.appendDirectoryPropertyAsHQL(hql, restriction);
                break;
            }
            case APPLICATION: {
                this.appendApplicationPropertyAsHQL(hql, restriction);
                break;
            }
            case ALIAS: {
                this.appendAliasPropertyAsHQL(hql, restriction);
                break;
            }
            default: {
                throw new IllegalArgumentException("Cannot form property restriction for entity of type <" + entityType + ">");
            }
        }
    }

    private void appendAliasPropertyAsHQL(HQLQuery hql, PropertyRestriction restriction) {
        String alias = HQLQueryTranslater.transformEntityToAlias(Entity.ALIAS);
        if (restriction.getProperty().equals(AliasTermKeys.ALIAS)) {
            hql.appendWhere(alias).append(".").append(HQL_ALIAS_NAME);
        } else if (restriction.getProperty().equals(AliasTermKeys.APPLICATION_ID)) {
            hql.appendWhere(alias).append(".").append(HQL_ALIAS_APPLICATION_ID);
        } else {
            throw new IllegalArgumentException("Alias does not support searching by property: " + restriction.getProperty().getPropertyName());
        }
        hql.appendWhere(" ");
    }

    private void appendApplicationPropertyAsHQL(HQLQuery hql, PropertyRestriction restriction) {
        String alias = HQLQueryTranslater.transformEntityToAlias(Entity.APPLICATION);
        if (restriction.getProperty().equals(DirectoryTermKeys.NAME)) {
            hql.appendWhere(alias).append(".").append("lowerName");
        } else if (restriction.getProperty().equals(DirectoryTermKeys.ACTIVE)) {
            hql.appendWhere(alias).append(".").append("active");
        } else if (restriction.getProperty().equals(DirectoryTermKeys.TYPE)) {
            hql.appendWhere(alias).append(".").append("type");
        } else {
            throw new IllegalArgumentException("Application does not support searching by property: " + restriction.getProperty().getPropertyName());
        }
        hql.appendWhere(" ");
    }

    protected void appendDirectoryPropertyAsHQL(HQLQuery hql, PropertyRestriction restriction) {
        String alias = HQLQueryTranslater.transformEntityToAlias(Entity.DIRECTORY);
        if (restriction.getProperty().equals(DirectoryTermKeys.NAME)) {
            hql.appendWhere(alias).append(".").append("lowerName");
        } else if (restriction.getProperty().equals(DirectoryTermKeys.ACTIVE)) {
            hql.appendWhere(alias).append(".").append("active");
        } else if (restriction.getProperty().equals(DirectoryTermKeys.IMPLEMENTATION_CLASS)) {
            hql.appendWhere(alias).append(".").append(HQL_DIRECTORY_IMPLEMENTATION_CLASS);
        } else if (restriction.getProperty().equals(DirectoryTermKeys.TYPE)) {
            hql.appendWhere(alias).append(".").append("type");
        } else {
            throw new IllegalArgumentException("Directory does not support searching by property: " + restriction.getProperty().getPropertyName());
        }
        hql.appendWhere(" ");
    }

    protected void appendTokenPropertyAsHQL(HQLQuery hql, PropertyRestriction restriction) {
        String tokenAlias = HQLQueryTranslater.transformEntityToAlias(Entity.TOKEN);
        if (restriction.getProperty().equals(TokenTermKeys.NAME)) {
            hql.appendWhere(tokenAlias).append(".").append("name");
        } else if (restriction.getProperty().equals(TokenTermKeys.LAST_ACCESSED_TIME)) {
            hql.appendWhere(tokenAlias).append(".").append(HQL_TOKEN_LAST_ACCESSED_TIME);
        } else if (restriction.getProperty().equals(TokenTermKeys.DIRECTORY_ID)) {
            hql.appendWhere(tokenAlias).append(".").append(HQL_TOKEN_DIRECTORY_ID);
        } else if (restriction.getProperty().equals(TokenTermKeys.RANDOM_NUMBER)) {
            hql.appendWhere(tokenAlias).append(".").append(HQL_TOKEN_RANDOM_NUMBER);
        } else {
            throw new IllegalArgumentException("Token does not support searching by property: " + restriction.getProperty().getPropertyName());
        }
        hql.appendWhere(" ");
    }

    protected void appendGroupTypeRestrictionAsHQL(HQLQuery hql, GroupType groupType) {
        if (groupType != null) {
            String groupAlias = HQLQueryTranslater.transformEntityToAlias(Entity.GROUP);
            hql.appendWhere(groupAlias).append(".").append("type");
            hql.appendWhere(" = ").append(hql.addParameterPlaceholder(groupType));
        }
    }

    protected void appendGroupPropertyAsHQL(HQLQuery hql, PropertyRestriction restriction, @Nullable String attributeSharedAlias) {
        String groupAlias = HQLQueryTranslater.transformEntityToAlias(Entity.GROUP);
        if (restriction.getProperty().equals(GroupTermKeys.NAME)) {
            hql.appendWhere(groupAlias).append(".").append("lowerName");
        } else if (restriction.getProperty().equals(GroupTermKeys.DESCRIPTION)) {
            hql.appendWhere(groupAlias).append(".").append(HQL_GROUP_DESCRIPTION);
        } else if (restriction.getProperty().equals(GroupTermKeys.ACTIVE)) {
            hql.appendWhere(groupAlias).append(".").append("active");
        } else if (restriction.getProperty().equals(GroupTermKeys.CREATED_DATE)) {
            hql.appendWhere(groupAlias).append(".").append(HQL_CREATED_DATE);
        } else if (restriction.getProperty().equals(GroupTermKeys.UPDATED_DATE)) {
            hql.appendWhere(groupAlias).append(".").append(HQL_UPDATED_DATE);
        } else if (restriction.getProperty().equals(GroupTermKeys.LOCAL)) {
            hql.appendWhere(groupAlias).append(".").append(HQL_GROUP_LOCAL);
        } else if (restriction.getProperty().equals(GroupTermKeys.EXTERNAL_ID)) {
            hql.appendWhere(groupAlias).append(".").append("externalId");
        } else {
            if (restriction.getMatchMode() == MatchMode.NULL) {
                String attrAlias = HQL_ATTRIBUTE_ALIAS + hql.getNextAlias();
                hql.appendWhere("NOT EXISTS (SELECT 1").append(" FROM InternalGroupAttribute ").append(attrAlias).append(" WHERE ").append(groupAlias).append(".id = ").append(attrAlias).append(".group.id").append(HQL_AND).append(attrAlias).append(".").append("name").append(" = ").append(hql.addParameterPlaceholder(restriction.getProperty().getPropertyName())).append(")");
                hql.appendWhere(HQL_AND).append(hql.addParameterPlaceholder(null));
            } else {
                String attrAlias = attributeSharedAlias == null ? EntityJoiner.GROUP.leftJoinAttributes(hql) : attributeSharedAlias;
                hql.appendWhere(groupAlias).append(".id = ").append(attrAlias).append(".group.id").append(HQL_AND).append(attrAlias).append(".").append("name").append(" = ").append(hql.addParameterPlaceholder(restriction.getProperty().getPropertyName())).append(HQL_AND).append(attrAlias).append(".").append(HQL_ATTRIBUTE_LOWER_VALUE);
            }
            hql.requireDistinct();
        }
        hql.appendWhere(" ");
    }

    protected void appendUserPropertyAsHQL(HQLQuery hql, PropertyRestriction restriction, @Nullable String attributeSharedAlias) {
        String userAlias = HQLQueryTranslater.transformEntityToAlias(Entity.USER);
        if (restriction.getProperty().equals(UserTermKeys.USERNAME)) {
            hql.appendWhere(userAlias).append(".").append("lowerName");
        } else if (restriction.getProperty().equals(UserTermKeys.EMAIL)) {
            hql.appendWhere(userAlias).append(".").append(HQL_USER_EMAIL_ADDRESS);
        } else if (restriction.getProperty().equals(UserTermKeys.FIRST_NAME)) {
            hql.appendWhere(userAlias).append(".").append(HQL_USER_FIRST_NAME);
        } else if (restriction.getProperty().equals(UserTermKeys.LAST_NAME)) {
            hql.appendWhere(userAlias).append(".").append(HQL_USER_LAST_NAME);
        } else if (restriction.getProperty().equals(UserTermKeys.DISPLAY_NAME)) {
            hql.appendWhere(userAlias).append(".").append(HQL_USER_DISPLAY_NAME);
        } else if (restriction.getProperty().equals(UserTermKeys.ACTIVE)) {
            hql.appendWhere(userAlias).append(".").append("active");
        } else if (restriction.getProperty().equals(UserTermKeys.EXTERNAL_ID)) {
            hql.appendWhere(userAlias).append(".").append("externalId");
        } else if (restriction.getProperty().equals(UserTermKeys.CREATED_DATE)) {
            hql.appendWhere(userAlias).append(".").append(HQL_CREATED_DATE);
        } else if (restriction.getProperty().equals(UserTermKeys.UPDATED_DATE)) {
            hql.appendWhere(userAlias).append(".").append(HQL_UPDATED_DATE);
        } else {
            if (restriction.getMatchMode() == MatchMode.NULL) {
                String attrAlias = HQL_ATTRIBUTE_ALIAS + hql.getNextAlias();
                hql.appendWhere("NOT EXISTS (SELECT 1").append(" FROM InternalUserAttribute ").append(attrAlias).append(" WHERE ").append(userAlias).append(".id = ").append(attrAlias).append(".user.id").append(HQL_AND).append(attrAlias).append(".").append("name").append(" = ").append(hql.addParameterPlaceholder(restriction.getProperty().getPropertyName())).append(")");
                hql.appendWhere(HQL_AND).append(hql.addParameterPlaceholder(null));
            } else {
                String attrAlias = attributeSharedAlias == null ? EntityJoiner.USER.leftJoinAttributes(hql) : attributeSharedAlias;
                String attributeValueType = Number.class.isAssignableFrom(restriction.getProperty().getPropertyType()) ? HQL_ATTRIBUTE_NUMERIC_VALUE : HQL_ATTRIBUTE_LOWER_VALUE;
                hql.appendWhere(userAlias).append(".id = ").append(attrAlias).append(".user.id").append(HQL_AND).append(attrAlias).append(".").append("name").append(" = ").append(hql.addParameterPlaceholder(restriction.getProperty().getPropertyName())).append(HQL_AND).append(attrAlias).append(".").append(attributeValueType);
            }
            hql.requireDistinct();
        }
        hql.appendWhere(" ");
    }

    protected void appendStringValueAsHQL(HQLQuery hql, PropertyRestriction<String> restriction) {
        String value = HQLQueryTranslater.isLowercaseProperty((Property<String>)restriction.getProperty()) ? IdentifierUtils.toLowerCase((String)((String)restriction.getValue())) : (String)restriction.getValue();
        switch (restriction.getMatchMode()) {
            case STARTS_WITH: {
                hql.appendWhere("LIKE ").append(hql.addParameterPlaceholder(value + "%"));
                break;
            }
            case ENDS_WITH: {
                hql.appendWhere("LIKE ").append(hql.addParameterPlaceholder("%" + value));
                break;
            }
            case CONTAINS: {
                hql.appendWhere("LIKE ").append(hql.addParameterPlaceholder("%" + value + "%"));
                break;
            }
            default: {
                this.appendComparableValueAsHQL(hql, restriction, value);
            }
        }
    }

    @VisibleForTesting
    static boolean isLowercaseProperty(Property<String> property) {
        return !property.equals((Object)GroupTermKeys.DESCRIPTION) && !UserTermKeys.EXTERNAL_ID.equals(property);
    }

    protected void appendComparableValueAsHQL(HQLQuery hql, PropertyRestriction restriction) {
        this.appendComparableValueAsHQL(hql, restriction, restriction.getValue());
    }

    protected void appendComparableValueAsHQL(HQLQuery hql, PropertyRestriction restriction, Object normalizedValue) {
        switch (restriction.getMatchMode()) {
            case GREATER_THAN: {
                hql.appendWhere("> ").append(hql.addParameterPlaceholder(normalizedValue));
                break;
            }
            case GREATER_THAN_OR_EQUAL: {
                hql.appendWhere(">= ").append(hql.addParameterPlaceholder(normalizedValue));
                break;
            }
            case LESS_THAN: {
                hql.appendWhere("< ").append(hql.addParameterPlaceholder(normalizedValue));
                break;
            }
            case LESS_THAN_OR_EQUAL: {
                hql.appendWhere("<= ").append(hql.addParameterPlaceholder(normalizedValue));
                break;
            }
            default: {
                hql.appendWhere("= ").append(hql.addParameterPlaceholder(normalizedValue));
            }
        }
    }

    private static String transformEntityToAlias(Entity entity) {
        switch (entity) {
            case USER: {
                return "usr";
            }
            case GROUP: {
                return "grp";
            }
            case TOKEN: {
                return "token";
            }
            case DIRECTORY: {
                return "directory";
            }
            case APPLICATION: {
                return "application";
            }
            case ALIAS: {
                return "alias";
            }
        }
        throw new IllegalArgumentException("Cannot transform entity of type <" + entity + ">");
    }

    private String transformEntityToPersistedClass(Entity entity) {
        switch (entity) {
            case USER: {
                return InternalUser.class.getSimpleName();
            }
            case GROUP: {
                return InternalGroup.class.getSimpleName();
            }
            case TOKEN: {
                return Token.class.getSimpleName();
            }
            case DIRECTORY: {
                return DirectoryImpl.class.getSimpleName();
            }
            case APPLICATION: {
                return ApplicationImpl.class.getSimpleName();
            }
            case ALIAS: {
                return Alias.class.getSimpleName();
            }
        }
        throw new IllegalArgumentException("Cannot transform entity of type <" + entity + ">");
    }

    private String resolveOrderByField(EntityQuery query) {
        return this.resolveDefaultOrderByFieldForEntity(query.getEntityDescriptor().getEntityType());
    }

    private String resolveDefaultOrderByFieldForEntity(Entity entity) {
        switch (entity) {
            case USER: {
                return "lowerName";
            }
            case GROUP: {
                return "lowerName";
            }
            case TOKEN: {
                return "name";
            }
            case DIRECTORY: {
                return "lowerName";
            }
            case APPLICATION: {
                return "lowerName";
            }
            case ALIAS: {
                return "lowerName";
            }
        }
        throw new IllegalArgumentException("Cannot transform entity of type <" + entity + ">");
    }

    @VisibleForTesting
    public void setOrBatchSize(int orBatchSize) {
        this.orBatchSize = orBatchSize;
    }

    public int getOrBatchSize() {
        return this.orBatchSize;
    }

    private static enum EntityJoiner {
        USER(Entity.USER, UserTermKeys.ALL_USER_PROPERTIES, "attributes"),
        GROUP(Entity.GROUP, GroupTermKeys.ALL_GROUP_PROPERTIES, "attributes");

        private static final ImmutableMap<Entity, EntityJoiner> BY_ENTITY;
        private final Entity entity;
        private final Set<Property<?>> allProperties;
        private final String tableName;

        public static Optional<EntityJoiner> forEntity(Entity entity) {
            return Optional.ofNullable(BY_ENTITY.get((Object)entity));
        }

        private EntityJoiner(Entity entity, Set<Property<?>> allProperties, String tableName) {
            this.entity = (Entity)Preconditions.checkNotNull((Object)entity);
            this.allProperties = (Set)Preconditions.checkNotNull(allProperties);
            this.tableName = (String)Preconditions.checkNotNull((Object)tableName);
        }

        public String leftJoinAttributes(HQLQuery hql) {
            String attributeAlias = HQLQueryTranslater.HQL_ATTRIBUTE_ALIAS + hql.getNextAlias();
            hql.appendFrom(String.format(" LEFT JOIN %s.%s AS %s", HQLQueryTranslater.transformEntityToAlias(this.entity), this.tableName, attributeAlias));
            return attributeAlias;
        }

        public String leftJoinAttributesIfSecondary(HQLQuery hql, BooleanRestriction booleanRestriction) {
            if (booleanRestriction.getRestrictions().stream().anyMatch(HQLQueryTranslater.isSecondaryPropertyRestriction(this.allProperties))) {
                return this.leftJoinAttributes(hql);
            }
            return null;
        }

        static {
            BY_ENTITY = Maps.uniqueIndex(EnumSet.allOf(EntityJoiner.class), input -> input.entity);
        }
    }
}

