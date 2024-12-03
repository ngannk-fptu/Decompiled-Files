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
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.mysql.MySqlContentDdlHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.mysql.MySqlContentPermissionDdlHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.mysql.MySqlContentPermissionSetDdlHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.mysql.MySqlSpaceDdlHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.mysql.MySqlSpacePermissionDdlHelper;
import org.springframework.jdbc.core.JdbcTemplate;

public class MySqlDenormalisedPermissionsDdlOperations
extends DenormalisedPermissionsDdlOperations {
    @Override
    public void createSpaceTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new MySqlSpaceDdlHelper(jdbcTemplate).createTriggersAndFunctions();
    }

    @Override
    public void createSpacePermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new MySqlSpacePermissionDdlHelper(jdbcTemplate).createTriggersAndFunctions();
    }

    @Override
    @VisibleForTesting
    void dropAllSpaceTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new MySqlSpaceDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    @VisibleForTesting
    void dropAllSpacePermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new MySqlSpacePermissionDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void enableSpaceServiceTriggers(JdbcTemplate jdbcTemplate) {
        new MySqlSpaceDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableSpaceServiceTriggers(JdbcTemplate jdbcTemplate) {
        new MySqlSpaceDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    void enableSpacePermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
        new MySqlSpacePermissionDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableSpacePermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
        new MySqlSpacePermissionDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    void createContentTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new MySqlContentDdlHelper(jdbcTemplate).createTriggersAndFunctions();
    }

    @Override
    void dropAllContentTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new MySqlContentDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void enableContentServiceTriggers(JdbcTemplate jdbcTemplate) {
        new MySqlContentDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableContentServiceTriggers(JdbcTemplate jdbcTemplate) {
        new MySqlContentDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    void createContentPermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new MySqlContentPermissionDdlHelper(jdbcTemplate).createTriggersAndFunctions();
    }

    @Override
    void dropAllContentPermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new MySqlContentPermissionDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void enableContentPermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
        new MySqlContentPermissionDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableContentPermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
        new MySqlContentPermissionDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    void createContentPermissionSetTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new MySqlContentPermissionSetDdlHelper(jdbcTemplate).createTriggersAndFunctions();
    }

    @Override
    void dropAllContentPermissionSetTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new MySqlContentPermissionSetDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void enableContentPermissionSetServiceTriggers(JdbcTemplate jdbcTemplate) {
        new MySqlContentPermissionSetDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableContentPermissionSetServiceTriggers(JdbcTemplate jdbcTemplate) {
        new MySqlContentPermissionSetDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    void dropIndex(JdbcTemplate jdbcTemplate, DenormalisedServiceStateRecord.ServiceType serviceType, String indexName, String tableName) {
        switch (serviceType) {
            case SPACE: {
                new MySqlSpaceDdlHelper(jdbcTemplate).dropIndex(indexName, tableName);
                break;
            }
            case CONTENT: {
                new MySqlContentDdlHelper(jdbcTemplate).dropIndex(indexName, tableName);
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
                return new MySqlSpaceDdlHelper(jdbcTemplate).indexExist(indexName, tableName);
            }
            case CONTENT: {
                return new MySqlContentDdlHelper(jdbcTemplate).indexExist(indexName, tableName);
            }
        }
        throw new IllegalStateException("Undefined service type: " + serviceType.name());
    }
}

