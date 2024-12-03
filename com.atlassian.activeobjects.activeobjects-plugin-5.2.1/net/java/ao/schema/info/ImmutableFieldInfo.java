/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema.info;

import java.lang.reflect.Method;
import net.java.ao.ValueGenerator;
import net.java.ao.schema.info.FieldInfo;
import net.java.ao.types.TypeInfo;

class ImmutableFieldInfo<T>
implements FieldInfo {
    private final String fieldName;
    private final String polymorphicName;
    private final Method accessor;
    private final Method mutator;
    private final boolean primary;
    private final boolean nullable;
    private final boolean isTransient;
    private final boolean autoIncrement;
    private final boolean defaultValue;
    private final Class<T> fieldType;
    private final TypeInfo<T> typeInfo;
    private final Class<ValueGenerator<? extends T>> generatorType;

    ImmutableFieldInfo(String fieldName, String polymorphicName, Method accessor, Method mutator, Class<T> fieldType, TypeInfo<T> typeInfo, boolean primary, boolean nullable, boolean isTransient, boolean autoIncrement, boolean defaultValue, Class<ValueGenerator<? extends T>> generatorType) {
        this.fieldName = fieldName;
        this.polymorphicName = polymorphicName;
        this.accessor = accessor;
        this.mutator = mutator;
        this.primary = primary;
        this.nullable = nullable;
        this.isTransient = isTransient;
        this.autoIncrement = autoIncrement;
        this.defaultValue = defaultValue;
        this.fieldType = fieldType;
        this.typeInfo = typeInfo;
        this.generatorType = generatorType;
    }

    @Override
    public String getName() {
        return this.fieldName;
    }

    @Override
    public String getPolymorphicName() {
        return this.polymorphicName;
    }

    @Override
    public boolean isPrimary() {
        return this.primary;
    }

    @Override
    public boolean isNullable() {
        return this.nullable;
    }

    @Override
    public boolean isStorable() {
        return !this.isTransient() && this.getTypeInfo().getLogicalType().shouldStore(this.getJavaType());
    }

    @Override
    public boolean isCacheable() {
        return this.isStorable();
    }

    @Override
    public boolean isTransient() {
        return this.isTransient;
    }

    @Override
    public boolean hasAutoIncrement() {
        return this.autoIncrement;
    }

    @Override
    public boolean hasDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public TypeInfo<T> getTypeInfo() {
        return this.typeInfo;
    }

    @Override
    public Class<T> getJavaType() {
        return this.fieldType;
    }

    @Override
    public boolean hasAccessor() {
        return this.accessor != null;
    }

    @Override
    public Method getAccessor() {
        return this.accessor;
    }

    @Override
    public boolean hasMutator() {
        return this.mutator != null;
    }

    @Override
    public Method getMutator() {
        return this.mutator;
    }

    @Override
    public Class<? extends ValueGenerator<? extends T>> getGeneratorType() {
        return this.generatorType;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ImmutableFieldInfo)) {
            return false;
        }
        ImmutableFieldInfo that = (ImmutableFieldInfo)o;
        if (this.accessor != null ? !this.accessor.equals(that.accessor) : that.accessor != null) {
            return false;
        }
        if (!this.fieldName.equals(that.fieldName)) {
            return false;
        }
        return !(this.mutator != null ? !this.mutator.equals(that.mutator) : that.mutator != null);
    }

    public int hashCode() {
        int result = this.fieldName.hashCode();
        result = 31 * result + (this.accessor != null ? this.accessor.hashCode() : 0);
        result = 31 * result + (this.mutator != null ? this.mutator.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "ImmutableFieldInfo{fieldName='" + this.fieldName + '\'' + ", polymorphicName='" + this.polymorphicName + '\'' + ", accessor=" + this.accessor + ", mutator=" + this.mutator + ", primary=" + this.primary + ", nullable=" + this.nullable + ", autoIncrement=" + this.autoIncrement + ", defaultValue=" + this.defaultValue + ", fieldType=" + this.fieldType + ", typeInfo=" + this.typeInfo + ", generatorType=" + this.generatorType + '}';
    }
}

