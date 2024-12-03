/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Alert
 *  com.atlassian.diagnostics.AlertCriteria
 *  com.atlassian.diagnostics.CallbackResult
 *  com.atlassian.diagnostics.PageRequest
 *  com.atlassian.diagnostics.Severity
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.mutable.MutableLong
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal.dao;

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
import com.atlassian.diagnostics.internal.dao.SimpleAlertEntity;
import com.atlassian.diagnostics.internal.dao.SimpleMinimalAlertEntity;
import com.atlassian.diagnostics.internal.util.InstantPrecisionUtil;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryAlertEntityDao
implements AlertEntityDao {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryAlertEntityDao.class);
    private static final Comparator<AlertEntity> ALERT_ENTITY_COMPARATOR = (alert1, alert2) -> {
        int result = alert2.getTimestamp().compareTo(alert1.getTimestamp());
        if (result != 0) {
            return result;
        }
        return Long.compare(alert2.getId(), alert1.getId());
    };
    private final AtomicLong nextId = new AtomicLong(1000L);
    private final List<AlertEntity> entities = new CopyOnWriteArrayList<AlertEntity>();

    @Override
    public void deleteAll(@Nonnull AlertCriteria criteria) {
        this.entities.removeIf(entity -> InMemoryAlertEntityDao.matches(criteria, entity));
    }

    @Override
    public Set<String> findAllComponentIds() {
        Set<String> results = this.entities.stream().map(AlertEntity::getIssueComponentId).collect(Collectors.toSet());
        logger.info("Got findAllComponentIds: [{}]", results);
        return results;
    }

    @Override
    public Map<String, Severity> findAllIssueIds() {
        HashMap<String, Severity> result = new HashMap<String, Severity>();
        this.entities.forEach(entity -> result.putIfAbsent(entity.getIssueId(), entity.getIssueSeverity()));
        return result;
    }

    @Override
    public Set<String> findAllNodeNames() {
        Set<String> results = this.entities.stream().map(AlertEntity::getNodeName).filter(Objects::nonNull).collect(Collectors.toSet());
        logger.info("Got findAllNodeNames: [{}]", results);
        return results;
    }

    @Override
    public Set<String> findAllPluginKeys() {
        Set<String> results = this.entities.stream().map(AlertEntity::getTriggerPluginKey).filter(Objects::nonNull).collect(Collectors.toSet());
        logger.info("Got findAllPluginKeys: [{}]", results);
        return results;
    }

    @Override
    public AlertEntity getById(long id) {
        return this.entities.stream().filter(entity -> entity.getId() == id).findFirst().orElse(null);
    }

    @Override
    @Nonnull
    public AlertEntity save(@Nonnull Alert alert) {
        SimpleAlertEntity entity = new SimpleAlertEntity(alert, this.nextId.getAndIncrement());
        this.entities.add(entity);
        this.entities.sort(ALERT_ENTITY_COMPARATOR);
        logger.info("Saved {} entities", (Object)this.entities.size());
        logger.trace("Saved entities: [{}]", this.entities);
        return entity;
    }

    @Override
    public void streamAll(@Nonnull AlertCriteria criteria, @Nonnull RowCallback<AlertEntity> callback, @Nonnull PageRequest pageRequest) {
        int row = 0;
        int startOffset = pageRequest.getStart();
        int endOffset = startOffset + pageRequest.getLimit();
        for (AlertEntity entity : this.entities) {
            if (!InMemoryAlertEntityDao.matches(criteria, entity)) continue;
            if (row >= startOffset && row <= endOffset && callback.onRow(entity) == CallbackResult.DONE) {
                return;
            }
            if (row++ != endOffset) continue;
            return;
        }
    }

    @Override
    public void streamByIds(@Nonnull Collection<Long> ids, @Nonnull RowCallback<AlertEntity> callback) {
        HashSet<Long> remaining = new HashSet<Long>(ids);
        for (AlertEntity entity : this.entities) {
            if (!remaining.remove(entity.getId()) || callback.onRow(entity) != CallbackResult.DONE && !remaining.isEmpty()) continue;
            return;
        }
    }

    @Override
    public void streamMetrics(@Nonnull AlertCriteria criteria, @Nonnull RowCallback<AlertMetric> callback, @Nonnull PageRequest pageRequest) {
        LinkedHashMap metricCounts = new LinkedHashMap();
        this.entities.stream().filter(entity -> InMemoryAlertEntityDao.matches(criteria, entity)).forEach(entity -> metricCounts.computeIfAbsent(new MetricKey((AlertEntity)entity), key -> new MutableLong(0L)).increment());
        List metrics = metricCounts.entrySet().stream().map(entry -> {
            MetricKey key = (MetricKey)entry.getKey();
            return new AlertMetric(key.issueId, key.issueSeverity, key.pluginKey, key.pluginVersion, key.nodeName, ((MutableLong)entry.getValue()).getValue());
        }).collect(Collectors.toList());
        metrics.sort(Comparator.comparing(AlertMetric::getIssueSeverity, Comparator.comparingInt(Severity::getId).reversed()).thenComparing(AlertMetric::getIssueId).thenComparing(AlertMetric::getPluginKey).thenComparing(metric -> StringUtils.defaultString((String)metric.getPluginVersion(), (String)"")).thenComparing(AlertMetric::getNodeName));
        int row = 0;
        int startOffset = pageRequest.getStart();
        int endOffset = startOffset + pageRequest.getLimit();
        for (AlertMetric metric2 : metrics) {
            if (row >= startOffset && row <= endOffset && callback.onRow(metric2) == CallbackResult.DONE) {
                return;
            }
            if (row++ != endOffset) continue;
            break;
        }
    }

    @Override
    public void streamMinimalAlerts(@Nonnull AlertCriteria criteria, @Nonnull RowCallback<MinimalAlertEntity> callback, @Nonnull PageRequest pageRequest) {
        this.streamAll(criteria, entity -> callback.onRow(InMemoryAlertEntityDao.toMinimalAlert(entity)), pageRequest);
    }

    private static boolean matches(AlertCriteria criteria, AlertEntity entity) {
        return InMemoryAlertEntityDao.valueMatchesCaseInsensitively(criteria.getIssueIds(), entity.getIssueId()) && InMemoryAlertEntityDao.valueMatchesCaseInsensitively(criteria.getPluginKeys(), entity.getTriggerPluginKey()) && InMemoryAlertEntityDao.valueMatchesCaseInsensitively(criteria.getNodeNames(), entity.getNodeName()) && InMemoryAlertEntityDao.valueMatches(criteria.getSeverities(), entity.getIssueSeverity()) && InMemoryAlertEntityDao.valueMatches(criteria.getComponentIds(), entity.getIssueComponentId()) && criteria.getSince().map(since -> since.isBefore(InstantPrecisionUtil.truncateNanoSecondPrecision(entity.getTimestamp()))).orElse(true) != false && criteria.getUntil().map(until -> !until.isBefore(InstantPrecisionUtil.truncateNanoSecondPrecision(entity.getTimestamp()))).orElse(true) != false;
    }

    private static <T> boolean valueMatches(Set<T> acceptedValues, T value) {
        return acceptedValues.isEmpty() || acceptedValues.contains(value);
    }

    private static boolean valueMatchesCaseInsensitively(Set<String> acceptedValues, String value) {
        return acceptedValues.isEmpty() || acceptedValues.stream().anyMatch(val -> val.equalsIgnoreCase(value));
    }

    private static MinimalAlertEntity toMinimalAlert(AlertEntity entity) {
        return new SimpleMinimalAlertEntity(entity.getId(), entity.getTimestamp().toEpochMilli(), entity.getIssueId(), entity.getTriggerPluginKey(), entity.getNodeName(), StringUtils.length((CharSequence)entity.getDetailsJson()));
    }

    private static class MetricKey {
        private final String issueId;
        private final Severity issueSeverity;
        private final String nodeName;
        private final String pluginKey;
        private final String pluginVersion;

        private MetricKey(AlertEntity entity) {
            this.issueId = entity.getIssueId();
            this.issueSeverity = entity.getIssueSeverity();
            this.nodeName = entity.getNodeName();
            this.pluginKey = entity.getTriggerPluginKey();
            this.pluginVersion = entity.getTriggerPluginVersion();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            MetricKey metricKey = (MetricKey)o;
            return Objects.equals(this.issueId, metricKey.issueId) && Objects.equals(this.nodeName, metricKey.nodeName) && Objects.equals(this.pluginKey, metricKey.pluginKey) && Objects.equals(this.pluginVersion, metricKey.pluginVersion);
        }

        public int hashCode() {
            return Objects.hash(this.issueId, this.nodeName, this.pluginKey, this.pluginVersion);
        }
    }
}

