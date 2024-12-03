/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogEntityType
 *  com.atlassian.crowd.audit.AuditLogEventSource
 *  com.atlassian.crowd.audit.AuditLogEventType
 *  com.atlassian.crowd.audit.query.AuditLogChangesetProjection
 *  com.atlassian.crowd.audit.query.AuditLogQuery
 *  com.atlassian.crowd.audit.query.AuditLogQueryAuthorRestriction
 *  com.atlassian.crowd.audit.query.AuditLogQueryEntityRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction$BooleanLogic
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.atlassian.crowd.search.hibernate.audit;

import com.atlassian.crowd.audit.AuditLogEntityType;
import com.atlassian.crowd.audit.AuditLogEventSource;
import com.atlassian.crowd.audit.AuditLogEventType;
import com.atlassian.crowd.audit.query.AuditLogChangesetProjection;
import com.atlassian.crowd.audit.query.AuditLogQuery;
import com.atlassian.crowd.audit.query.AuditLogQueryAuthorRestriction;
import com.atlassian.crowd.audit.query.AuditLogQueryEntityRestriction;
import com.atlassian.crowd.search.hibernate.HQLQuery;
import com.atlassian.crowd.search.hibernate.audit.AuditLogQueryProjectionTranslator;
import com.atlassian.crowd.search.hibernate.audit.BooleanHqlRestriction;
import com.atlassian.crowd.search.hibernate.audit.EmptyRestriction;
import com.atlassian.crowd.search.hibernate.audit.PrefixRestriction;
import com.atlassian.crowd.search.hibernate.audit.Restriction;
import com.atlassian.crowd.search.hibernate.audit.RestrictionGroup;
import com.atlassian.crowd.search.hibernate.audit.RestrictionWithJoin;
import com.atlassian.crowd.search.hibernate.audit.SimpleRestriction;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AuditLogQueryTranslator {
    static final String DIRECTORY_ALIAS = "directoryentities";
    static final String USER_ALIAS = "userentities";
    static final String GROUP_ALIAS = "groupentities";
    static final String AUDIT_LOG_ENTITY_NAME = "entities";
    static final String CHANGESET_ALIAS = "chset";
    static final String APPLICATION_ALIAS = "applicationentities";
    static final String CHANGESET_PROJECTION_KEY_ALIAS = "projection";
    static final String CHANGESET_TIMESTAMP_PROPERTY = "timestamp";
    static final String CHANGESET_ENTITY = "AuditLogChangesetEntity";
    static final String CHANGESET_AUTHOR_TYPE_PROPERTY = "authorType";
    static final String ENTITY_ID_PROPERTY = "entityId";
    static final String ENTITY_NAME_PROPERTY = "entityName";
    static final String ENTITY_TYPE_PROPERTY = "entityType";
    static final String ENTITY_PRIMARY_PROPERTY = "primary";
    static final String EVENT_TYPE_PROPERTY = "eventType";
    static final String EVENT_SOURCE_PROPERTY = "source";
    static final String AUTHOR_ID_PROPERTY = "authorId";
    static final String AUTHOR_NAME_PROPERTY = "authorName";

    public HQLQuery asHQL(AuditLogQuery query) {
        HQLQuery hqlQuery = new HQLQuery();
        this.appendFrom(hqlQuery, query);
        this.appendRestrictions(hqlQuery, query);
        this.configureStartIndexAndMaxResults(query, hqlQuery);
        this.appendOrderBy(query, hqlQuery);
        return hqlQuery;
    }

    private void configureStartIndexAndMaxResults(AuditLogQuery query, HQLQuery hqlQuery) {
        hqlQuery.offsetResults(query.getStartIndex());
        hqlQuery.limitResults(query.getMaxResults());
    }

    private void appendOrderBy(AuditLogQuery query, HQLQuery hqlQuery) {
        if (query.getProjection() == null) {
            hqlQuery.appendOrderBy("chset.timestamp DESC, chset.id DESC");
        } else {
            hqlQuery.appendOrderBy("projection ASC");
        }
    }

    private void appendRestrictions(HQLQuery hqlQuery, AuditLogQuery query) {
        BooleanHqlRestriction allClauses = this.createTopLevelClause(query);
        allClauses.visit(hqlQuery);
    }

    private BooleanHqlRestriction createTopLevelClause(AuditLogQuery query) {
        ImmutableList.Builder queryRestrictions = new ImmutableList.Builder();
        queryRestrictions.add((Object)this.createTimeRestrictions(query));
        queryRestrictions.add((Object)this.createActionsRestriction(query.getActions()));
        queryRestrictions.add((Object)this.createSourcesRestriction(query.getSources()));
        queryRestrictions.add((Object)this.createAuthorsRestriction(query.getAuthors()));
        queryRestrictions.add((Object)this.createUsersRestriction(query.getUsers()));
        queryRestrictions.add((Object)this.createGroupsRestriction(query.getGroups()));
        queryRestrictions.add((Object)this.createApplicationsRestriction(query.getApplications()));
        queryRestrictions.add((Object)this.createDirectoriesRestriction(query.getDirectories()));
        return new BooleanHqlRestriction(BooleanRestriction.BooleanLogic.AND, (List<Restriction>)queryRestrictions.build());
    }

    private Restriction createDirectoriesRestriction(List<AuditLogQueryEntityRestriction> directories) {
        return this.createEntityRestriction(directories, DIRECTORY_ALIAS, AuditLogEntityType.DIRECTORY);
    }

    private Restriction createUsersRestriction(List<AuditLogQueryEntityRestriction> users) {
        return this.createEntityRestriction(users, USER_ALIAS, AuditLogEntityType.USER);
    }

    private Restriction createGroupsRestriction(List<AuditLogQueryEntityRestriction> groups) {
        return this.createEntityRestriction(groups, GROUP_ALIAS, AuditLogEntityType.GROUP);
    }

    private Restriction createApplicationsRestriction(List<AuditLogQueryEntityRestriction> groups) {
        return this.createEntityRestriction(groups, APPLICATION_ALIAS, AuditLogEntityType.APPLICATION);
    }

    private Restriction createEntityRestriction(List<AuditLogQueryEntityRestriction> entities, String alias, AuditLogEntityType entityType) {
        return this.createClauseWithJoin(entities, "INNER JOIN chset.entities AS " + alias, alias, entityType);
    }

    private Restriction createAuthorsRestriction(List<AuditLogQueryAuthorRestriction> authors) {
        return this.createRestrictionGroup(this.appendBooleanLogicClauseForCollection(authors, this.createAuthorRestrictionMapper("chset.authorId", "chset.authorName")));
    }

    private Restriction createActionsRestriction(Collection<AuditLogEventType> actions) {
        return this.createRestrictionGroup(this.appendBooleanLogicClauseForCollection(actions, action -> new SimpleRestriction("chset.eventType", "=", action)));
    }

    private Restriction createSourcesRestriction(Collection<AuditLogEventSource> sources) {
        return this.createRestrictionGroup(this.appendBooleanLogicClauseForCollection(sources, source -> new SimpleRestriction("chset.source", "=", source)));
    }

    private Restriction createTimeRestrictions(AuditLogQuery query) {
        ArrayList<Restriction> restrictions = new ArrayList<Restriction>(2);
        if (query.getOnOrAfter() != null) {
            restrictions.add(new SimpleRestriction("chset.timestamp", ">=", query.getOnOrAfter().toEpochMilli()));
        }
        if (query.getBeforeOrOn() != null) {
            restrictions.add(new SimpleRestriction("chset.timestamp", "<=", query.getBeforeOrOn().toEpochMilli()));
        }
        return this.createBooleanRestriction(BooleanRestriction.BooleanLogic.AND, restrictions);
    }

    private void appendFrom(HQLQuery hqlQuery, AuditLogQuery query) {
        AuditLogChangesetProjection projection = query.getProjection();
        if (projection == null) {
            hqlQuery.appendSelect(CHANGESET_ALIAS);
        } else {
            hqlQuery.requireDistinct();
            hqlQuery.appendSelect(AuditLogQueryProjectionTranslator.selectFor(query));
            hqlQuery.setResultTransform(AuditLogQueryProjectionTranslator.resultMapperFor(query).andThen(Function.identity()));
        }
        hqlQuery.appendFrom(CHANGESET_ENTITY).append(" ").append(CHANGESET_ALIAS);
    }

    private <T, U extends Restriction> List<Restriction> appendBooleanLogicClauseForCollection(Collection<T> elements, Function<T, U> appender) {
        return elements.stream().map(appender).collect(Collectors.toList());
    }

    private Restriction createBooleanRestriction(BooleanRestriction.BooleanLogic booleanLogic, List<Restriction> restrictions) {
        if (restrictions.isEmpty()) {
            return new EmptyRestriction();
        }
        return new BooleanHqlRestriction(booleanLogic, restrictions);
    }

    private Restriction createRestrictionGroup(List<Restriction> restrictions) {
        if (restrictions.isEmpty()) {
            return new EmptyRestriction();
        }
        return new RestrictionGroup(restrictions);
    }

    private Restriction createClauseWithJoin(List<AuditLogQueryEntityRestriction> entities, String join, String alias, AuditLogEntityType entityType) {
        if (entities.isEmpty()) {
            return new EmptyRestriction();
        }
        ImmutableList.Builder restrictions = new ImmutableList.Builder();
        restrictions.add((Object)new SimpleRestriction(alias + "." + ENTITY_TYPE_PROPERTY, "=", entityType));
        restrictions.add((Object)new RestrictionGroup(entities.stream().map(this.createEntityRestrictionMapper(alias + "." + ENTITY_ID_PROPERTY, alias + "." + ENTITY_NAME_PROPERTY)).collect(Collectors.toList())));
        return new RestrictionWithJoin(join, new BooleanHqlRestriction(BooleanRestriction.BooleanLogic.AND, (List<Restriction>)restrictions.build()));
    }

    private Function<AuditLogQueryEntityRestriction, Restriction> createEntityRestrictionMapper(String idProperty, String nameProperty) {
        return entity -> {
            ImmutableList.Builder<Restriction> restrictions = this.buildNameIdRestrictions(idProperty, nameProperty, (AuditLogQueryEntityRestriction)entity);
            return this.createBooleanRestriction(BooleanRestriction.BooleanLogic.AND, (List<Restriction>)restrictions.build());
        };
    }

    private Function<AuditLogQueryAuthorRestriction, Restriction> createAuthorRestrictionMapper(String idProperty, String nameProperty) {
        return author -> {
            ImmutableList.Builder<Restriction> restrictions = this.buildNameIdRestrictions(idProperty, nameProperty, (AuditLogQueryEntityRestriction)author);
            if (author.getType() != null) {
                restrictions.add((Object)new SimpleRestriction("chset.authorType", "=", author.getType()));
            }
            return this.createBooleanRestriction(BooleanRestriction.BooleanLogic.AND, (List<Restriction>)restrictions.build());
        };
    }

    private ImmutableList.Builder<Restriction> buildNameIdRestrictions(String idProperty, String nameProperty, AuditLogQueryEntityRestriction entity) {
        ImmutableList.Builder restrictions = new ImmutableList.Builder();
        if (entity.getId() != null) {
            restrictions.add((Object)new SimpleRestriction(idProperty, "=", entity.getId()));
        } else if (!Strings.isNullOrEmpty((String)entity.getName())) {
            restrictions.add((Object)new SimpleRestriction(nameProperty, "=", entity.getName()));
        } else if (!Strings.isNullOrEmpty((String)entity.getNamePrefix())) {
            restrictions.add((Object)new PrefixRestriction(nameProperty, entity.getNamePrefix()));
        }
        return restrictions;
    }
}

