/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.SQLServer2012Dialect;

public class SQLServer2016Dialect
extends SQLServer2012Dialect {
    @Override
    public boolean supportsIfExistsBeforeTableName() {
        return true;
    }

    @Override
    public boolean supportsIfExistsBeforeConstraintName() {
        return true;
    }

    @Override
    public String getDropSequenceString(String sequenceName) {
        return "drop sequence if exists " + sequenceName;
    }

    @Override
    public String[] getDropSchemaCommand(String schemaName) {
        return new String[]{"drop schema if exists " + schemaName};
    }
}

