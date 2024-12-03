/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  com.atlassian.spring.container.ContainerManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.DataAccessException
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.core.persistence.hibernate.HibernateDatabaseCapabilities;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.atlassian.confluence.upgrade.ddl.AddUniqueConstraintCommand;
import com.atlassian.confluence.upgrade.ddl.AlterTableCommand;
import com.atlassian.confluence.upgrade.ddl.AlterTableExecutor;
import com.atlassian.confluence.upgrade.ddl.CreateIndexCommand;
import com.atlassian.confluence.upgrade.ddl.DdlExecutor;
import com.atlassian.confluence.upgrade.ddl.DropIndexCommand;
import com.atlassian.confluence.upgrade.ddl.NullChoice;
import com.atlassian.confluence.upgrade.upgradetask.BandanaKeyUniqueConstraintCleaner;
import com.atlassian.spring.container.ContainerManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

public class BandanaKeyUniqueConstraintUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(BandanaKeyUniqueConstraintUpgradeTask.class);
    public static final AddUniqueConstraintCommand UNIQUE_CONSTRAINT = new AddUniqueConstraintCommand("bandana_unique_key", Arrays.asList("BANDANACONTEXT", "BANDANAKEY"));
    private AlterTableExecutor alterTableExecutor;
    private DdlExecutor ddlExecutor;
    private BandanaKeyUniqueConstraintCleaner bandanaKeyUniqueConstraintCleaner;

    public BandanaKeyUniqueConstraintUpgradeTask(AlterTableExecutor alterTableExecutor, DdlExecutor ddlExecutor, BandanaKeyUniqueConstraintCleaner bandanaKeyUniqueConstraintCleaner) {
        this.alterTableExecutor = alterTableExecutor;
        this.ddlExecutor = ddlExecutor;
        this.bandanaKeyUniqueConstraintCleaner = bandanaKeyUniqueConstraintCleaner;
    }

    public String getBuildNumber() {
        return "3033";
    }

    public String getShortDescription() {
        return "Removes duplicate Bandana entries, entries with null context/key, and adds not null and unique constraints on the Bandana table's context and key columns";
    }

    private void doPreUpgradeCleanUp() {
        this.bandanaKeyUniqueConstraintCleaner.cleanUp();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void doUpgrade() throws Exception {
        HibernateDatabaseCapabilities databaseCapabilities = HibernateDatabaseCapabilities.from(BootstrapUtils.getBootstrapManager().getHibernateConfig());
        boolean isIndexDropNeeded = databaseCapabilities.isSqlServer() || databaseCapabilities.isH2();
        this.doPreUpgradeCleanUp();
        log.info("Beginning task to add not null and unique constraints on Bandana context and key columns");
        try {
            List<DropIndexCommand> dropKeyIndexCommands = Arrays.asList(this.ddlExecutor.createDropIndexCommand("band_key_idx", "BANDANA"));
            this.ddlExecutor.executeDdl(dropKeyIndexCommands);
        }
        catch (DataAccessException e) {
            log.info("Ignoring non-existence of band_key_idx constraint", (Throwable)e);
        }
        if (isIndexDropNeeded) {
            log.info("Dropping Bandana indexes to be able to alter columns in SQL Server and H2");
            List<DropIndexCommand> dropOtherIndexesCommands = Arrays.asList(this.ddlExecutor.createDropIndexCommand("band_context_idx", "BANDANA"), this.ddlExecutor.createDropIndexCommand("band_cont_key_idx", "BANDANA"));
            this.ddlExecutor.executeDdl(dropOtherIndexesCommands);
            log.info("Dropping unique constraints to be able to alter columns in SQL Server and H2");
            try {
                List<AlterTableCommand> dropUniqueConstraintCommands = Arrays.asList(this.alterTableExecutor.createDropUniqueConstraintIfExistsCommand("bandana_unique_key"));
                this.alterTableExecutor.alterTable("BANDANA", dropUniqueConstraintCommands);
            }
            catch (DataAccessException e) {
                log.info("Ignoring non-existence of bandana_unique_key constraint");
            }
        }
        try {
            log.info("Adding not null and unique constraints on Bandana context and key columns");
            ArrayList<AlterTableCommand> commands = new ArrayList<AlterTableCommand>(3);
            commands.add(this.alterTableExecutor.createAlterColumnNullChoiceCommand("BANDANACONTEXT", "varchar(255)", NullChoice.NOT_NULL));
            commands.add(this.alterTableExecutor.createAlterColumnNullChoiceCommand("BANDANAKEY", "varchar(100)", NullChoice.NOT_NULL));
            commands.add(UNIQUE_CONSTRAINT);
            this.alterTableExecutor.alterTable("BANDANA", commands);
            if (!isIndexDropNeeded) return;
        }
        catch (Throwable throwable) {
            if (!isIndexDropNeeded) throw throwable;
            log.info("Recreating Bandana indexes for SQL Server and H2");
            List<CreateIndexCommand> createOtherIndexesCommands = Arrays.asList(this.ddlExecutor.createCreateIndexCommand("band_context_idx", "BANDANA", "BANDANACONTEXT"), this.ddlExecutor.createCreateIndexCommand("band_cont_key_idx", "BANDANA", "BANDANACONTEXT", "BANDANAKEY"));
            this.ddlExecutor.executeDdl(createOtherIndexesCommands);
            throw throwable;
        }
        log.info("Recreating Bandana indexes for SQL Server and H2");
        List<CreateIndexCommand> createOtherIndexesCommands = Arrays.asList(this.ddlExecutor.createCreateIndexCommand("band_context_idx", "BANDANA", "BANDANACONTEXT"), this.ddlExecutor.createCreateIndexCommand("band_cont_key_idx", "BANDANA", "BANDANACONTEXT", "BANDANAKEY"));
        this.ddlExecutor.executeDdl(createOtherIndexesCommands);
    }

    @Deprecated(forRemoval=true)
    public static List<String> getUniqueConstraintSqlStatements() {
        AlterTableExecutor executor = (AlterTableExecutor)ContainerManager.getComponent((String)"alterTableExecutor");
        return BandanaKeyUniqueConstraintUpgradeTask.getUniqueConstraintSqlStatements(executor);
    }

    public static List<String> getUniqueConstraintSqlStatements(AlterTableExecutor executor) {
        return executor.getAlterTableStatements("BANDANA", List.of(UNIQUE_CONSTRAINT));
    }

    public boolean runOnSpaceImport() {
        return true;
    }

    public boolean breaksBackwardCompatibility() {
        return true;
    }
}

