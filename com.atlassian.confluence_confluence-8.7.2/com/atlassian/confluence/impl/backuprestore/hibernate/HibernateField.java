/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.type.Type
 */
package com.atlassian.confluence.impl.backuprestore.hibernate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.hibernate.type.Type;

public class HibernateField {
    public static final boolean NULLABLE_COLUMN = true;
    public static final boolean NOT_NULLABLE_COLUMN = false;
    final List<Type> types;
    final List<String> propertyNames;
    final List<String> columnNames;
    final Class<?> referencedClass;
    final boolean nullable;

    @Deprecated
    public HibernateField(Type type, String propertyName, String[] columnNames, Class<?> referencedClass) {
        this(Collections.unmodifiableList(Arrays.asList(type)), Collections.unmodifiableList(Arrays.asList(propertyName)), columnNames, referencedClass);
    }

    public HibernateField(Type type, String propertyName, String[] columnNames, Class<?> referencedClass, boolean nullable) {
        this(Collections.singletonList(type), Collections.singletonList(propertyName), columnNames, referencedClass, nullable);
    }

    @Deprecated
    public HibernateField(List<Type> types, List<String> propertyNames, String[] columnNames, Class<?> referencedClass) {
        this.types = types;
        this.propertyNames = propertyNames;
        this.columnNames = Collections.unmodifiableList(Arrays.asList(columnNames));
        this.referencedClass = referencedClass;
        this.nullable = false;
    }

    public HibernateField(List<Type> types, List<String> propertyNames, String[] columnNames, Class<?> referencedClass, boolean nullable) {
        this.types = types;
        this.propertyNames = propertyNames;
        this.columnNames = Collections.unmodifiableList(Arrays.asList(columnNames));
        this.referencedClass = referencedClass;
        this.nullable = nullable;
    }

    public String getPropertyName() {
        if (this.propertyNames.size() != 1) {
            throw new IllegalStateException("Hibernate Field has more than one property name or the property name has not been initialised.");
        }
        return this.propertyNames.get(0);
    }

    public String getPropertyName(String columnName) {
        if (!this.columnNames.contains(columnName)) {
            throw new IllegalArgumentException("Property name for given column name does not exist");
        }
        return this.propertyNames.get(this.columnNames.indexOf(columnName));
    }

    public Type getIdPropertyType(String columnName) {
        return this.types.get(this.columnNames.indexOf(columnName));
    }

    public List<String> getColumnNames() {
        return this.columnNames;
    }

    public Class<?> getReferencedClass() {
        return this.referencedClass;
    }

    public String getSingleColumnName() {
        if (this.columnNames.size() != 1) {
            throw new IllegalArgumentException("Hibernate field " + this.types.get(0) + " should have only one column, but it has " + this.columnNames.size());
        }
        return this.columnNames.get(0);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        HibernateField that = (HibernateField)o;
        return Objects.equals(this.types, that.types) && Objects.equals(this.propertyNames, that.propertyNames) && Objects.equals(this.columnNames, that.columnNames) && Objects.equals(this.referencedClass, that.referencedClass);
    }

    public int hashCode() {
        return Objects.hash(this.types, this.propertyNames, this.columnNames, this.referencedClass);
    }

    public List<Type> getTypes() {
        return this.types;
    }

    public Type getType() {
        if (this.types.size() == 0) {
            throw new IllegalStateException("Hibernate field should have only one column, but it has " + this.columnNames.size());
        }
        if (this.types.size() != 1) {
            throw new IllegalStateException("Hibernate field " + this.types.get(0) + " should have only one column, but it has " + this.columnNames.size());
        }
        return this.types.get(0);
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public List<String> getPropertyNames() {
        return this.propertyNames;
    }

    public String toString() {
        return "HibernateField{types=" + this.types + ", propertyNames=" + this.propertyNames + ", columnNames=" + this.columnNames + ", referencedClass=" + this.referencedClass + "}";
    }
}

