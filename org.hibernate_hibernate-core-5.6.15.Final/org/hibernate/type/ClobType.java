/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.sql.Clob;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.ClobTypeDescriptor;

public class ClobType
extends AbstractSingleColumnStandardBasicType<Clob> {
    public static final ClobType INSTANCE = new ClobType();

    public ClobType() {
        super(org.hibernate.type.descriptor.sql.ClobTypeDescriptor.DEFAULT, ClobTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "clob";
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }

    @Override
    protected Clob getReplacement(Clob original, Clob target, SharedSessionContractImplementor session) {
        return session.getJdbcServices().getJdbcEnvironment().getDialect().getLobMergeStrategy().mergeClob(original, target, session);
    }
}

