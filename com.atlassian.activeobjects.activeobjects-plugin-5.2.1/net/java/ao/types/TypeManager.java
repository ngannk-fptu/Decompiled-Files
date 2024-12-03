/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.SetMultimap
 */
package net.java.ao.types;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import net.java.ao.Common;
import net.java.ao.RawEntity;
import net.java.ao.types.LogicalType;
import net.java.ao.types.LogicalTypes;
import net.java.ao.types.SchemaProperties;
import net.java.ao.types.TypeInfo;
import net.java.ao.types.TypeQualifiers;

public class TypeManager {
    private static final ImmutableSet<Integer> UNLIMITED_TEXT_TYPES = ImmutableSet.of((Object)2005, (Object)-16, (Object)-1);
    private final ImmutableMultimap<Class<?>, TypeInfo<?>> classIndex;
    private final ImmutableMultimap<Integer, TypeInfo<?>> jdbcTypeIndex;

    private TypeManager(Builder builder) {
        this.classIndex = ImmutableMultimap.copyOf((Multimap)builder.classIndex);
        this.jdbcTypeIndex = ImmutableMultimap.copyOf((Multimap)builder.jdbcTypeIndex);
    }

    public <T> TypeInfo<T> getType(Class<T> javaType) {
        return this.getType(javaType, TypeQualifiers.qualifiers());
    }

    public <T> TypeInfo<T> getType(Class<T> javaType, TypeQualifiers qualifiers) {
        if (RawEntity.class.isAssignableFrom(javaType)) {
            Class<T> entityType = javaType;
            Class primaryKeyClass = Common.getPrimaryKeyClassType(entityType);
            TypeInfo primaryKeyTypeInfo = this.getType(primaryKeyClass);
            LogicalType<T> logicalType = LogicalTypes.entityType(entityType, primaryKeyTypeInfo, primaryKeyClass);
            return new TypeInfo<T>(logicalType, primaryKeyTypeInfo.getSchemaProperties(), primaryKeyTypeInfo.getQualifiers());
        }
        for (Class<T> clazz = javaType; clazz != null; clazz = clazz.getSuperclass()) {
            if (!this.classIndex.containsKey(clazz)) continue;
            return this.findTypeWithQualifiers((Iterable<TypeInfo<?>>)this.classIndex.get(clazz), qualifiers);
        }
        throw new RuntimeException("Unrecognized type: " + javaType.getName());
    }

    public TypeInfo<?> getTypeFromSchema(int jdbcType, TypeQualifiers qualifiers) {
        if (this.jdbcTypeIndex.containsKey((Object)jdbcType)) {
            if (UNLIMITED_TEXT_TYPES.contains((Object)jdbcType)) {
                qualifiers = qualifiers.stringLength(-1);
            }
            return this.findTypeWithQualifiers((Iterable<TypeInfo<?>>)this.jdbcTypeIndex.get((Object)jdbcType), qualifiers);
        }
        return null;
    }

    private TypeInfo<?> findTypeWithQualifiers(Iterable<TypeInfo<?>> types, TypeQualifiers qualifiers) {
        TypeInfo<?> acceptableType = null;
        for (TypeInfo<?> type : types) {
            TypeQualifiers typeQualifiers = type.getQualifiers();
            if (typeQualifiers.equals(qualifiers)) {
                return type;
            }
            if (!typeQualifiers.isUnlimitedStringLengthSupportCompatible(qualifiers)) continue;
            acceptableType = type;
        }
        return acceptableType != null ? acceptableType.withQualifiers(qualifiers) : null;
    }

    public static TypeManager derby() {
        return new Builder().addMapping(LogicalTypes.blobType(), SchemaProperties.schemaType("BLOB")).addMapping(LogicalTypes.booleanType(), SchemaProperties.schemaType("SMALLINT").jdbcWriteType(-6).precisionAllowed(true), TypeQualifiers.qualifiers().precision(1)).addMapping(LogicalTypes.dateType(), SchemaProperties.schemaType("DATETIME")).addMapping(LogicalTypes.doubleType(), SchemaProperties.schemaType("DOUBLE")).addMapping(LogicalTypes.integerType(), SchemaProperties.schemaType("INTEGER")).addMapping(LogicalTypes.longType(), SchemaProperties.schemaType("BIGINT")).addStringTypes("VARCHAR", "CLOB", Integer.MAX_VALUE).build();
    }

    public static TypeManager hsql() {
        return new Builder().addMapping(LogicalTypes.blobType(), SchemaProperties.schemaType("LONGVARBINARY")).addMapping(LogicalTypes.booleanType(), SchemaProperties.schemaType("BOOLEAN")).addMapping(LogicalTypes.dateType(), SchemaProperties.schemaType("DATETIME")).addMapping(LogicalTypes.doubleType(), SchemaProperties.schemaType("DOUBLE")).addMapping(LogicalTypes.integerType(), SchemaProperties.schemaType("INTEGER")).addMapping(LogicalTypes.longType(), SchemaProperties.schemaType("BIGINT")).addStringTypes("VARCHAR", "LONGVARCHAR", 0x1000000).build();
    }

