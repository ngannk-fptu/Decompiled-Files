/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.querydsl.sql.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Nullable;

public interface Type<T> {
    public int[] getSQLTypes();

    public Class<T> getReturnedClass();

    public String getLiteral(T var1);

    @Nullable
    public T getValue(ResultSet var1, int var2) throws SQLException;

    public void setValue(PreparedStatement var1, int var2, T var3) throws SQLException;
}

