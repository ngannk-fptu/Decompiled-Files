/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface IntegralDataTypeHolder
extends Serializable {
    public IntegralDataTypeHolder initialize(long var1);

    public IntegralDataTypeHolder initialize(ResultSet var1, long var2) throws SQLException;

    public void bind(PreparedStatement var1, int var2) throws SQLException;

    public IntegralDataTypeHolder increment();

    public IntegralDataTypeHolder add(long var1);

    public IntegralDataTypeHolder decrement();

    public IntegralDataTypeHolder subtract(long var1);

    public IntegralDataTypeHolder multiplyBy(IntegralDataTypeHolder var1);

    public IntegralDataTypeHolder multiplyBy(long var1);

    public boolean eq(IntegralDataTypeHolder var1);

    public boolean eq(long var1);

    public boolean lt(IntegralDataTypeHolder var1);

    public boolean lt(long var1);

    public boolean gt(IntegralDataTypeHolder var1);

    public boolean gt(long var1);

    public IntegralDataTypeHolder copy();

    public Number makeValue();

    public Number makeValueThenIncrement();

    public Number makeValueThenAdd(long var1);
}

