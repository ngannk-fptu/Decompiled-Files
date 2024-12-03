/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters.site;

import com.atlassian.confluence.impl.backuprestore.backup.exporters.DatabaseExporterHelper;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.site.QueryProvider;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import java.util.Arrays;
import java.util.List;

public class BucketPropertySetItemQueryProvider
implements QueryProvider {
    private final String initialSqlQuery;
    private final String repetitiveSqlQuery;
    private final List<String> idColumnNames = Arrays.asList("entity_name", "entity_id", "entity_key");
    private final List<String> latestIdParamNames = Arrays.asList("latestMaxEntity_name", "latestMaxEntity_id", "latestMaxEntity_key");
    private final String tableName;

    public BucketPropertySetItemQueryProvider(DatabaseExporterHelper helper, ExportableEntityInfo entityInfo) {
        this.tableName = helper.checkNameDoesNotHaveSqlInjections(entityInfo.getTableName());
        this.initialSqlQuery = "SELECT * FROM " + this.tableName + " ORDER BY entity_name, entity_id, entity_key";
        this.repetitiveSqlQuery = String.format("SELECT * FROM %1$s WHERE (entity_name = :%2$s and entity_id = :%3$s and entity_key > :%4$s) OR (entity_name = :%2$s and entity_id > :%3$s) OR (entity_name > :%2$s) ORDER BY entity_name, entity_id, entity_key", this.tableName, this.latestIdParamNames.get(0), this.latestIdParamNames.get(1), this.latestIdParamNames.get(2));
    }

    @Override
    public String getInitialQuery() {
        return this.initialSqlQuery;
    }

    @Override
    public String getRepetitiveQuery() {
        return this.repetitiveSqlQuery;
    }

    @Override
    public List<String> getIdColumnNames() {
        return this.idColumnNames;
    }

    @Override
    public String getLatestIdParamName(int idIndex) {
        if (idIndex > this.idColumnNames.size()) {
            throw new IllegalArgumentException("Column with index " + idIndex + " does not exist in BucketPropertySetItem");
        }
        return this.latestIdParamNames.get(idIndex);
    }

    @Override
    public String getTableName() {
        return this.tableName;
    }
}

