/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup;

import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.domain.DenormalisedServiceStateRecord;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.DenormalisedPermissionsDdlOperations;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.h2triggers.H2ContentDdlHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.h2triggers.H2ContentPermissionDdlHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.h2triggers.H2ContentPermissionSetDdlHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.h2triggers.H2SpaceDdlHelper;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.h2triggers.H2SpacePermissionDdlHelper;
import org.springframework.jdbc.core.JdbcTemplate;

class H2DenormalisedPermissionsDdlOperations
extends DenormalisedPermissionsDdlOperations {
    H2DenormalisedPermissionsDdlOperations() {
    }

    @Override
    public void createSpaceTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new H2SpaceDdlHelper(jdbcTemplate).createTriggersAndFunctions(false);
    }

    @Override
    void dropAllSpaceTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new H2SpaceDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void dropAllSpacePermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new H2SpacePermissionDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void enableSpaceServiceTriggers(JdbcTemplate jdbcTemplate) {
        new H2SpaceDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void enableSpacePermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
        new H2SpacePermissionDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableSpacePermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
        new H2SpacePermissionDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    void disableSpaceServiceTriggers(JdbcTemplate jdbcTemplate) {
        new H2SpaceDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    public void createSpacePermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new H2SpacePermissionDdlHelper(jdbcTemplate).createTriggersAndFunctions(false);
    }

    @Override
    void createContentTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new H2ContentDdlHelper(jdbcTemplate).createTriggersAndFunctions(false);
    }

    @Override
    void dropAllContentTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new H2ContentDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void enableContentServiceTriggers(JdbcTemplate jdbcTemplate) {
        new H2ContentDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableContentServiceTriggers(JdbcTemplate jdbcTemplate) {
        new H2ContentDdlHelper(jdbcTemplate).disableService();
    }

    @Override
    void createContentPermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new H2ContentPermissionDdlHelper(jdbcTemplate).createTriggersAndFunctions(false);
    }

    @Override
    void dropAllContentPermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new H2ContentPermissionDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void enableContentPermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
        new H2ContentPermissionDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableContentPermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
        new H2ContentPermissionDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void createContentPermissionSetTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new H2ContentPermissionSetDdlHelper(jdbcTemplate).createTriggersAndFunctions(false);
    }

    @Override
    void dropAllContentPermissionSetTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
        new H2ContentPermissionSetDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void enableContentPermissionSetServiceTriggers(JdbcTemplate jdbcTemplate) {
        new H2ContentPermissionSetDdlHelper(jdbcTemplate).enableService();
    }

    @Override
    void disableContentPermissionSetServiceTriggers(JdbcTemplate jdbcTemplate) {
        new H2ContentPermissionSetDdlHelper(jdbcTemplate).dropTriggersAndFunctions();
    }

    @Override
    void dropIndex(JdbcTemplate jdbcTemplate, DenormalisedServiceStateRecord.ServiceType serviceType, String indexName, String tableName) {
        switch (serviceType) {
            case SPACE: {
                new H2SpaceDdlHelper(jdbcTemplate).dropIndex(indexName);
                break;
            }
            case CONTENT: {
                new H2ContentDdlHelper(jdbcTemplate).dropIndex(indexName);
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
                return new H2SpaceDdlHelper(jdbcTemplate).indexExist(indexName, tableName);
            }
            case CONTENT: {
                return new H2ContentDdlHelper(jdbcTemplate).indexExist(indexName, tableName);
            }
        }
        throw new IllegalStateException("Undefined service type: " + serviceType.name());
    }
}

