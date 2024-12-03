/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.SybaseDialect;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.identity.SybaseAnywhereIdentityColumnSupport;
import org.hibernate.type.descriptor.sql.BitTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public class SybaseAnywhereDialect
extends SybaseDialect {
    public SybaseAnywhereDialect() {
        this.registerColumnType(16, "bit");
    }

    @Override
    public String getNoColumnsInsertString() {
        return "values (default)";
    }

    @Override
    public boolean dropConstraints() {
        return false;
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new SybaseAnywhereIdentityColumnSupport();
    }

    @Override
    protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
        return sqlCode == 16 ? BitTypeDescriptor.INSTANCE : super.getSqlTypeDescriptorOverride(sqlCode);
    }
}

