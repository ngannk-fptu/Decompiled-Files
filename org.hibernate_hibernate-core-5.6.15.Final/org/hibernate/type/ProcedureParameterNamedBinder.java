/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.sql.CallableStatement;
import java.sql.SQLException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface ProcedureParameterNamedBinder {
    public boolean canDoSetting();

    public void nullSafeSet(CallableStatement var1, Object var2, String var3, SharedSessionContractImplementor var4) throws SQLException;
}

