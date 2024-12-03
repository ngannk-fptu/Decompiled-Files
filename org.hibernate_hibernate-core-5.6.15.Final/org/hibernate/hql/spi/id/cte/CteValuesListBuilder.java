/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.cte;

import java.util.Collections;
import java.util.List;

public class CteValuesListBuilder {
    private final String tableName;
    private final String[] columns;
    private final List<Object[]> ids;
    private String cteStatement;

    public CteValuesListBuilder(String tableName, String[] columns, List<Object[]> ids) {
        this.tableName = tableName;
        this.columns = columns;
        this.ids = ids;
        this.cteStatement = this.buildStatement();
    }

    public List<Object[]> getIds() {
        return this.ids;
    }

    public String toStatement(String statement) {
        return this.cteStatement + statement;
    }

    private String buildStatement() {
        String columnNames = String.join((CharSequence)",", this.columns);
        String singleIdValuesParam = '(' + String.join((CharSequence)",", Collections.nCopies(this.columns.length, "?")) + ')';
        String parameters = String.join((CharSequence)",", Collections.nCopies(this.ids.size(), singleIdValuesParam));
        return "with " + this.tableName + " (" + columnNames + " ) as ( select " + columnNames + " from ( values  " + parameters + ") as HT " + "(" + columnNames + ") ) ";
    }
}

