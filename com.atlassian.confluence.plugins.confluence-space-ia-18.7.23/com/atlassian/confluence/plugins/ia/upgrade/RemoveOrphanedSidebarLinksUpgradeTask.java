/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
 *  com.atlassian.activeobjects.external.ModelVersion
 *  net.java.ao.DatabaseProvider
 *  net.java.ao.EntityManager
 *  net.java.ao.Query
 *  net.java.ao.schema.TableNameConverter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.ia.upgrade;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.external.ModelVersion;
import com.atlassian.confluence.plugins.ia.SidebarLink;
import java.sql.Connection;
import java.sql.PreparedStatement;
import net.java.ao.DatabaseProvider;
import net.java.ao.EntityManager;
import net.java.ao.Query;
import net.java.ao.schema.TableNameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveOrphanedSidebarLinksUpgradeTask
implements ActiveObjectsUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(RemoveOrphanedSidebarLinksUpgradeTask.class);

    public ModelVersion getModelVersion() {
        return ModelVersion.valueOf((String)"1");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void upgrade(ModelVersion modelVersion, ActiveObjects activeObjects) {
        activeObjects.migrate(new Class[]{SidebarLink.class});
        try {
            log.info("Starting Upgrade Task RemoveOrphanedSidebarLinksUpgradeTask");
            SidebarLink[] firstMatches = (SidebarLink[])activeObjects.find(SidebarLink.class, Query.select().limit(1));
            if (firstMatches == null || firstMatches.length == 0) {
                log.info("Skipping RemoveOrphanedSidebarLinksUpgradeTask because there's no data to be upgraded");
                return;
            }
            EntityManager entityManager = firstMatches[0].getEntityManager();
            DatabaseProvider databaseProvider = entityManager.getProvider();
            TableNameConverter tableNameConverter = entityManager.getNameConverters().getTableNameConverter();
            String processedSpaceKey = databaseProvider.processID("SPACE_KEY");
            String processedTableName = databaseProvider.withSchema(tableNameConverter.getName(SidebarLink.class));
            String innerSelect = "SELECT 1 FROM SPACES WHERE SPACES.SPACEKEY=" + processedSpaceKey;
            String whereClause = "NOT EXISTS (" + innerSelect + ")";
            String sqlStatement = "DELETE FROM " + processedTableName + " WHERE " + whereClause;
            try (Connection connection = databaseProvider.getConnection();){
                PreparedStatement preparedStatement = databaseProvider.preparedStatement(connection, (CharSequence)sqlStatement);
                int rowsRemoved = preparedStatement.executeUpdate();
                log.info("Finished Upgrade Task RemoveOrphanedSidebarLinksUpgradeTask");
                log.info("Cleaned up [{}] rows", (Object)rowsRemoved);
            }
        }
        catch (Exception ex) {
            log.warn("RemoveOrphanedSidebarLinksUpgradeTask did not complete: {}", (Object)ex.getMessage());
        }
    }
}

