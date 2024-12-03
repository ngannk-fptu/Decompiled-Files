/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  org.hibernate.boot.model.naming.Identifier
 *  org.hibernate.boot.model.relational.QualifiedTableName
 *  org.hibernate.tool.schema.extract.spi.ColumnInformation
 *  org.hibernate.tool.schema.extract.spi.IndexInformation
 *  org.hibernate.tool.schema.extract.spi.TableInformation
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.DataAccessException
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.core.persistence.schema.api.SchemaInformationService;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.atlassian.confluence.upgrade.ddl.CreateIndexCommand;
import com.atlassian.confluence.upgrade.ddl.DdlExecutor;
import com.atlassian.confluence.upgrade.ddl.DropIndexCommand;
import java.util.Arrays;
import java.util.HashSet;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.QualifiedTableName;
import org.hibernate.tool.schema.extract.spi.ColumnInformation;
import org.hibernate.tool.schema.extract.spi.IndexInformation;
import org.hibernate.tool.schema.extract.spi.TableInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

public class UpdateSpacePermissionsIndexUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(UpdateSpacePermissionsIndexUpgradeTask.class);
    static final String SP_TABLE = "SPACEPERMISSIONS";
    static final String SP_COMP_IDX = "sp_comp_idx";
    static final String SP_COLUMN_PERMTYPE = "PERMTYPE";
    static final String SP_COLUMN_PERMGROUPNAME = "PERMGROUPNAME";
    private final DdlExecutor ddlExecutor;
    private final SchemaInformationService dbSchemaInformationService;

    public UpdateSpacePermissionsIndexUpgradeTask(DdlExecutor ddlExecutor, SchemaInformationService dbSchemaInformationService) {
        this.ddlExecutor = ddlExecutor;
        this.dbSchemaInformationService = dbSchemaInformationService;
    }

    public String getBuildNumber() {
        return "7108";
    }

    public String getShortDescription() {
        return "Update index sp_comp_idx when the definition is not correct";
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public void doUpgrade() throws Exception {
        try (SchemaInformationService.CloseableDatabaseInformation databaseInformation = this.dbSchemaInformationService.getDatabaseInformation();){
            TableInformation tableInformation = databaseInformation.getTableInformation(new QualifiedTableName(this.dbSchemaInformationService.getCurrentCatalog(), this.dbSchemaInformationService.getCurrentSchema(), new Identifier(SP_TABLE, false)));
            if (tableInformation == null) {
                log.warn("Table Metadata can not be found: SPACEPERMISSIONS");
                return;
            }
            IndexInformation indexInformation = tableInformation.getIndex(new Identifier(SP_COMP_IDX, false));
            if (this.columnsMatch(indexInformation, SP_COLUMN_PERMTYPE, SP_COLUMN_PERMGROUPNAME)) {
                return;
            }
        }
        log.info("Update sp_comp_idx for spacepermissions table");
        DropIndexCommand dropCmd = this.ddlExecutor.createDropIndexCommand(SP_COMP_IDX, SP_TABLE);
        try {
            this.ddlExecutor.executeDdl(Arrays.asList(dropCmd));
        }
        catch (DataAccessException tableInformation) {
            // empty catch block
        }
        CreateIndexCommand createCmd = this.ddlExecutor.createCreateIndexCommand(SP_COMP_IDX, SP_TABLE, SP_COLUMN_PERMTYPE, SP_COLUMN_PERMGROUPNAME);
        this.ddlExecutor.executeDdl(Arrays.asList(createCmd));
    }

    private boolean columnsMatch(IndexInformation indexInformation, String ... columns) {
        if (indexInformation == null) {
            return false;
        }
        if (indexInformation.getIndexedColumns().size() == columns.length) {
            HashSet<String> names = new HashSet<String>(Arrays.asList(columns));
            for (ColumnInformation columnInformation : indexInformation.getIndexedColumns()) {
                names.remove(columnInformation.getColumnIdentifier().getCanonicalName().toUpperCase());
            }
            return names.isEmpty();
        }
        return false;
    }
}

