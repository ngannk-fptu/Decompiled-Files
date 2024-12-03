/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Alert
 *  com.atlassian.diagnostics.AlertCriteria
 *  com.atlassian.diagnostics.CallbackResult
 *  com.atlassian.diagnostics.PageRequest
 *  com.atlassian.diagnostics.Severity
 *  com.atlassian.diagnostics.internal.dao.AlertEntity
 *  com.atlassian.diagnostics.internal.dao.AlertEntityDao
 *  com.atlassian.diagnostics.internal.dao.AlertMetric
 *  com.atlassian.diagnostics.internal.dao.MinimalAlertEntity
 *  com.atlassian.diagnostics.internal.dao.RowCallback
 *  com.atlassian.diagnostics.internal.dao.SimpleMinimalAlertEntity
 *  com.google.common.collect.Iterables
 *  javax.persistence.criteria.CriteriaBuilder
 *  javax.persistence.criteria.CriteriaBuilder$In
 *  javax.persistence.criteria.CriteriaDelete
 *  javax.persistence.criteria.CriteriaQuery
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Order
 *  javax.persistence.criteria.Path
 *  javax.persistence.criteria.Predicate
 *  javax.persistence.criteria.Root
 *  javax.persistence.criteria.Selection
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.ScrollMode
 *  org.hibernate.ScrollableResults
 *  org.hibernate.query.Query
 *  org.springframework.orm.hibernate5.HibernateCallback
 */
package com.atlassian.confluence.internal.diagnostics.persistence.dao.hibernate;

import com.atlassian.confluence.core.persistence.hibernate.ConfluenceHibernateObjectDao;
import com.atlassian.confluence.internal.diagnostics.persistence.dao.hibernate.AlertEntityImpl;
import com.atlassian.diagnostics.Alert;
import com.atlassian.diagnostics.AlertCriteria;
import com.atlassian.diagnostics.CallbackResult;
import com.atlassian.diagnostics.PageRequest;
import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.internal.dao.AlertEntity;
import com.atlassian.diagnostics.internal.dao.AlertEntityDao;
import com.atlassian.diagnostics.internal.dao.AlertMetric;
import com.atlassian.diagnostics.internal.dao.MinimalAlertEntity;
import com.atlassian.diagnostics.internal.dao.RowCallback;
import com.atlassian.diagnostics.internal.dao.SimpleMinimalAlertEntity;
import com.google.common.collect.Iterables;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.HibernateCallback;

