/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  com.google.common.collect.ImmutableList
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.DataAccessException
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.core.persistence.hibernate.HibernateDatabaseCapabilities;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.atlassian.confluence.upgrade.ddl.AddUniqueConstraintCommand;
import com.atlassian.confluence.upgrade.ddl.AlterTableCommand;
import com.atlassian.confluence.upgrade.ddl.AlterTableExecutor;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

public class Hibernate5ConstraintNamesPostSchemaUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(Hibernate5ConstraintNamesPostSchemaUpgradeTask.class);
    private final HibernateDatabaseCapabilities databaseCapabilities;
    private final AlterTableExecutor alterTableExecutor;
    private static final String[] ADD_UNIQUE_CONSTRAINTS = new String[]{"ATTACHMENTDATA", "UK_mxrudo8qrpxb7w28dnoo64aec", "ATTACHMENTID", "CONFVERSION", "UK_osprt1myxoltvtd8yodb0besm", "BUILDNUMBER", "cwd_application", "UK_esg7ywl12bt4wt5h1ka27m6u3", "lower_application_name", "cwd_directory", "UK_ojmqo7ksu5dlpaqs0b9qf0k37", "lower_directory_name", "groups", "UK_7y2xug6xwfc0qe9tg9oer6gjc", "groupname", "logininfo", "UK_cxh64nyrevdya903riaky8hs0", "USERNAME", "os_group", "UK_dxfqn6n2b524nx69kq4hsgtcn", "groupname", "os_user", "UK_fbxi8ego2k3uwg0lngdwv05j", "username", "PLUGINDATA", "UK_dg9b9idpgjdj5ljfmnld9lshn", "FILENAME", "PLUGINDATA", "UK_6i3f2odnxreeous9k1baxbc0a", "PLUGINKEY", "scheduler_clustered_jobs", "UK_h41yn0carypy2jdlo4oapqo7m", "job_id", "SPACES", "UK_jp1ad5yufsih5r7lqrygakpug", "SPACEKEY", "TRUSTEDAPP", "UK_f48dl9nadsqeudry5cyura0du", "NAME", "TRUSTEDAPP", "UK_mqknjsql47jf4ue5kn4sdtbj0", "PUBLIC_KEY_ID", "users", "UK_3g1j96g94xpk3lpxl2qbl985x", "name"};

    public Hibernate5ConstraintNamesPostSchemaUpgradeTask(HibernateDatabaseCapabilities databaseCapabilities, AlterTableExecutor alterTableExecutor) {
        this.databaseCapabilities = databaseCapabilities;
        this.alterTableExecutor = alterTableExecutor;
    }

    public String getBuildNumber() {
        return "7107";
    }

    public String getShortDescription() {
        return "Convert hibernate 2 unique constraint names to the hibernate 5 naming style";
    }

    public void doUpgrade() throws Exception {
        log.info("Starting {}", (Object)((Object)((Object)this)).getClass().getSimpleName());
        if (!this.databaseCapabilities.isH2() && !this.databaseCapabilities.isHSQL()) {
            this.addUniqueConstraints();
        }
        log.info("Finished {}", (Object)((Object)((Object)this)).getClass().getSimpleName());
    }

    private void addUniqueConstraints() {
        for (int i = 0; i < ADD_UNIQUE_CONSTRAINTS.length; i += 3) {
            String tableName = ADD_UNIQUE_CONSTRAINTS[i];
            String constraintName = ADD_UNIQUE_CONSTRAINTS[i + 1];
            String columnNames = ADD_UNIQUE_CONSTRAINTS[i + 2];
            AddUniqueConstraintCommand command = this.alterTableExecutor.createAddUniqueConstraintCommand(constraintName, columnNames.split(","));
            ImmutableList commands = ImmutableList.of((Object)command);
            try {
                this.alterTableExecutor.alterTable(tableName, (List<? extends AlterTableCommand>)commands);
                continue;
            }
            catch (DataAccessException e) {
                log.info("Ignoring already existing unique constraint: {}.{}", (Object)tableName, (Object)columnNames);
            }
        }
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }
}

