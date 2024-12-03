/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.mma.model.processor;

import com.atlassian.migration.agent.mma.model.SpaceMetadata;
import com.atlassian.migration.agent.newexport.processor.RowProcessor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceMetadataRowProcessor
implements RowProcessor {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(SpaceMetadataRowProcessor.class);
    private static final String SPACEID_COLUMN = "SPACEID";
    private static final String SPACEKEY_COLUMN = "SPACEKEY";
    private static final String SPACENAME_COLUMN = "SPACENAME";
    private static final String SPACETYPE_COLUMN = "SPACETYPE";
    private static final String PAGE_BLOG_DRAFT_COUNT_COLUMN = "SUMOFPAGEBLOGDRAFTCOUNT";
    private static final String ATTACHMENT_SIZE_COLUMN = "ATTACHMENTSIZE";
    private static final String ATTACHMENT_COUNT_COLUMN = "ATTACHMENTCOUNT";
    private static final String ESTIMATED_MIGRATION_TIME_COLUMN = "ESTIMATEDMIGRATIONTIME";
    private static final String LAST_MODIFIED_COLUMN = "LASTMODIFIED";
    private final List<SpaceMetadata> result = new ArrayList<SpaceMetadata>();

    @Override
    public void process(ResultSet resultSet) {
        try {
            SpaceMetadata spaceResult = new SpaceMetadata(resultSet.getLong(SPACEID_COLUMN), resultSet.getString(SPACEKEY_COLUMN), resultSet.getString(SPACENAME_COLUMN), resultSet.getString(SPACETYPE_COLUMN), this.getLong(resultSet, PAGE_BLOG_DRAFT_COUNT_COLUMN), this.getLong(resultSet, ATTACHMENT_SIZE_COLUMN), this.getLong(resultSet, ATTACHMENT_COUNT_COLUMN), this.getLong(resultSet, ESTIMATED_MIGRATION_TIME_COLUMN), resultSet.getTimestamp(LAST_MODIFIED_COLUMN));
            this.result.add(spaceResult);
        }
        catch (SQLException e) {
            log.error("Error in processing space selector result", (Throwable)e);
            throw new RuntimeException("Error in processing Space Metadata from MIG_SPACE_STATISTIC", e);
        }
    }

    private Long getLong(ResultSet resultSet, String columnName) throws SQLException {
        Object columnValue = resultSet.getObject(columnName);
        return columnValue == null ? null : Long.valueOf(((Number)columnValue).longValue());
    }

    @Generated
    public List<SpaceMetadata> getResult() {
        return this.result;
    }
}

