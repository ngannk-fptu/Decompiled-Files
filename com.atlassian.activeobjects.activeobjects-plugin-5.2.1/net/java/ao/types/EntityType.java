/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.java.ao.Common;
import net.java.ao.EntityManager;
import net.java.ao.RawEntity;
import net.java.ao.types.AbstractLogicalType;
import net.java.ao.types.TypeInfo;

final class EntityType<K, T extends RawEntity<K>>
extends AbstractLogicalType<T> {
    private final TypeInfo<K> primaryKeyTypeInfo;
    private final Class<K> primaryKeyClass;

    public EntityType(Class<T> entityClass, TypeInfo<K> primaryKeyTypeInfo, Class<K> primaryKeyClass) {
        super("Entity(" + entityClass.getName() + ")", new Class[]{RawEntity.class}, 4, new Integer[0]);
        this.primaryKeyTypeInfo = primaryKeyTypeInfo;
        this.primaryKeyClass = primaryKeyClass;
    }

    @Override
    public int getDefaultJdbcWriteType() {
        return this.primaryKeyTypeInfo.getJdbcWriteType();
    }

    @Override
    public Object validate(Object value) {
        return value;
    }

    @Override
    public T pullFromDatabase(EntityManager manager, ResultSet res, Class<T> type, String columnName) throws SQLException {
        return Common.createPeer(manager, type, this.primaryKeyTypeInfo.getLogicalType().pullFromDatabase(manager, res, this.primaryKeyClass, columnName));
    }

    @Override
    public void putToDatabase(EntityManager manager, PreparedStatement stmt, int index, T value, int jdbcType) throws SQLException {
        this.primaryKeyTypeInfo.getLogicalType().putToDatabase(manager, stmt, index, Common.getPrimaryKeyValue(value), this.primaryKeyTypeInfo.getJdbcWriteType());
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof EntityType) {
            EntityType et = (EntityType)other;
            return et.primaryKeyTypeInfo.equals(this.primaryKeyTypeInfo) && et.primaryKeyClass.equals(this.primaryKeyClass);
        }
        return false;
    }
}

