/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface LobMergeStrategy {
    public Blob mergeBlob(Blob var1, Blob var2, SharedSessionContractImplementor var3);

    public Clob mergeClob(Clob var1, Clob var2, SharedSessionContractImplementor var3);

    public NClob mergeNClob(NClob var1, NClob var2, SharedSessionContractImplementor var3);
}