public class HibernateAlertEntityDao
extends ConfluenceHibernateObjectDao
implements AlertEntityDao {
    public void deleteAll(@NonNull AlertCriteria alertCriteria) {
        this.withSession(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaDelete criteriaDelete = builder.createCriteriaDelete(AlertEntityImpl.class);
            Root root = criteriaDelete.from(AlertEntityImpl.class);
            List<Predicate> predicates = this.getCriteriaPredicates(builder, (Root<AlertEntityImpl>)root, alertCriteria);
            criteriaDelete.where(predicates.toArray(new Predicate[0]));
            session.createQuery(criteriaDelete).executeUpdate();
            return null;
        });
    }

    public Set<String> findAllComponentIds() {
        return (Set)this.withSession(session -> new HashSet(session.createQuery("SELECT DISTINCT issueComponentId from AlertEntityImpl", String.class).list()));
    }

    public Map<String, Severity> findAllIssueIds() {
        return (Map)this.withSession(session -> {
            Query query = session.createQuery("SELECT DISTINCT issueId, issueSeverity from AlertEntityImpl", Object[].class);
            return query.stream().collect(Collectors.toMap(HibernateAlertEntityDao.itemAt(0, String.class), HibernateAlertEntityDao.itemAt(1, Severity.class), (severity1, severity2) -> severity1.getId() > severity2.getId() ? severity1 : severity2));
        });
    }

    public Set<String> findAllNodeNames() {
        return (Set)this.withSession(session -> new HashSet(session.createQuery("SELECT DISTINCT nodeName from AlertEntityImpl", String.class).list()));
    }

    public Set<String> findAllPluginKeys() {
        return (Set)this.withSession(session -> new HashSet(session.createQuery("SELECT DISTINCT triggerPluginKey from AlertEntityImpl", String.class).list()));
    }

    public @Nullable AlertEntity getById(long id) {
        return (AlertEntity)this.getHibernateTemplate().get(AlertEntityImpl.class, (Serializable)Long.valueOf(id));
    }

    @Override
    public Class getPersistentClass() {
        return AlertEntityImpl.class;
    }

    public @NonNull AlertEntity save(@NonNull Alert alert) {
        AlertEntityImpl entity = new AlertEntityImpl(alert);
        this.getHibernateTemplate().save((Object)entity);
        return entity;
    }

    public void streamAll(@NonNull AlertCriteria alertCriteria, @NonNull RowCallback<AlertEntity> rowCallback, @NonNull PageRequest pageRequest) {
        this.withSession(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery criteriaQuery = builder.createQuery(AlertEntityImpl.class);
            Root root = criteriaQuery.from(AlertEntityImpl.class);
            Path id = root.get("id");
            Path timestamp = root.get("timestampUtc");
            List<Predicate> predicates = this.getCriteriaPredicates(builder, (Root<AlertEntityImpl>)root, alertCriteria);
            Query query = session.createQuery(criteriaQuery.select((Selection)root).where(predicates.toArray(new Predicate[0])).orderBy(new Order[]{builder.desc((Expression)timestamp), builder.desc((Expression)id)})).setFirstResult(pageRequest.getStart()).setMaxResults(pageRequest.getLimit() + 1);
            HibernateAlertEntityDao.scrollQuery(query, value -> {
                AlertEntityImpl alert = (AlertEntityImpl)value.get(0);
                try {
                    boolean bl = rowCallback.onRow((Object)alert) == CallbackResult.DONE;
                    return bl;
                }
                finally {
                    session.evict((Object)alert);
                }
            });
            return null;
        });
    }

    public void streamByIds(@NonNull Collection<Long> ids, @NonNull RowCallback<AlertEntity> rowCallback) {
        this.withSession(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery alertCriteriaQuery = builder.createQuery(AlertEntityImpl.class);
            Root root = alertCriteriaQuery.from(AlertEntityImpl.class);
            Path id = root.get("id");
            Path timestamp = root.get("timestampUtc");
            MutableBoolean done = new MutableBoolean(false);
            ArrayList<Long> sortedIds = new ArrayList<Long>(ids);
            sortedIds.sort(Comparator.comparingLong(Long::longValue).reversed());
            for (Collection partition : Iterables.partition(sortedIds, (int)500)) {
                CriteriaBuilder.In inClause = builder.in((Expression)id);
                partition.forEach(arg_0 -> ((CriteriaBuilder.In)inClause).value(arg_0));
                Query alertQuery = session.createQuery(alertCriteriaQuery.select((Selection)root).where((Expression)inClause).orderBy(new Order[]{builder.desc((Expression)timestamp), builder.desc((Expression)id)}));
                HibernateAlertEntityDao.scrollQuery(alertQuery, value -> {
                    AlertEntityImpl alert = (AlertEntityImpl)value.get(0);
                    done.setValue(rowCallback.onRow((Object)alert) == CallbackResult.DONE);
                    return done.getValue();
                });
                if (!done.isTrue()) continue;
                break;
            }
            return null;
        });
    }

    public void streamMetrics(@NonNull AlertCriteria criteria, @NonNull RowCallback<AlertMetric> rowCallback, @NonNull PageRequest pageRequest) {
        this.withSession(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery criteriaQuery = builder.createQuery(AlertMetric.class);
            Root root = criteriaQuery.from(AlertEntityImpl.class);
            Expression count = builder.count((Expression)root.get("id"));
            Path issueId = root.get("issueId");
            Path severity = root.get("issueSeverity");
            Path node = root.get("nodeName");
            Path pluginKey = root.get("triggerPluginKey");
            Path pluginVersion = root.get("triggerPluginVersion");
            Expression isNullPluginVersion = builder.selectCase().when((Expression)builder.isNull((Expression)pluginVersion), (Object)1).otherwise((Object)0);
            List<Predicate> predicates = this.getCriteriaPredicates(builder, (Root<AlertEntityImpl>)root, criteria);
            Query query = session.createQuery(criteriaQuery.select((Selection)builder.construct(AlertMetric.class, new Selection[]{issueId, severity, pluginKey, pluginVersion, node, count})).where(predicates.toArray(new Predicate[0])).groupBy(new Expression[]{issueId, severity, pluginKey, pluginVersion, node}).orderBy(new Order[]{builder.desc((Expression)severity), builder.asc((Expression)issueId), builder.asc((Expression)pluginKey), builder.desc(isNullPluginVersion), builder.asc((Expression)pluginVersion), builder.asc((Expression)node)})).setFirstResult(pageRequest.getStart()).setMaxResults(pageRequest.getLimit() + 1);
            HibernateAlertEntityDao.scrollQuery(query, value -> rowCallback.onRow((Object)((AlertMetric)value.get(0))) == CallbackResult.DONE);
            return null;
        });
    }

    public void streamMinimalAlerts(@NonNull AlertCriteria alertCriteria, @NonNull RowCallback<MinimalAlertEntity> rowCallback, @NonNull PageRequest pageRequest) {
        this.withSession(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery criteriaQuery = builder.createQuery(SimpleMinimalAlertEntity.class);
            Root root = criteriaQuery.from(AlertEntityImpl.class);
            Path id = root.get("id");
            Path timestamp = root.get("timestampUtc");
            Path issueId = root.get("issueId");
            Path nodeName = root.get("nodeName");
            Path pluginKey = root.get("triggerPluginKey");
            Path details = root.get("detailsJson");
            Expression detailsLength = builder.selectCase().when((Expression)builder.isNull((Expression)details), (Object)0).otherwise(builder.length((Expression)details));
            List<Predicate> predicates = this.getCriteriaPredicates(builder, (Root<AlertEntityImpl>)root, alertCriteria);
            Query query = session.createQuery(criteriaQuery.select((Selection)builder.construct(SimpleMinimalAlertEntity.class, new Selection[]{id, timestamp, issueId, pluginKey, nodeName, detailsLength})).where(predicates.toArray(new Predicate[predicates.size()])).orderBy(new Order[]{builder.desc((Expression)timestamp), builder.desc((Expression)id)}));
            HibernateAlertEntityDao.scrollQuery(query, value -> {
                SimpleMinimalAlertEntity row = (SimpleMinimalAlertEntity)value.get(0);
                return rowCallback.onRow((Object)row) == CallbackResult.DONE;
            });
            return null;
        });
    }

    private static <T> Function<Object, T> itemAt(int index, Class<T> clazz) {
        return row -> {
            Object[] data = (Object[])row;
            return index < data.length ? clazz.cast(data[index]) : null;
        };
    }

    private static void scrollQuery(Query query, SatiableConsumer<ScrollableResults> resultsConsumer) {
        try (ScrollableResults result = query.scroll(ScrollMode.FORWARD_ONLY);){
            boolean isDone = false;
            while (!isDone && result.next()) {
                isDone = resultsConsumer.accept(result);
            }
        }
    }

    private static Set<String> toLower(Set<String> values) {
        return values.stream().map(value -> StringUtils.lowerCase((String)value, (Locale)Locale.ROOT)).collect(Collectors.toSet());
    }

    private List<Predicate> getCriteriaPredicates(CriteriaBuilder builder, Root<AlertEntityImpl> root, AlertCriteria criteria) {
        Path timestamp = root.get("timestampUtc");
        ArrayList<Predicate> result = new ArrayList<Predicate>();
        if (!criteria.getComponentIds().isEmpty()) {
            result.add(root.get("issueComponentId").in((Collection)criteria.getComponentIds()));
        }
        if (!criteria.getIssueIds().isEmpty()) {
            result.add(root.get("issueId").in((Collection)criteria.getIssueIds()));
        }
        if (!criteria.getNodeNames().isEmpty()) {
            result.add(root.get("nodeNameLower").in(HibernateAlertEntityDao.toLower(criteria.getNodeNames())));
        }
        if (!criteria.getPluginKeys().isEmpty()) {
            result.add(root.get("triggerPluginKeyLower").in(HibernateAlertEntityDao.toLower(criteria.getPluginKeys())));
        }
        if (!criteria.getSeverities().isEmpty()) {
            result.add(root.get("issueSeverity").in((Collection)criteria.getSeverities()));
        }
        criteria.getSince().ifPresent(since -> result.add(builder.greaterThan((Expression)timestamp, (Comparable)Long.valueOf(since.toEpochMilli()))));
        criteria.getUntil().ifPresent(until -> result.add(builder.lessThanOrEqualTo((Expression)timestamp, (Comparable)Long.valueOf(until.toEpochMilli()))));
        return result;
    }

    private <T> T withSession(HibernateCallback<T> sessionCallback) {
        return (T)this.getHibernateTemplate().executeWithNativeSession(sessionCallback);
    }

    @FunctionalInterface
    public static interface SatiableConsumer<T> {
        public boolean accept(T var1);
    }
}

