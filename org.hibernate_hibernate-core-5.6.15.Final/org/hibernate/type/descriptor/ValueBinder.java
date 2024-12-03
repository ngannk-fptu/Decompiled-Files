/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.hibernate.type.descriptor.WrapperOptions;

public interface ValueBinder<X> {
    public void bind(PreparedStatement var1, X var2, int var3, WrapperOptions var4) throws SQLException;

    public void bind(CallableStatement var1, X var2, String var3, WrapperOptions var4) throws SQLException;
}

