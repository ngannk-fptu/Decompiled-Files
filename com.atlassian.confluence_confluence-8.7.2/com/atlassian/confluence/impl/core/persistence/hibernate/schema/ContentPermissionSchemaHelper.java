/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.impl.core.persistence.hibernate.schema;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.core.persistence.hibernate.HibernateDatabaseCapabilities;
import com.atlassian.confluence.upgrade.ddl.AddUniqueConstraintCommand;
import com.atlassian.confluence.upgrade.ddl.AlterTableCommand;
import com.atlassian.confluence.upgrade.ddl.AlterTableExecutor;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContentPermissionSchemaHelper {
    private static final String CONTENT_PERM_TABLE_NAME = "CONTENT_PERM";
    private static final String CONTENT_PERM_SET_TABLE_NAME = "CONTENT_PERM_SET";
    private static final String CPS_UNIQUE_TYPE_CONSTRAINT_NAME = "cps_unique_type";
    private static final String CP_UNIQUE_USER_GROUPS_CONSTRAINT_NAME = "cp_unique_user_groups";
    private static final String CP_UNIQUE_USER_CONSTRAINT_NAME = "cp_unique_user";
    private static final String CP_UNIQUE_GROUP_CONSTRAINT_NAME = "cp_unique_group";
    private static final AddUniqueConstraintCommand CPS_UNIQUE_TYPE_CONSTRAINT = new AddUniqueConstraintCommand("cps_unique_type", Arrays.asList("CONTENT_ID", "CONT_PERM_TYPE"));
    private static final AddUniqueConstraintCommand CP_UNIQUE_USER_GROUPS_CONSTRAINT = new AddUniqueConstraintCommand("cp_unique_user_groups", Arrays.asList("CPS_ID", "CP_TYPE", "USERNAME", "GROUPNAME"));
    private static final AddUniqueConstraintCommand CP_UNIQUE_USER_CONSTRAINT = new AddUniqueConstraintCommand("cp_unique_user", Arrays.asList("CPS_ID", "CP_TYPE", "USERNAME"));
    private static final AddUniqueConstraintCommand CP_UNIQUE_GROUP_CONSTRAINT = new AddUniqueConstraintCommand("cp_unique_group", Arrays.asList("CPS_ID", "CP_TYPE", "GROUPNAME"));

    static List<String> getContentPermissionUniqueConstraintSqlStatements(AlterTableExecutor executor) {
        HibernateDatabaseCapabilities hibernateConfig = HibernateDatabaseCapabilities.from(BootstrapUtils.getBootstrapManager().getHibernateConfig());
        ArrayList statements = Lists.newArrayListWithCapacity((int)2);
        statements.addAll(executor.getAlterTableStatements(CONTENT_PERM_TABLE_NAME, ContentPermissionSchemaHelper.getContentPermissionUniqueConstraintCommands(hibernateConfig)));
        statements.addAll(executor.getAlterTableStatements(CONTENT_PERM_SET_TABLE_NAME, ContentPermissionSchemaHelper.getContentPermissionSetUniqueConstraintCommands()));
        return statements;
    }

    private static List<AlterTableCommand> getContentPermissionUniqueConstraintCommands(HibernateDatabaseCapabilities hibernateConfig) {
        ArrayList contentPermCommands = Lists.newArrayListWithCapacity((int)2);
        if (hibernateConfig.uniqueAllowsAnyNullValues()) {
            if (hibernateConfig.uniqueAllowsMultipleNullValues()) {
                contentPermCommands.add(CP_UNIQUE_USER_CONSTRAINT);
                contentPermCommands.add(CP_UNIQUE_GROUP_CONSTRAINT);
            } else {
                contentPermCommands.add(CP_UNIQUE_USER_GROUPS_CONSTRAINT);
            }
        }
        return contentPermCommands;
    }

    private static List<AlterTableCommand> getContentPermissionSetUniqueConstraintCommands() {
        return Arrays.asList(CPS_UNIQUE_TYPE_CONSTRAINT);
    }
}

