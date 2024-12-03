/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public interface ParameterBinder {
    public int bind(PreparedStatement var1, QueryParameters var2, SharedSessionContractImplementor var3, int var4) throws SQLException;
}

