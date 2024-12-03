/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.NotImplementedException
 *  org.springframework.jdbc.core.JdbcTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.setup;

import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.domain.DenormalisedServiceStateRecord;
import com.atlassian.confluence.security.denormalisedpermissions.impl.setup.DenormalisedPermissionsDdlOperations;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.jdbc.core.JdbcTemplate;

class HsqlDenormalisedPermissionsDdlOperations
extends DenormalisedPermissionsDdlOperations {
    HsqlDenormalisedPermissionsDdlOperations() {
    }

    @Override
    public void createSpaceTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
    }

    @Override
    void dropAllSpaceTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
    }

    @Override
    void dropAllSpacePermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
    }

    @Override
    void enableSpaceServiceTriggers(JdbcTemplate jdbcTemplate) {
    }

    @Override
    void enableSpacePermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
    }

    @Override
    void disableSpacePermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
    }

    @Override
    void disableSpaceServiceTriggers(JdbcTemplate jdbcTemplate) {
    }

    @Override
    public void createSpacePermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
    }

    @Override
    void createContentTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
    }

    @Override
    void dropAllContentTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
    }

    @Override
    void enableContentServiceTriggers(JdbcTemplate jdbcTemplate) {
    }

    @Override
    void disableContentServiceTriggers(JdbcTemplate jdbcTemplate) {
    }

    @Override
    void createContentPermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
    }

    @Override
    void dropAllContentPermissionTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
    }

    @Override
    void enableContentPermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
    }

    @Override
    void disableContentPermissionServiceTriggers(JdbcTemplate jdbcTemplate) {
    }

    @Override
    void createContentPermissionSetTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
    }

    @Override
    void dropAllContentPermissionSetTriggersAndFunctions(JdbcTemplate jdbcTemplate) {
    }

    @Override
    void enableContentPermissionSetServiceTriggers(JdbcTemplate jdbcTemplate) {
    }

    @Override
    void disableContentPermissionSetServiceTriggers(JdbcTemplate jdbcTemplate) {
    }

    @Override
    void dropIndex(JdbcTemplate jdbcTemplate, DenormalisedServiceStateRecord.ServiceType serviceType, String indexName, String tableName) {
    }

    @Override
    boolean indexExist(JdbcTemplate jdbcTemplate, DenormalisedServiceStateRecord.ServiceType serviceType, String indexName, String tableName) {
        throw new NotImplementedException("Denormalised permissions aren't implemented for HSQL");
    }
}