    public static TypeManager h2() {
        return new Builder().addMapping(LogicalTypes.blobType(), SchemaProperties.schemaType("BLOB")).addMapping(LogicalTypes.booleanType(), SchemaProperties.schemaType("BOOLEAN")).addMapping(LogicalTypes.dateType(), SchemaProperties.schemaType("TIMESTAMP")).addMapping(LogicalTypes.doubleType(), SchemaProperties.schemaType("DOUBLE")).addMapping(LogicalTypes.integerType(), SchemaProperties.schemaType("INT")).addMapping(LogicalTypes.longType(), SchemaProperties.schemaType("BIGINT")).addStringTypes("VARCHAR", "CLOB", Integer.MAX_VALUE).build();
    }

    public static TypeManager mysql() {
        Builder builder = new Builder().addMapping(LogicalTypes.blobType(), SchemaProperties.schemaType("BLOB")).addMapping(LogicalTypes.booleanType(), SchemaProperties.schemaType("BOOLEAN")).addMapping(LogicalTypes.dateType(), SchemaProperties.schemaType("DATETIME")).addMapping(LogicalTypes.doubleType(), SchemaProperties.schemaType("DOUBLE")).addMapping(LogicalTypes.integerType(), SchemaProperties.schemaType("INTEGER")).addMapping(LogicalTypes.longType(), SchemaProperties.schemaType("BIGINT")).addStringTypes("VARCHAR", "LONGTEXT", Integer.MAX_VALUE);
        return new MySQLTypeManager(builder);
    }

    public static TypeManager postgres() {
        return new Builder().addMapping(LogicalTypes.blobType(), SchemaProperties.schemaType("BYTEA")).addMapping(LogicalTypes.booleanType(), SchemaProperties.schemaType("BOOLEAN")).addMapping(LogicalTypes.dateType(), SchemaProperties.schemaType("TIMESTAMP")).addMapping(LogicalTypes.doubleType(), SchemaProperties.schemaType("DOUBLE PRECISION")).addMapping(LogicalTypes.integerType(), SchemaProperties.schemaType("INTEGER")).addMapping(LogicalTypes.longType(), SchemaProperties.schemaType("BIGINT")).addStringTypes("VARCHAR", "TEXT", 0x40000000).build();
    }

    public static TypeManager sqlServer() {
        int maxTextPrecision = 0x3FFFFFFF;
        Builder builder = new Builder().addMapping(LogicalTypes.blobType(), SchemaProperties.schemaType("IMAGE")).addMapping(LogicalTypes.booleanType(), SchemaProperties.schemaType("BIT")).addMapping(LogicalTypes.dateType(), SchemaProperties.schemaType("DATETIME")).addMapping(LogicalTypes.doubleType(), SchemaProperties.schemaType("FLOAT")).addMapping(LogicalTypes.integerType(), SchemaProperties.schemaType("INTEGER")).addMapping(LogicalTypes.longType(), SchemaProperties.schemaType("BIGINT")).addStringTypes("NVARCHAR", "NVARCHAR(max)", 0x3FFFFFFF);
        return new SqlServerTypeManager(builder, 0x3FFFFFFF);
    }

    public static TypeManager oracle() {
        return new Builder().addMapping(LogicalTypes.blobType(), SchemaProperties.schemaType("BLOB")).addMapping(LogicalTypes.booleanType(), SchemaProperties.schemaType("NUMBER").precisionAllowed(true), TypeQualifiers.qualifiers().precision(1)).addMapping(LogicalTypes.dateType(), SchemaProperties.schemaType("TIMESTAMP")).addMapping(LogicalTypes.doubleType(), SchemaProperties.schemaType("FLOAT").precisionAllowed(true), TypeQualifiers.qualifiers().precision(126)).addMapping(LogicalTypes.integerType(), SchemaProperties.schemaType("NUMBER").precisionAllowed(true), TypeQualifiers.qualifiers().precision(11)).addMapping(LogicalTypes.longType(), SchemaProperties.schemaType("NUMBER").precisionAllowed(true), TypeQualifiers.qualifiers().precision(20)).addStringTypes("VARCHAR", "CLOB", Integer.MAX_VALUE).build();
    }

