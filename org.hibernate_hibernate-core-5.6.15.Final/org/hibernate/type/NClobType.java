/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.sql.NClob;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.NClobTypeDescriptor;

public class NClobType
extends AbstractSingleColumnStandardBasicType<NClob> {
    public static final NClobType INSTANCE = new NClobType();

    public NClobType() {
        super(org.hibernate.type.descriptor.sql.NClobTypeDescriptor.DEFAULT, NClobTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "nclob";
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }

    @Override
    protected NClob getReplacement(NClob original, NClob target, SharedSessionContractImplementor session) {
        return session.getJdbcServices().getJdbcEnvironment().getDialect().getLobMergeStrategy().mergeNClob(original, target, session);
    }
}

