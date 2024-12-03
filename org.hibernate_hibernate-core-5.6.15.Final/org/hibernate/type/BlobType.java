/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.sql.Blob;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.BlobTypeDescriptor;

public class BlobType
extends AbstractSingleColumnStandardBasicType<Blob> {
    public static final BlobType INSTANCE = new BlobType();

    public BlobType() {
        super(org.hibernate.type.descriptor.sql.BlobTypeDescriptor.DEFAULT, BlobTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "blob";
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }

    @Override
    protected Blob getReplacement(Blob original, Blob target, SharedSessionContractImplementor session) {
        return session.getJdbcServices().getJdbcEnvironment().getDialect().getLobMergeStrategy().mergeBlob(original, target, session);
    }
}

