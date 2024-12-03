/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters.site;

import com.atlassian.confluence.impl.backuprestore.backup.exporters.DatabaseExporterHelper;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.site.QueryProvider;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateMetadataHelper;
import java.util.List;

public class SingleColumnQueryProvider
implements QueryProvider {
    private final String initialSqlQuery;
    private final String repetitiveSqlQuery;
    private final List<String> idColumnNames;
    private final String latestIdParamName;
    private final String tableName;

    public SingleColumnQueryProvider(DatabaseExporterHelper helper, ExportableEntityInfo entityInfo) {
        String idColumnName = entityInfo.getId().getSingleColumnName();
        String idColumnNameWithoutQuotes = HibernateMetadataHelper.removeQuotes(idColumnName);
        this.idColumnNames = List.of(idColumnNameWithoutQuotes);
        this.tableName = helper.checkNameDoesNotHaveSqlInjections(entityInfo.getTableName());
        this.latestIdParamName = "latestMax" + this.capitaliseWord(idColumnNameWithoutQuotes);
        this.initialSqlQuery = "SELECT * FROM " + this.tableName + " ORDER BY " + idColumnName;
        this.repetitiveSqlQuery = "SELECT * FROM " + this.tableName + " WHERE " + idColumnName + " > :" + this.latestIdParamName + " ORDER BY " + idColumnName;
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
        return this.latestIdParamName;
    }

    @Override
    public String getTableName() {
        return this.tableName;
    }

    private String capitaliseWord(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}

