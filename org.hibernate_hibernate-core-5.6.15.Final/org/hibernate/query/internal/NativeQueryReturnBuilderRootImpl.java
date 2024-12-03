/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.internal;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.LockMode;
import org.hibernate.SQLQuery;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
import org.hibernate.query.internal.NativeQueryReturnBuilder;

public class NativeQueryReturnBuilderRootImpl
implements SQLQuery.RootReturn,
NativeQueryReturnBuilder {
    private final String alias;
    private final String entityName;
    private LockMode lockMode = LockMode.READ;
    private Map<String, String[]> propertyMappings;

    public NativeQueryReturnBuilderRootImpl(String alias, String entityName) {
        this.alias = alias;
        this.entityName = entityName;
    }

    @Override
    public SQLQuery.RootReturn setLockMode(LockMode lockMode) {
        this.lockMode = lockMode;
        return this;
    }

    @Override
    public SQLQuery.RootReturn setDiscriminatorAlias(String alias) {
        this.addProperty("class", alias);
        return this;
    }

    @Override
    public SQLQuery.RootReturn addProperty(String propertyName, String columnAlias) {
        this.addProperty(propertyName).addColumnAlias(columnAlias);
        return this;
    }

    @Override
    public SQLQuery.ReturnProperty addProperty(final String propertyName) {
        if (this.propertyMappings == null) {
            this.propertyMappings = new HashMap<String, String[]>();
        }
        return new SQLQuery.ReturnProperty(){

            @Override
            public SQLQuery.ReturnProperty addColumnAlias(String columnAlias) {
                String[] columnAliases = (String[])NativeQueryReturnBuilderRootImpl.this.propertyMappings.get(propertyName);
                if (columnAliases == null) {
                    columnAliases = new String[]{columnAlias};
                } else {
                    String[] newColumnAliases = new String[columnAliases.length + 1];
                    System.arraycopy(columnAliases, 0, newColumnAliases, 0, columnAliases.length);
                    newColumnAliases[columnAliases.length] = columnAlias;
                    columnAliases = newColumnAliases;
                }
                NativeQueryReturnBuilderRootImpl.this.propertyMappings.put(propertyName, columnAliases);
                return this;
            }
        };
    }

    @Override
    public NativeSQLQueryReturn buildReturn() {
        return new NativeSQLQueryRootReturn(this.alias, this.entityName, this.propertyMappings, this.lockMode);
    }
}

