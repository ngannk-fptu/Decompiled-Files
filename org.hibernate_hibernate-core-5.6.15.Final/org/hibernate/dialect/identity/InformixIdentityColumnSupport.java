/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.identity;

import org.hibernate.MappingException;
import org.hibernate.dialect.identity.IdentityColumnSupportImpl;

public class InformixIdentityColumnSupport
extends IdentityColumnSupportImpl {
    @Override
    public boolean supportsIdentityColumns() {
        return true;
    }

    @Override
    public String getIdentitySelectString(String table, String column, int type) throws MappingException {
        return type == -5 ? "select dbinfo('serial8') from informix.systables where tabid=1" : "select dbinfo('sqlca.sqlerrd1') from informix.systables where tabid=1";
    }

    @Override
    public String getIdentityColumnString(int type) throws MappingException {
        return type == -5 ? "serial8 not null" : "serial not null";
    }

    @Override
    public boolean hasDataTypeInIdentityColumn() {
        return false;
    }
}

