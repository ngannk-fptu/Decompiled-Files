/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.activeobjects.external.FailedFastCountException
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.audit.api.AuditEntityCursor
 *  com.atlassian.audit.api.AuditQuery
 *  com.atlassian.audit.api.util.pagination.Page
 *  com.atlassian.audit.api.util.pagination.Page$Builder
 *  com.atlassian.audit.api.util.pagination.PageRequest
 *  com.atlassian.audit.api.util.pagination.PageRequest$Builder
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  javax.annotation.Nonnull
 *  net.java.ao.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.ao.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.FailedFastCountException;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.audit.ao.dao.AoAuditEntityMapper;
import com.atlassian.audit.ao.dao.AuditEntityDao;
import com.atlassian.audit.ao.dao.AuditEntityMapper;
import com.atlassian.audit.ao.dao.AuditQueryMapper;
import com.atlassian.audit.ao.dao.entity.AoAuditEntity;
import com.atlassian.audit.api.AuditEntityCursor;
import com.atlassian.audit.api.AuditQuery;
import com.atlassian.audit.api.util.pagination.Page;
import com.atlassian.audit.api.util.pagination.PageRequest;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.plugin.configuration.PropertiesProvider;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AoAuditEntityDao
implements AuditEntityDao {
    public static final int MIN_RETAIN_LIMIT_DEFAULT = 10000;
    private static final Logger log = LoggerFactory.getLogger(AoAuditEntityDao.class);
    private static final String DELETE_BATCH_LIMIT_KEY = "plugin.audit.db.delete.batch.limit";
    private static final int DELETE_BATCH_LIMIT_DEFAULT = 10000;
    private final ActiveObjects ao;
    private final TransactionTemplate transactionTemplate;
    private final AuditQueryMapper auditQueryMapper;
    private final AuditEntityMapper auditEntityMapper;
    private final AoAuditEntityMapper aoAuditEntityMapper;
    private final Supplier<Integer> deleteBatchSizeSupplier;
    private final int minRetainLimit;

    public AoAuditEntityDao(ActiveObjects ao, TransactionTemplate transactionTemplate, AuditQueryMapper auditQueryMapper, AuditEntityMapper auditEntityMapper, AoAuditEntityMapper aoAuditEntityMapper, PropertiesProvider propertiesProvider) {
        this(ao, transactionTemplate, auditQueryMapper, auditEntityMapper, aoAuditEntityMapper, () -> propertiesProvider.getInteger(DELETE_BATCH_LIMIT_KEY, 10000), 10000);
    }

    @VisibleForTesting
    public AoAuditEntityDao(ActiveObjects ao, TransactionTemplate transactionTemplate, AuditQueryMapper auditQueryMapper, AuditEntityMapper auditEntityMapper, AoAuditEntityMapper aoAuditEntityMapper, Supplier<Integer> deleteBatchSizeSupplier, int minRetainLimit) {
        this.ao = Objects.requireNonNull(ao);
        this.transactionTemplate = transactionTemplate;
        this.auditQueryMapper = Objects.requireNonNull(auditQueryMapper);
        this.auditEntityMapper = Objects.requireNonNull(auditEntityMapper);
        this.aoAuditEntityMapper = Objects.requireNonNull(aoAuditEntityMapper);
        this.deleteBatchSizeSupplier = deleteBatchSizeSupplier;
        this.minRetainLimit = minRetainLimit;
    }

    @Override
    @Nonnull
    public Page<AuditEntity, AuditEntityCursor> findBy(@Nonnull AuditQuery auditQuery, @Nonnull PageRequest<AuditEntityCursor> pageRequest, int scanLimit) {
        return (Page)this.transactionTemplate.execute(() -> this.doFindBy(auditQuery, pageRequest, scanLimit));
    }

    private Page<AuditEntity, AuditEntityCursor> doFindBy(@Nonnull AuditQuery auditQuery, @Nonnull PageRequest<AuditEntityCursor> pageRequest, int scanLimit) {
        AuditQuery scanLimitQuery;
        Objects.requireNonNull(auditQuery, "auditQuery");
        Objects.requireNonNull(pageRequest, "pageRequest");
        if (scanLimit == Integer.MAX_VALUE) {
            scanLimitQuery = AuditQuery.builder((AuditQuery)auditQuery).build();
        } else {
            AoAuditEntity[] last = (AoAuditEntity[])this.ao.find(AoAuditEntity.class, Query.select().where(String.format("%s >= ? AND %s <= ?", "ENTITY_TIMESTAMP", "ENTITY_TIMESTAMP"), new Object[]{auditQuery.getFrom().orElse(Instant.EPOCH).toEpochMilli(), auditQuery.getTo().orElse(Instant.now()).toEpochMilli()}).limit(1).order(String.format("%s DESC", "ID")));
            if (last.length == 0) {
                return Page.emptyPage();
            }
            scanLimitQuery = AuditQuery.builder((AuditQuery)auditQuery).minId(Long.valueOf(Math.max(auditQuery.getMinId().orElse(0L), last[0].getId() - (long)scanLimit))).build();
        }
        Query query = this.auditQueryMapper.map(scanLimitQuery, pageRequest);
        return this.createPage(pageRequest, (AoAuditEntity[])this.ao.find(AoAuditEntity.class, query));
    }

    private Page<AuditEntity, AuditEntityCursor> createPage(@Nonnull PageRequest<AuditEntityCursor> pageRequest, AoAuditEntity[] aoAuditEntities) {
        List entities = Arrays.stream(aoAuditEntities).map(this.aoAuditEntityMapper::map).limit(pageRequest.getLimit()).collect(Collectors.toList());
        if (entities.isEmpty()) {
            return Page.emptyPage();
        }
        AoAuditEntity lastAuditEntity = aoAuditEntities[Math.min(pageRequest.getLimit(), aoAuditEntities.length) - 1];
        PageRequest nextPageRequest = new PageRequest.Builder().cursor((Object)new AuditEntityCursor(Instant.ofEpochMilli(lastAuditEntity.getTimestamp()), lastAuditEntity.getId().longValue())).limit(pageRequest.getLimit()).build();
        return new Page.Builder(entities, aoAuditEntities.length <= pageRequest.getLimit()).nextPageRequest(nextPageRequest).build();
    }

    @Override
    public void stream(@Nonnull AuditQuery auditQuery, @Nonnull Consumer<AuditEntity> consumer, int offset, int limit) {
        Objects.requireNonNull(auditQuery, "auditQuery");
        Objects.requireNonNull(consumer, "consumer");
        Query query = this.auditQueryMapper.map(auditQuery).order(String.format("%s DESC, %s DESC", "ENTITY_TIMESTAMP", "ID")).offset(offset).limit(limit);
        this.transactionTemplate.execute(() -> {
            this.ao.stream(AoAuditEntity.class, query, aoAuditEntity -> consumer.accept(this.aoAuditEntityMapper.map((AoAuditEntity)aoAuditEntity)));
            return null;
        });
    }

    @Override
    public void save(@Nonnull List<AuditEntity> auditEntities) {
        Objects.requireNonNull(auditEntities, "auditEntities");
        log.trace("#save auditEntities={}", auditEntities);
        this.transactionTemplate.execute(() -> {
            this.ao.create(AoAuditEntity.class, auditEntities.stream().map(this.auditEntityMapper::map).collect(Collectors.toList()));
            return null;
        });
    }

    @Override
    public void save(@Nonnull AuditEntity auditEntity) {
        Objects.requireNonNull(auditEntity, "auditEntity");
        log.trace("#save auditEntity={}", (Object)auditEntity);
        this.transactionTemplate.execute(() -> (AoAuditEntity)this.ao.create(AoAuditEntity.class, this.auditEntityMapper.map(auditEntity)));
    }

    @Override
    public void removeBefore(Instant before) {
        int deletedCount;
        int batchSize = this.deleteBatchSizeSupplier.get();
        log.trace("#removeBefore before={}, batchSize={}", (Object)before, (Object)batchSize);
        do {
            deletedCount = (Integer)this.transactionTemplate.execute(() -> {
                AoAuditEntity[] last = (AoAuditEntity[])this.ao.find(AoAuditEntity.class, Query.select((String)String.format("%s, %s", "ENTITY_TIMESTAMP", "ID")).where(String.format("%s < ?", "ENTITY_TIMESTAMP"), new Object[]{before.toEpochMilli()}).limit(1).offset(batchSize - 1).order(String.format("%s, %s", "ENTITY_TIMESTAMP", "ID")));
                if (last.length == 0) {
                    return this.ao.deleteWithSQL(AoAuditEntity.class, String.format("%s < ?", "ENTITY_TIMESTAMP"), new Object[]{before.toEpochMilli()});
                }
                return this.ao.deleteWithSQL(AoAuditEntity.class, String.format("%s <= ?", "ENTITY_TIMESTAMP"), new Object[]{last[0].getTimestamp()});
            });
            log.trace("#removeBefore before={}, deletedCount={}", (Object)before, (Object)deletedCount);
        } while (deletedCount > 0);
    }

    @Override
    public int fastCountEstimate() {
        return (Integer)this.transactionTemplate.execute(() -> {
            try {
                return this.ao.getFastCountEstimate(AoAuditEntity.class);
            }
            catch (FailedFastCountException e) {
                log.warn("Something went wrong with getting a (fast) count estimate, falling back (once off) to using the regular count method for audit events which can be slower", (Throwable)e);
                return this.ao.count(AoAuditEntity.class);
            }
            catch (Exception e) {
                log.error("Something went wrong while getting an estimate of the number of audit events in the DB", (Throwable)e);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public int count() {
        return (Integer)this.transactionTemplate.execute(() -> this.ao.count(AoAuditEntity.class));
    }

    @Override
    public int count(@Nonnull AuditQuery auditQuery) {
        Objects.requireNonNull(auditQuery, "auditQuery");
        Query query = this.auditQueryMapper.map(auditQuery);
        return (Integer)this.transactionTemplate.execute(() -> this.ao.count(AoAuditEntity.class, query));
    }

    @Override
    public void retainRecent(int limit) {
        int deletedCount;
        if (limit < this.minRetainLimit) {
            throw new IllegalArgumentException("Invalid retain limit : " + limit);
        }
        int batchSize = this.deleteBatchSizeSupplier.get();
        for (int toBeDeleted = this.count() - limit; toBeDeleted > 0 && (deletedCount = this.deleteOldest(Math.min(toBeDeleted, batchSize)).intValue()) != 0; toBeDeleted -= deletedCount) {
        }
    }

    private Integer deleteOldest(int count) {
        return (Integer)this.transactionTemplate.execute(() -> {
            AoAuditEntity[] last = (AoAuditEntity[])this.ao.find(AoAuditEntity.class, Query.select((String)String.format("%s,%s", "ENTITY_TIMESTAMP", "ID")).limit(1).offset(count - 1).order(String.format("%s, %s", "ENTITY_TIMESTAMP", "ID")));
            if (last.length == 0) {
                return 0;
            }
            return this.ao.deleteWithSQL(AoAuditEntity.class, String.format("%s < ? OR (%s = ? AND %s <= ?)", "ENTITY_TIMESTAMP", "ENTITY_TIMESTAMP", "ID"), new Object[]{last[0].getTimestamp(), last[0].getTimestamp(), last[0].getId()});
        });
    }
}

