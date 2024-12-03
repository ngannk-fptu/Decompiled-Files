/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  lombok.Generated
 *  org.apache.commons.codec.binary.Hex
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.apache.commons.collections.CollectionUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.DataAccessException
 *  org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
 */
package com.atlassian.migration.agent.service.stepexecutor.export;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.migration.agent.entity.ExportCacheEntry;
import com.atlassian.migration.agent.entity.ExportType;
import com.atlassian.migration.agent.newexport.DescriptorBuilder;
import com.atlassian.migration.agent.store.ExportCacheStore;
import com.atlassian.migration.agent.store.jpa.impl.ConfluenceWrapperDataSource;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import lombok.Generated;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@ParametersAreNonnullByDefault
public class SpaceExportCacheService
implements JobRunner {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(SpaceExportCacheService.class);
    public static final JobRunnerKey RUNNER_KEY = JobRunnerKey.of((String)"migration-plugin:space-export-cache-service");
    public static final JobId CACHE_JOB_ID = JobId.of((String)"migration-plugin:space-export-cache-service-ttl-cleaner-job-id");
    public static final Duration CACHE_RESCHEDULE_INTERVAL = Duration.ofMinutes(5L);
    private static final Duration CACHE_TTL = Duration.ofDays(14L);
    private static final byte[] NULL_HASH = DigestUtils.sha256((byte[])new byte[]{11});
    private static final String CONTENT_IDS_QUERY = "SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId\nUNION\nSELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId)\nUNION\nSELECT CONTENTID FROM CONTENT WHERE PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId))\n";
    private static final ImmutableList<Query> BASE_ID_QUERIES = ImmutableList.of((Object)new Query("SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId AND LASTMODDATE > :cacheTime", "content"), (Object)new Query("SELECT CONTENTID FROM CONTENT WHERE LASTMODDATE > :cacheTime AND CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId)", "comment"), (Object)new Query("SELECT CONTENTID FROM CONTENT WHERE LASTMODDATE > :cacheTime AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId))", "Comment Content"), (Object)new Query("SELECT SPACEID FROM SPACES WHERE SPACEID = :spaceId AND LASTMODDATE > :cacheTime", "spaces"), (Object)new Query(String.format("SELECT NOTIFICATIONID FROM NOTIFICATIONS WHERE (SPACEID = :spaceId OR CONTENTID IN (%s)) AND LASTMODDATE > :cacheTime", "SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId\nUNION\nSELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId)\nUNION\nSELECT CONTENTID FROM CONTENT WHERE PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId))\n"), "notifications"), (Object)new Query("SELECT PERMID FROM SPACEPERMISSIONS WHERE SPACEID = :spaceId AND LASTMODDATE > :cacheTime", "spacepermissions"), (Object)new Query("SELECT TEMPLATEID FROM PAGETEMPLATES WHERE SPACEID = :spaceId AND LASTMODDATE > :cacheTime", "pagetemplates"), (Object)new Query("SELECT ID FROM CONTENT_LABEL WHERE LASTMODDATE > :cacheTime AND PAGETEMPLATEID IN (SELECT TEMPLATEID FROM PAGETEMPLATES WHERE SPACEID = :spaceId)", "content_label"), (Object)new Query(String.format("SELECT LABELID FROM LABEL WHERE LASTMODDATE > :cacheTime AND LABELID IN (SELECT LABELID FROM CONTENT_LABEL WHERE CONTENTID IN (%s))", "SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId\nUNION\nSELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId)\nUNION\nSELECT CONTENTID FROM CONTENT WHERE PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId))\n"), "label"), (Object)new Query(String.format("SELECT ID FROM CONTENT_PERM_SET WHERE LASTMODDATE > :cacheTime AND CONTENT_ID IN (%s)", "SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId\nUNION\nSELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId)\nUNION\nSELECT CONTENTID FROM CONTENT WHERE PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId))\n"), "content_perm_set"), (Object)new Query(String.format("SELECT ID FROM CONTENT_PERM WHERE LASTMODDATE > :cacheTime AND CPS_ID IN (SELECT ID FROM CONTENT_PERM_SET WHERE CONTENT_ID IN (%s))", "SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId\nUNION\nSELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId)\nUNION\nSELECT CONTENTID FROM CONTENT WHERE PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId))\n"), "content_perm"), (Object)new Query(String.format("SELECT LINKID FROM LINKS WHERE LASTMODDATE > :cacheTime AND CONTENTID IN (%s)", "SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId\nUNION\nSELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId)\nUNION\nSELECT CONTENTID FROM CONTENT WHERE PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId))\n"), "links"), (Object[])new Query[]{new Query(String.format("SELECT ID FROM LIKES WHERE CREATIONDATE > :cacheTime AND CONTENTID IN (%s)", "SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId\nUNION\nSELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId)\nUNION\nSELECT CONTENTID FROM CONTENT WHERE PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId))\n"), "likes"), new Query(String.format("SELECT RELATIONID FROM USERCONTENT_RELATION WHERE LASTMODDATE > :cacheTime AND TARGETCONTENTID IN (%s)", "SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId\nUNION\nSELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId)\nUNION\nSELECT CONTENTID FROM CONTENT WHERE PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId))\n"), "usercontent_relation"), new Query(String.format("SELECT RELATIONID FROM CONTENT_RELATION WHERE LASTMODDATE > :cacheTime and TARGETCONTENTID IN (%s) or TARGETCONTENTID IN (%s)", "SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId\nUNION\nSELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId)\nUNION\nSELECT CONTENTID FROM CONTENT WHERE PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId))\n", "SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId\nUNION\nSELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId)\nUNION\nSELECT CONTENTID FROM CONTENT WHERE PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId))\n"), "content_relation"), new Query(String.format("SELECT ID FROM CONTENT_LABEL WHERE LASTMODDATE > :cacheTime AND CONTENTID IN (%s)", "SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId\nUNION\nSELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId)\nUNION\nSELECT CONTENTID FROM CONTENT WHERE PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId))\n"), "content_label")});
    private static final ImmutableList<Query> PRE_V7_ID_QUERIES = ImmutableList.of((Object)new Query(String.format("SELECT LINKID FROM EXTRNLNKS WHERE LASTMODDATE > :cacheTime AND CONTENTID IN (%s)", "SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId\nUNION\nSELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId)\nUNION\nSELECT CONTENTID FROM CONTENT WHERE PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId))\n"), "extrnlnks"), (Object)new Query(String.format("SELECT LINKID FROM TRACKBACKLINKS WHERE LASTMODDATE > :cacheTime AND CONTENTID IN (%s)", "SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId\nUNION\nSELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId)\nUNION\nSELECT CONTENTID FROM CONTENT WHERE PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId))\n"), "trackbacklinks"));
    private static final Query BANDANA_QUERY = new Query("SELECT * FROM BANDANA WHERE BANDANACONTEXT = :spaceKey", "bandana");
    private static final Query OS_PROPERTY_ENTRY_QUERY = new Query(String.format("SELECT * FROM OS_PROPERTYENTRY WHERE entity_name = 'confluence_ContentEntityObject' AND entity_id IN (%s)", "SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId\nUNION\nSELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId)\nUNION\nSELECT CONTENTID FROM CONTENT WHERE PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE CONTENTTYPE = 'COMMENT' AND PAGEID IN (SELECT CONTENTID FROM CONTENT WHERE SPACEID = :spaceId))\n"), "os_propertyentry");
    private static final Query USER_MAPPING_QUERY = new Query("SELECT * FROM user_mapping", "user_mapping");
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;
    private final SpaceManager spaceManager;
    private final SchedulerService schedulerService;
    private final PluginTransactionTemplate ptx;
    private final ExportCacheStore exportCacheStore;
    private final DescriptorBuilder descriptorBuilder;
    private final Clock clock;

    public SpaceExportCacheService(ConfluenceWrapperDataSource dataSource, SpaceManager spaceManager, TransactionTemplate transactionTemplate, SchedulerService schedulerService, PluginTransactionTemplate ptx, ExportCacheStore exportCacheStore, DescriptorBuilder descriptorBuilder) throws SQLException {
        this(dataSource, spaceManager, transactionTemplate, schedulerService, ptx, exportCacheStore, descriptorBuilder, Clock.systemUTC());
    }

    public SpaceExportCacheService(ConfluenceWrapperDataSource dataSource, SpaceManager spaceManager, TransactionTemplate transactionTemplate, SchedulerService schedulerService, PluginTransactionTemplate ptx, ExportCacheStore exportCacheStore, DescriptorBuilder descriptorBuilder, Clock clock) throws SQLException {
        this(new NamedParameterJdbcTemplate((DataSource)((Object)dataSource)), spaceManager, transactionTemplate, schedulerService, ptx, exportCacheStore, descriptorBuilder, clock);
    }

    @VisibleForTesting
    SpaceExportCacheService(NamedParameterJdbcTemplate jdbcTemplate, SpaceManager spaceManager, TransactionTemplate transactionTemplate, SchedulerService schedulerService, PluginTransactionTemplate ptx, ExportCacheStore exportCacheStore, DescriptorBuilder descriptorBuilder, Clock clock) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
        this.spaceManager = spaceManager;
        this.schedulerService = schedulerService;
        this.ptx = ptx;
        this.exportCacheStore = exportCacheStore;
        this.descriptorBuilder = descriptorBuilder;
        this.clock = clock;
    }

    @PostConstruct
    public void postConstruct() throws SchedulerServiceException {
        this.schedulerService.registerJobRunner(RUNNER_KEY, (JobRunner)this);
        this.schedulerService.scheduleJob(CACHE_JOB_ID, JobConfig.forJobRunnerKey((JobRunnerKey)RUNNER_KEY).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(Schedule.forInterval((long)CACHE_RESCHEDULE_INTERVAL.toMillis(), (Date)new Date(System.currentTimeMillis() + 10000L))));
        log.debug("Successfully started CachingSpaceExportExecutor cleanup job.");
    }

    @PreDestroy
    public void cleanup() {
        this.schedulerService.unregisterJobRunner(RUNNER_KEY);
        this.schedulerService.unscheduleJob(CACHE_JOB_ID);
    }

    public JobRunnerResponse runJob(JobRunnerRequest request) {
        this.removeExpiredCacheEntries();
        return JobRunnerResponse.success();
    }

    private void removeExpiredCacheEntries() {
        log.info("Removing export cache entries that have exceeded their TTL.");
        long expiryTime = Instant.now(this.clock).minus(CACHE_TTL).toEpochMilli();
        this.ptx.write(() -> {
            List<ExportCacheEntry> deletedEntries = this.exportCacheStore.deleteExportCacheEntriesOlderThan(expiryTime);
            for (ExportCacheEntry deletedEntry : deletedEntries) {
                this.removeFileIgnoringErrors(deletedEntry);
            }
        });
    }

    private void removeFileIgnoringErrors(ExportCacheEntry entry) {
        try {
            Files.delete(Paths.get(entry.getFilePath(), new String[0]));
        }
        catch (IOException e) {
            log.error("Failed to clean up export cache file. Reason: " + e.getMessage(), (Throwable)e);
        }
    }

    public void cacheExportData(long snapshotTime, ExportType exportType, String spaceKey, String cloudId, boolean containsUserMigrationTask, String filePath) {
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            throw new IllegalArgumentException("Could not find the space associated with the key " + spaceKey);
        }
        QueryContext queryContext = new QueryContext(spaceKey, space.getId(), snapshotTime);
        this.exportCacheStore.createExportCacheEntry(new ExportCacheEntry(snapshotTime, exportType, spaceKey, cloudId, containsUserMigrationTask, filePath, BANDANA_QUERY.hashResults(this.jdbcTemplate, queryContext), OS_PROPERTY_ENTRY_QUERY.hashResults(this.jdbcTemplate, queryContext), USER_MAPPING_QUERY.hashResults(this.jdbcTemplate, queryContext)));
    }

    public Optional<ExportCacheEntry> getCacheEntry(String spaceKey, ExportType exportType, boolean containsUserMigrationTask, String cloudId) {
        Optional<ExportCacheEntry> maybeCachedValue = this.exportCacheStore.getExportCacheEntry(spaceKey, exportType, containsUserMigrationTask, cloudId);
        if (maybeCachedValue.isPresent()) {
            ExportCacheEntry cachedValue = maybeCachedValue.get();
            log.info("Found a cached export file: id={} spaceKey={}, exportType={}, containsUserMigrationTask={}, snapshotTime={}, filePath={}", new Object[]{cachedValue.getId(), spaceKey, exportType, containsUserMigrationTask, cachedValue.getSnapshotTime(), cachedValue.getFilePath()});
            if (!this.isStale(cachedValue)) {
                return Optional.of(cachedValue);
            }
            log.info("Discarding cached export file {} because it has been invalidated (space may have been modified).", (Object)cachedValue.getId());
            this.ptx.write(() -> {
                this.exportCacheStore.deleteExportCacheEntry(cachedValue.getId());
                this.removeFileIgnoringErrors(cachedValue);
            });
        }
        return Optional.empty();
    }

    private boolean isStale(ExportCacheEntry cacheEntry) {
        Path path = Paths.get(cacheEntry.getFilePath(), new String[0]);
        if (!Files.exists(path, new LinkOption[0])) {
            log.warn("Cannot use the export cache because the cache file at {} is missing.", (Object)path);
            return true;
        }
        Space space = this.spaceManager.getSpace(cacheEntry.getSpaceKey());
        if (space == null) {
            log.error("Could not find the space associated with the key {}", (Object)cacheEntry.getSpaceKey());
            return true;
        }
        return (Boolean)this.transactionTemplate.execute(() -> {
            QueryContext queryContext = new QueryContext(cacheEntry.getSpaceKey(), space.getId(), cacheEntry.getSnapshotTime());
            for (Query query : this.getIdQueries()) {
                List<String> modified = query.queryForList(this.jdbcTemplate, queryContext);
                if (!CollectionUtils.isNotEmpty(modified)) continue;
                log.info("The cached export file cannot be used because the following {} were changed: {}", (Object)query.getEntityName(), (Object)String.join((CharSequence)", ", modified));
                return true;
            }
            boolean allHashesMatch = this.checkResultHashes(queryContext, cacheEntry);
            log.info("All hashes match: {}", (Object)allHashesMatch);
            return !allHashesMatch;
        });
    }

    private boolean checkResultHashes(QueryContext queryContext, ExportCacheEntry cacheEntry) {
        boolean bandanaHash = this.hashesMatch(queryContext, BANDANA_QUERY, cacheEntry.getBandanaHash());
        boolean osPropertyHash = this.hashesMatch(queryContext, OS_PROPERTY_ENTRY_QUERY, cacheEntry.getOsPropertyEntryHash());
        boolean userMappingHash = this.hashesMatch(queryContext, USER_MAPPING_QUERY, cacheEntry.getUserMappingHash());
        return bandanaHash && osPropertyHash && userMappingHash;
    }

    private boolean hashesMatch(QueryContext queryContext, Query query, String expectedHash) {
        String actualHash = query.hashResults(this.jdbcTemplate, queryContext);
        if (!Objects.equals(actualHash, expectedHash)) {
            log.info("The cache content hashes do not match for {}", (Object)query.getEntityName());
            return false;
        }
        return true;
    }

    private ImmutableList<Query> getIdQueries() {
        if (this.descriptorBuilder.getBuildNumber() < 8201) {
            return new ImmutableList.Builder().addAll(BASE_ID_QUERIES).addAll(PRE_V7_ID_QUERIES).build();
        }
        return BASE_ID_QUERIES;
    }

    public boolean debugCacheEntry(String spaceKey, String cloudId) {
        Optional<ExportCacheEntry> maybeCacheEntry = this.getCacheEntry(spaceKey, ExportType.RAPID, false, cloudId);
        return maybeCacheEntry.isPresent();
    }

    static final class Query {
        private final String queryString;
        private final String entityName;

        public Query(String queryString, String entityName) {
            this.queryString = queryString;
            this.entityName = entityName;
        }

        public List<String> queryForList(NamedParameterJdbcTemplate jdbcTemplate, QueryContext queryContext) {
            return jdbcTemplate.queryForList(this.queryString, this.buildParams(queryContext), String.class);
        }

        public String hashResults(NamedParameterJdbcTemplate jdbcTemplate, QueryContext queryContext) {
            return (String)jdbcTemplate.query(this.queryString, this.buildParams(queryContext), Query::hashResultSet);
        }

        @VisibleForTesting
        static String hashResultSet(ResultSet rs) throws SQLException, DataAccessException {
            int columnCount = rs.getMetaData().getColumnCount();
            ArrayList<byte[]> hashes = new ArrayList<byte[]>(columnCount);
            while (rs.next()) {
                for (int i = 0; i < columnCount; ++i) {
                    String valueString = rs.getString(i + 1);
                    if (valueString != null) {
                        hashes.add(DigestUtils.sha256((String)valueString));
                        continue;
                    }
                    hashes.add(NULL_HASH);
                }
            }
            MessageDigest digest = DigestUtils.getSha256Digest();
            hashes.forEach(digest::update);
            return Hex.encodeHexString((byte[])digest.digest());
        }

        private Map<String, Object> buildParams(QueryContext queryContext) {
            HashMap<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("spaceKey", queryContext.spaceKey);
            paramMap.put("spaceId", queryContext.spaceId);
            paramMap.put("cacheTime", queryContext.cacheTime);
            return paramMap;
        }

        public String getEntityName() {
            return this.entityName;
        }
    }

    private static final class QueryContext {
        private final String spaceKey;
        private final long spaceId;
        private final Date cacheTime;

        private QueryContext(String spaceKey, long spaceId, long timestamp) {
            this.spaceKey = spaceKey;
            this.spaceId = spaceId;
            this.cacheTime = new Date(timestamp);
        }
    }
}

