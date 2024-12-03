/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.domain.DenormalisedServiceStateRecord;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.DenormalisedPermissionsDdlOperations;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.postgres.PostgresContentDdlHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.postgres.PostgresContentPermissionDdlHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.postgres.PostgresContentPermissionSetDdlHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.postgres.PostgresSpaceDdlHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.postgres.PostgresSpacePermissionDdlHelper;
import org.springframework.jdbc.core.JdbcTemplate;

public class PostgresDenormalisedPermissionsDdlOperations
extends DenormalisedPermissionsDdlOperations {
    @Override
    public void createSpaceTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new PostgresSpaceDdlHelper(jdbcTemplate).createTriggersAndFunctions();
    }

    @Override
    @VisibleForTesting
    void dropAllSpaceTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new PostgresSpaceDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    @VisibleForTesting
    void dropAllSpacePermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new PostgresSpacePermissionDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void enableSpaceServiceTriggers(JdbcTemplate jdbcTemplate) {
        new PostgresSpaceDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableSpaceServiceTriggers(JdbcTemplate jdbcTemplate) {
        new PostgresSpaceDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    public void createSpacePermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new PostgresSpacePermissionDdlHelper(jdbcTemplate).createTriggersAndFunctions();
    }

    @Override
    void enableSpacePermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
        new PostgresSpacePermissionDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableSpacePermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
        new PostgresSpacePermissionDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    void createContentTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new PostgresContentDdlHelper(jdbcTemplate).createTriggersAndFunctions();
    }

    @Override
    void dropAllContentTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new PostgresContentDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void enableContentServiceTriggers(JdbcTemplate jdbcTemplate) {
        new PostgresContentDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableContentServiceTriggers(JdbcTemplate jdbcTemplate) {
        new PostgresContentDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    void createContentPermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new PostgresContentPermissionDdlHelper(jdbcTemplate).createTriggersAndFunctions();
    }

    @Override
    void dropAllContentPermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new PostgresContentPermissionDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void enableContentPermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
        new PostgresContentPermissionDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableContentPermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
        new PostgresContentPermissionDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    void createContentPermissionSetTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new PostgresContentPermissionSetDdlHelper(jdbcTemplate).createTriggersAndFunctions();
    }

    @Override
    void dropAllContentPermissionSetTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new PostgresContentPermissionSetDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void enableContentPermissionSetServiceTriggers(JdbcTemplate jdbcTemplate) {
        new PostgresContentPermissionSetDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableContentPermissionSetServiceTriggers(JdbcTemplate jdbcTemplate) {
        new PostgresContentPermissionSetDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    void dropIndex(JdbcTemplate jdbcTemplate, DenormalisedServiceStateRecord.ServiceType serviceType, String indexName, String tableName) {
        switch (serviceType) {
            case SPACE: {
                new PostgresSpaceDdlHelper(jdbcTemplate).dropIndex(indexName);
                break;
            }
            case CONTENT: {
                new PostgresContentDdlHelper(jdbcTemplate).dropIndex(indexName);
                break;
            }
            default: {
                throw new IllegalStateException("Undefined service type: " + serviceType.name());
            }
        }
    }

    @Override
    boolean indexExist(JdbcTemplate jdbcTemplate, DenormalisedServiceStateRecord.ServiceType serviceType, String indexName, String tableName) {
        switch (serviceType) {
            case SPACE: {
                return new PostgresSpaceDdlHelper(jdbcTemplate).indexExist(indexName, tableName);
            }
            case CONTENT: {
                return new PostgresContentDdlHelper(jdbcTemplate).indexExist(indexName, tableName);
            }
        }
        throw new IllegalStateException("Undefined service type: " + serviceType.name());
    }
}

