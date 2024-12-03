/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.sql.CallableStatement;
import java.sql.SQLException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface ProcedureParameterExtractionAware<T> {
    public boolean canDoExtraction();

    public T extract(CallableStatement var1, int var2, SharedSessionContractImplementor var3) throws SQLException;

    public T extract(CallableStatement var1, String[] var2, SharedSessionContractImplementor var3) throws SQLException;
}

