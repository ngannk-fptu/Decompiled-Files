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
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.oracle.OracleContentDdlHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.oracle.OracleContentPermissionDdlHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.oracle.OracleContentPermissionSetDdlHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.oracle.OracleSpaceDdlHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.oracle.OracleSpacePermissionDdlHelper;
import org.springframework.jdbc.core.JdbcTemplate;

public class OracleDenormalisedPermissionsDdlOperations
extends DenormalisedPermissionsDdlOperations {
    @Override
    public void createSpaceTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new OracleSpaceDdlHelper(jdbcTemplate).createTriggersAndFunctions();
    }

    @Override
    @VisibleForTesting
    void dropAllSpaceTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new OracleSpaceDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void enableSpaceServiceTriggers(JdbcTemplate jdbcTemplate) {
        new OracleSpaceDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableSpaceServiceTriggers(JdbcTemplate jdbcTemplate) {
        new OracleSpaceDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    @VisibleForTesting
    void dropAllSpacePermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new OracleSpacePermissionDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    public void createSpacePermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new OracleSpacePermissionDdlHelper(jdbcTemplate).createTriggersAndFunctions();
    }

    @Override
    void enableSpacePermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
        new OracleSpacePermissionDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableSpacePermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
        new OracleSpacePermissionDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    void createContentTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new OracleContentDdlHelper(jdbcTemplate).createTriggersAndFunctions();
    }

    @Override
    void dropAllContentTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new OracleContentDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void enableContentServiceTriggers(JdbcTemplate jdbcTemplate) {
        new OracleContentDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableContentServiceTriggers(JdbcTemplate jdbcTemplate) {
        new OracleContentDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    void createContentPermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new OracleContentPermissionDdlHelper(jdbcTemplate).createTriggersAndFunctions();
    }

    @Override
    void dropAllContentPermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new OracleContentPermissionDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void enableContentPermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
        new OracleContentPermissionDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableContentPermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
        new OracleContentPermissionDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    void createContentPermissionSetTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new OracleContentPermissionSetDdlHelper(jdbcTemplate).createTriggersAndFunctions();
    }

    @Override
    void dropAllContentPermissionSetTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new OracleContentPermissionSetDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void enableContentPermissionSetServiceTriggers(JdbcTemplate jdbcTemplate) {
        new OracleContentPermissionSetDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableContentPermissionSetServiceTriggers(JdbcTemplate jdbcTemplate) {
        new OracleContentPermissionSetDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    void dropIndex(JdbcTemplate jdbcTemplate, DenormalisedServiceStateRecord.ServiceType serviceType, String indexName, String tableName) {
        switch (serviceType) {
            case SPACE: {
                new OracleSpaceDdlHelper(jdbcTemplate).dropIndex(indexName, tableName);
                break;
            }
            case CONTENT: {
                new OracleContentDdlHelper(jdbcTemplate).dropIndex(indexName, tableName);
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
                return new OracleSpaceDdlHelper(jdbcTemplate).indexExist(indexName, tableName);
            }
            case CONTENT: {
                return new OracleContentDdlHelper(jdbcTemplate).indexExist(indexName, tableName);
            }
        }
        throw new IllegalStateException("Undefined service type: " + serviceType.name());
    }
}

