/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.model.stats.ContentSummary;
import com.atlassian.migration.agent.store.jpa.impl.DialectResolver;
import java.time.Duration;
import java.util.Collection;

public final class MigrationTimeEstimationUtils {
    private static final long SPACE_TIME_MS = 0L;
    private static final long USER_GROUP_TIME_MS = 597L;
    private static final long CONTENT_TIME_MS = 833L;
    private static final long BASE_MIGRATION_TIME_MS = 261480L;
    private static final long GLOBAL_ENTITIES_MS = 800L;
    private static final long TEAM_CALENDAR_TIME_MS = 833L;
    private static final long MS_PER_MB = 169L;
    private static final long BYTES_TO_MB = 1000000L;
    private final DialectResolver dialectResolver;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;

    public MigrationTimeEstimationUtils(DialectResolver dialectResolver, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        this.dialectResolver = dialectResolver;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
    }

    public static Duration getBaseMigrationTime() {
        return Duration.ofMillis(261480L);
    }

    private long calculateContentTime(ContentSummary contentSummary) {
        long numberOfBlogs = contentSummary.getNumberOfBlogs() != null ? contentSummary.getNumberOfBlogs() : 0L;
        long numberOfDrafts = contentSummary.getNumberOfDrafts() != null ? contentSummary.getNumberOfDrafts() : 0L;
        long numberOfPages = contentSummary.getNumberOfPages() != null ? contentSummary.getNumberOfPages() : 0L;
        return this.calculateContentTime(numberOfBlogs + numberOfDrafts + numberOfPages);
    }

    private long calculateContentTime(long sumOfPagesBlogsDraftsCount) {
        if (!this.migrationDarkFeaturesManager.isNewSpaceSelectorEnabled()) {
            return sumOfPagesBlogsDraftsCount * 833L;
        }
        switch (this.dialectResolver.getDbType()) {
            case MSSQL: {
                return sumOfPagesBlogsDraftsCount * 16L;
            }
            case MYSQL: {
                return sumOfPagesBlogsDraftsCount * 14L;
            }
            case ORACLE: {
                return sumOfPagesBlogsDraftsCount * 26L;
            }
            case POSTGRES: {
                return sumOfPagesBlogsDraftsCount * 14L;
            }
        }
        return sumOfPagesBlogsDraftsCount * 833L;
    }

    private long calculateAttachmentsTime(ContentSummary contentSummary) {
        return this.calculateAttachmentsTime(contentSummary.getAttachments().getTotalSize() != null ? contentSummary.getAttachments().getTotalSize() : 0L);
    }

    private long calculateAttachmentsTime(long totalAttachmentSize) {
        return totalAttachmentSize * 169L / 1000000L;
    }

    private static long calculateTeamCalendarTime(ContentSummary contentSummary) {
        return MigrationTimeEstimationUtils.calculateTeamCalendarTime(contentSummary.getNumberOfTeamCalendars() != null ? contentSummary.getNumberOfTeamCalendars() : 0L);
    }

    private static long calculateTeamCalendarTime(long teamCalendarCount) {
        return teamCalendarCount * 833L;
    }

    public static Duration estimateTotalUserGroupMigrationTime(int numberOfUsers, int numberOfGroups) {
        return Duration.ofMillis((long)(numberOfUsers + numberOfGroups) * 597L);
    }

    public Duration estimateSpaceMigrationTime(ContentSummary contentSummary) {
        return Duration.ofMillis(this.calculateContentTime(contentSummary) + this.calculateAttachmentsTime(contentSummary) + MigrationTimeEstimationUtils.calculateTeamCalendarTime(contentSummary) + 0L);
    }

    public long estimateSpaceMigrationTime(long sumOfPagesBlogsDrafts, long attachmentSize, long teamCalendarCount) {
        return this.calculateContentTime(sumOfPagesBlogsDrafts) + this.calculateAttachmentsTime(attachmentSize) + MigrationTimeEstimationUtils.calculateTeamCalendarTime(teamCalendarCount) + 0L;
    }

    public Duration estimateTotalSpaceMigrationTime(Collection<ContentSummary> spaces) {
        return spaces.stream().map(this::estimateSpaceMigrationTime).reduce(Duration.ZERO, Duration::plus);
    }

    public static Duration estimateGlobalEntitiesMigrationTime(long numGlobalEntities) {
        return Duration.ofMillis(numGlobalEntities * 800L);
    }

    private static final class NewSpaceSelectorCoefficients {
        private static final long ONE_PAGE_MS_POSTGRES = 14L;
        private static final long ONE_PAGE_MS_MYSQL = 14L;
        private static final long ONE_PAGE_MS_ORACLE = 26L;
        private static final long ONE_PAGE_MS_MSSQL = 16L;

        private NewSpaceSelectorCoefficients() {
        }
    }
}

