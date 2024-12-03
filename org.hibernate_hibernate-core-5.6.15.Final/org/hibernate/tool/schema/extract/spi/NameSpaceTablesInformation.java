/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.extract.spi;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.extract.spi.TableInformation;

public class NameSpaceTablesInformation {
    private final IdentifierHelper identifierHelper;
    private Map<String, TableInformation> tables = new HashMap<String, TableInformation>();

    public NameSpaceTablesInformation(IdentifierHelper identifierHelper) {
        this.identifierHelper = identifierHelper;
    }

    public void addTableInformation(TableInformation tableInformation) {
        this.tables.put(tableInformation.getName().getTableName().getText(), tableInformation);
    }

    public TableInformation getTableInformation(Table table) {
        return this.tables.get(this.identifierHelper.toMetaDataObjectName(table.getQualifiedTableName().getTableName()));
    }

    public TableInformation getTableInformation(String tableName) {
        return this.tables.get(tableName);
    }
}

