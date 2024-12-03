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
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.sqlserver.SqlServerContentDdlHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.sqlserver.SqlServerContentPermissionDdlHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.sqlserver.SqlServerContentPermissionSetDdlHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.sqlserver.SqlServerSpaceDdlHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.sqlserver.SqlServerSpacePermissionDdlHelper;
import org.springframework.jdbc.core.JdbcTemplate;

public class SqlServerDenormalisedPermissionsDdlOperations
extends DenormalisedPermissionsDdlOperations {
    @Override
    public void createSpaceTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new SqlServerSpaceDdlHelper(jdbcTemplate).createTriggersAndFunctions();
    }

    @Override
    @VisibleForTesting
    void dropAllSpaceTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new SqlServerSpaceDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    @VisibleForTesting
    void dropAllSpacePermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new SqlServerSpacePermissionDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void enableSpaceServiceTriggers(JdbcTemplate jdbcTemplate) {
        new SqlServerSpaceDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableSpaceServiceTriggers(JdbcTemplate jdbcTemplate) {
        new SqlServerSpaceDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    public void createSpacePermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new SqlServerSpacePermissionDdlHelper(jdbcTemplate).createTriggersAndFunctions();
    }

    @Override
    void enableSpacePermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
        new SqlServerSpacePermissionDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableSpacePermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
        new SqlServerSpacePermissionDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    void createContentTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new SqlServerContentDdlHelper(jdbcTemplate).createTriggersAndFunctions();
    }

    @Override
    void dropAllContentTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new SqlServerContentDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void enableContentServiceTriggers(JdbcTemplate jdbcTemplate) {
        new SqlServerContentDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableContentServiceTriggers(JdbcTemplate jdbcTemplate) {
        new SqlServerContentDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    void createContentPermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new SqlServerContentPermissionDdlHelper(jdbcTemplate).createTriggersAndFunctions();
    }

    @Override
    void dropAllContentPermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new SqlServerContentPermissionDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void enableContentPermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
        new SqlServerContentPermissionDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableContentPermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
        new SqlServerContentPermissionDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    void createContentPermissionSetTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new SqlServerContentPermissionSetDdlHelper(jdbcTemplate).createTriggersAndFunctions();
    }

    @Override
    void dropAllContentPermissionSetTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new SqlServerContentPermissionSetDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void enableContentPermissionSetServiceTriggers(JdbcTemplate jdbcTemplate) {
        new SqlServerContentPermissionSetDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableContentPermissionSetServiceTriggers(JdbcTemplate jdbcTemplate) {
        new SqlServerContentPermissionSetDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    void dropIndex(JdbcTemplate jdbcTemplate, DenormalisedServiceStateRecord.ServiceType serviceType, String indexName, String tableName) {
        switch (serviceType) {
            case SPACE: {
                new SqlServerSpaceDdlHelper(jdbcTemplate).dropIndex(indexName, tableName);
                break;
            }
            case CONTENT: {
                new SqlServerContentDdlHelper(jdbcTemplate).dropIndex(indexName, tableName);
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
                return new SqlServerSpaceDdlHelper(jdbcTemplate).indexExist(indexName, tableName);
            }
            case CONTENT: {
                return new SqlServerContentDdlHelper(jdbcTemplate).indexExist(indexName, tableName);
            }
        }
        throw new IllegalStateException("Undefined service type: " + serviceType.name());
    }
}

