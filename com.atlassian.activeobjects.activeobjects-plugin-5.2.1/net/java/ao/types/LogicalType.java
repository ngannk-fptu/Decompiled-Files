/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package net.java.ao.types;

import com.google.common.collect.ImmutableSet;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import net.java.ao.EntityManager;

public interface LogicalType<T> {
    public String getName();

    @Deprecated
    public ImmutableSet<Class<?>> getTypes();

    public Set<Class<?>> getAllTypes();

    @Deprecated
    public ImmutableSet<Integer> getJdbcReadTypes();

    public Set<Integer> getAllJdbcReadTypes();

    public int getDefaultJdbcWriteType();

    public boolean isAllowedAsPrimaryKey();

    public T parse(String var1) throws IllegalArgumentException;

    public T parseDefault(String var1) throws IllegalArgumentException;

    public Object validate(Object var1) throws IllegalArgumentException;

    public void putToDatabase(EntityManager var1, PreparedStatement var2, int var3, T var4, int var5) throws SQLException;

    public T pullFromDatabase(EntityManager var1, ResultSet var2, Class<T> var3, String var4) throws SQLException;

    public T pullFromDatabase(EntityManager var1, ResultSet var2, Class<T> var3, int var4) throws SQLException;

    public boolean shouldCache(Class<?> var1);

    public boolean shouldStore(Class<?> var1);

    public boolean valueEquals(Object var1, Object var2);

    public String valueToString(T var1);
}

