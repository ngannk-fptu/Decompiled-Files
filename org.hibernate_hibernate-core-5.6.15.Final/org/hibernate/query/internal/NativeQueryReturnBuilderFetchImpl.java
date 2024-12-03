/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.internal;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.LockMode;
import org.hibernate.SQLQuery;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryJoinReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.query.internal.NativeQueryReturnBuilder;

public class NativeQueryReturnBuilderFetchImpl
implements SQLQuery.FetchReturn,
NativeQueryReturnBuilder {
    private final String alias;
    private String ownerTableAlias;
    private final String joinedPropertyName;
    private LockMode lockMode = LockMode.READ;
    private Map<String, String[]> propertyMappings;

    public NativeQueryReturnBuilderFetchImpl(String alias, String ownerTableAlias, String joinedPropertyName) {
        this.alias = alias;
        this.ownerTableAlias = ownerTableAlias;
        this.joinedPropertyName = joinedPropertyName;
    }

    @Override
    public SQLQuery.FetchReturn setLockMode(LockMode lockMode) {
        this.lockMode = lockMode;
        return this;
    }

    @Override
    public SQLQuery.FetchReturn addProperty(String propertyName, String columnAlias) {
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
                String[] columnAliases = (String[])NativeQueryReturnBuilderFetchImpl.this.propertyMappings.get(propertyName);
                if (columnAliases == null) {
                    columnAliases = new String[]{columnAlias};
                } else {
                    String[] newColumnAliases = new String[columnAliases.length + 1];
                    System.arraycopy(columnAliases, 0, newColumnAliases, 0, columnAliases.length);
                    newColumnAliases[columnAliases.length] = columnAlias;
                    columnAliases = newColumnAliases;
                }
                NativeQueryReturnBuilderFetchImpl.this.propertyMappings.put(propertyName, columnAliases);
                return this;
            }
        };
    }

    @Override
    public NativeSQLQueryReturn buildReturn() {
        return new NativeSQLQueryJoinReturn(this.alias, this.ownerTableAlias, this.joinedPropertyName, this.propertyMappings, this.lockMode);
    }
}

