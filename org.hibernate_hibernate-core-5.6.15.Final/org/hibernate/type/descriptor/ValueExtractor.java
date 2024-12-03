/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.type.descriptor.WrapperOptions;

public interface ValueExtractor<X> {
    public X extract(ResultSet var1, String var2, WrapperOptions var3) throws SQLException;

    public X extract(CallableStatement var1, int var2, WrapperOptions var3) throws SQLException;

    public X extract(CallableStatement var1, String[] var2, WrapperOptions var3) throws SQLException;
}

