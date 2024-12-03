/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 */
package net.java.ao.schema.info;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import java.lang.reflect.Method;
import net.java.ao.ValueGenerator;
import net.java.ao.types.TypeInfo;

public interface FieldInfo<T> {
    public static final Predicate<FieldInfo> IS_REQUIRED = new Predicate<FieldInfo>(){

        public boolean apply(FieldInfo fieldInfo) {
            return !fieldInfo.isNullable() && !fieldInfo.hasDefaultValue() && !fieldInfo.hasAutoIncrement();
        }
    };
    public static final Predicate<FieldInfo> HAS_GENERATOR = new Predicate<FieldInfo>(){

        public boolean apply(FieldInfo fieldInfo) {
            return fieldInfo.getGeneratorType() != null;
        }
    };
    public static final Function<FieldInfo, String> PLUCK_NAME = new Function<FieldInfo, String>(){

        public String apply(FieldInfo fieldInfo) {
            return fieldInfo.getName();
        }
    };

    public String getName();

    public String getPolymorphicName();

    public boolean isPrimary();

    public boolean isNullable();

    public boolean isStorable();

    public boolean isCacheable();

    public boolean isTransient();

    public boolean hasAutoIncrement();

    public boolean hasDefaultValue();

    public TypeInfo<T> getTypeInfo();

    public Class<T> getJavaType();

    public boolean hasAccessor();

    public Method getAccessor();

    public boolean hasMutator();

    public Method getMutator();

    public Class<? extends ValueGenerator<? extends T>> getGeneratorType();
}