    public static TypeManager nuodb() {
        return new Builder().addMapping(LogicalTypes.blobType(), SchemaProperties.schemaType("BLOB")).addMapping(LogicalTypes.booleanType(), SchemaProperties.schemaType("BOOLEAN")).addMapping(LogicalTypes.dateType(), SchemaProperties.schemaType("TIMESTAMP").scaleAllowed(true).precisionAllowed(false), TypeQualifiers.qualifiers().scale(6).precision(16)).addMapping(LogicalTypes.doubleType(), SchemaProperties.schemaType("DOUBLE")).addMapping(LogicalTypes.integerType(), SchemaProperties.schemaType("INTEGER").precisionAllowed(false), TypeQualifiers.qualifiers().precision(9)).addMapping(LogicalTypes.longType(), SchemaProperties.schemaType("BIGINT").precisionAllowed(false)).addStringTypes("VARCHAR", "TEXT", Integer.MAX_VALUE).build();
    }

    private static class SqlServerTypeManager
    extends TypeManager {
        private final int maxTextPrecision;

        private SqlServerTypeManager(Builder builder, int maxTextPrecision) {
            super(builder);
            this.maxTextPrecision = maxTextPrecision;
        }

        @Override
        public TypeInfo<?> getTypeFromSchema(int jdbcType, TypeQualifiers qualifiers) {
            if (jdbcType == -9 && qualifiers.hasPrecision() && qualifiers.getPrecision() == this.maxTextPrecision) {
                return super.getTypeFromSchema(-9, TypeQualifiers.qualifiers().stringLength(-1));
            }
            return super.getTypeFromSchema(jdbcType, qualifiers);
        }
    }

    private static class MySQLTypeManager
    extends TypeManager {
        private MySQLTypeManager(Builder builder) {
            super(builder);
        }

        @Override
        public TypeInfo<?> getTypeFromSchema(int jdbcType, TypeQualifiers qualifiers) {
            if (jdbcType == -6 && qualifiers.hasPrecision() && qualifiers.getPrecision() == 1) {
                return super.getTypeFromSchema(-7, TypeQualifiers.qualifiers());
            }
            return super.getTypeFromSchema(jdbcType, qualifiers);
        }
    }

    public static class Builder {
        private final SetMultimap<Class<?>, TypeInfo<?>> classIndex = HashMultimap.create();
        private final SetMultimap<Integer, TypeInfo<?>> jdbcTypeIndex = HashMultimap.create();

        public TypeManager build() {
            return new TypeManager(this);
        }

        public <T> Builder addMapping(LogicalType<T> logicalType, SchemaProperties schemaProperties) {
            return this.addMapping(logicalType, schemaProperties, TypeQualifiers.qualifiers());
        }

        public <T> Builder addMapping(LogicalType<T> logicalType, SchemaProperties schemaProperties, TypeQualifiers qualifiers) {
            TypeInfo<T> typeInfo = new TypeInfo<T>(logicalType, schemaProperties, qualifiers);
            for (Class<?> clazz : logicalType.getAllTypes()) {
                this.classIndex.put(clazz, typeInfo);
            }
            for (Integer jdbcType : logicalType.getAllJdbcReadTypes()) {
                this.jdbcTypeIndex.put((Object)jdbcType, typeInfo);
            }
            return this;
        }

        public Builder addStringTypes(String limitedStringSqlType, String unlimitedStringSqlType, int precision) {
            this.addMapping(LogicalTypes.stringType(), SchemaProperties.schemaType(limitedStringSqlType).stringLengthAllowed(true), TypeQualifiers.qualifiers().stringLength(255));
            this.addMapping(LogicalTypes.stringType(), SchemaProperties.schemaType(unlimitedStringSqlType).stringLengthAllowed(true).defaultValueAllowed(false), TypeQualifiers.qualifiers().stringLength(-1).precision(precision));
            this.addMapping(LogicalTypes.enumType(), SchemaProperties.schemaType(limitedStringSqlType).stringLengthAllowed(true), TypeQualifiers.qualifiers().stringLength(255));
            this.addMapping(LogicalTypes.uriType(), SchemaProperties.schemaType(limitedStringSqlType).stringLengthAllowed(true), TypeQualifiers.qualifiers().stringLength(450));
            this.addMapping(LogicalTypes.uriType(), SchemaProperties.schemaType(unlimitedStringSqlType).stringLengthAllowed(true).defaultValueAllowed(false), TypeQualifiers.qualifiers().stringLength(-1).precision(precision));
            this.addMapping(LogicalTypes.urlType(), SchemaProperties.schemaType(limitedStringSqlType).stringLengthAllowed(true), TypeQualifiers.qualifiers().stringLength(450));
            this.addMapping(LogicalTypes.urlType(), SchemaProperties.schemaType(unlimitedStringSqlType).stringLengthAllowed(true).defaultValueAllowed(false), TypeQualifiers.qualifiers().stringLength(-1).precision(precision));
            return this;
        }
    }
}

