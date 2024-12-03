/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.json;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Map;
import nonapi.io.github.classgraph.json.ClassFieldCache;
import nonapi.io.github.classgraph.json.JSONUtils;
import nonapi.io.github.classgraph.json.TypeResolutions;

class FieldTypeInfo {
    final Field field;
    private final Type fieldTypePartiallyResolved;
    private final boolean hasUnresolvedTypeVariables;
    private final boolean isTypeVariable;
    private final PrimitiveType primitiveType;
    private Constructor<?> constructorForFieldTypeWithSizeHint;
    private Constructor<?> defaultConstructorForFieldType;

    private static boolean hasTypeVariables(Type type) {
        if (type instanceof TypeVariable || type instanceof GenericArrayType) {
            return true;
        }
        if (type instanceof ParameterizedType) {
            for (Type arg : ((ParameterizedType)type).getActualTypeArguments()) {
                if (!FieldTypeInfo.hasTypeVariables(arg)) continue;
                return true;
            }
        }
        return false;
    }

    public FieldTypeInfo(Field field, Type fieldTypePartiallyResolved, ClassFieldCache classFieldCache) {
        boolean isArray;
        this.field = field;
        this.fieldTypePartiallyResolved = fieldTypePartiallyResolved;
        this.isTypeVariable = fieldTypePartiallyResolved instanceof TypeVariable;
        this.hasUnresolvedTypeVariables = this.isTypeVariable || FieldTypeInfo.hasTypeVariables(fieldTypePartiallyResolved);
        boolean bl = isArray = fieldTypePartiallyResolved instanceof GenericArrayType || fieldTypePartiallyResolved instanceof Class && ((Class)fieldTypePartiallyResolved).isArray();
        if (isArray || this.isTypeVariable) {
            this.primitiveType = PrimitiveType.NON_PRIMITIVE;
        } else {
            Class<?> fieldRawType = JSONUtils.getRawType(fieldTypePartiallyResolved);
            this.primitiveType = fieldRawType == Integer.TYPE ? PrimitiveType.INTEGER : (fieldRawType == Long.TYPE ? PrimitiveType.LONG : (fieldRawType == Short.TYPE ? PrimitiveType.SHORT : (fieldRawType == Double.TYPE ? PrimitiveType.DOUBLE : (fieldRawType == Float.TYPE ? PrimitiveType.FLOAT : (fieldRawType == Boolean.TYPE ? PrimitiveType.BOOLEAN : (fieldRawType == Byte.TYPE ? PrimitiveType.BYTE : (fieldRawType == Character.TYPE ? PrimitiveType.CHARACTER : (fieldRawType == Class.class ? PrimitiveType.CLASS_REF : PrimitiveType.NON_PRIMITIVE))))))));
            if (!JSONUtils.isBasicValueType(fieldRawType)) {
                if (Collection.class.isAssignableFrom(fieldRawType) || Map.class.isAssignableFrom(fieldRawType)) {
                    this.constructorForFieldTypeWithSizeHint = classFieldCache.getConstructorWithSizeHintForConcreteTypeOf(fieldRawType);
                }
                if (this.constructorForFieldTypeWithSizeHint == null) {
                    this.defaultConstructorForFieldType = classFieldCache.getDefaultConstructorForConcreteTypeOf(fieldRawType);
                }
            }
        }
    }

    public Constructor<?> getConstructorForFieldTypeWithSizeHint(Type fieldTypeFullyResolved, ClassFieldCache classFieldCache) {
        if (!this.isTypeVariable) {
            return this.constructorForFieldTypeWithSizeHint;
        }
        Class<?> fieldRawTypeFullyResolved = JSONUtils.getRawType(fieldTypeFullyResolved);
        if (!Collection.class.isAssignableFrom(fieldRawTypeFullyResolved) && !Map.class.isAssignableFrom(fieldRawTypeFullyResolved)) {
            return null;
        }
        return classFieldCache.getConstructorWithSizeHintForConcreteTypeOf(fieldRawTypeFullyResolved);
    }

    public Constructor<?> getDefaultConstructorForFieldType(Type fieldTypeFullyResolved, ClassFieldCache classFieldCache) {
        if (!this.isTypeVariable) {
            return this.defaultConstructorForFieldType;
        }
        Class<?> fieldRawTypeFullyResolved = JSONUtils.getRawType(fieldTypeFullyResolved);
        return classFieldCache.getDefaultConstructorForConcreteTypeOf(fieldRawTypeFullyResolved);
    }

    public Type getFullyResolvedFieldType(TypeResolutions typeResolutions) {
        if (!this.hasUnresolvedTypeVariables) {
            return this.fieldTypePartiallyResolved;
        }
        return typeResolutions.resolveTypeVariables(this.fieldTypePartiallyResolved);
    }

    void setFieldValue(Object containingObj, Object value) {
        try {
            if (value == null) {
                if (this.primitiveType != PrimitiveType.NON_PRIMITIVE) {
                    throw new IllegalArgumentException("Tried to set primitive-typed field " + this.field.getDeclaringClass().getName() + "." + this.field.getName() + " to null value");
                }
                this.field.set(containingObj, null);
                return;
            }
            switch (this.primitiveType) {
                case NON_PRIMITIVE: {
                    this.field.set(containingObj, value);
                    break;
                }
                case CLASS_REF: {
                    if (!(value instanceof Class)) {
                        throw new IllegalArgumentException("Expected value of type Class<?>; got " + value.getClass().getName());
                    }
                    this.field.set(containingObj, value);
                    break;
                }
                case INTEGER: {
                    if (!(value instanceof Integer)) {
                        throw new IllegalArgumentException("Expected value of type Integer; got " + value.getClass().getName());
                    }
                    this.field.setInt(containingObj, (Integer)value);
                    break;
                }
                case LONG: {
                    if (!(value instanceof Long)) {
                        throw new IllegalArgumentException("Expected value of type Long; got " + value.getClass().getName());
                    }
                    this.field.setLong(containingObj, (Long)value);
                    break;
                }
                case SHORT: {
                    if (!(value instanceof Short)) {
                        throw new IllegalArgumentException("Expected value of type Short; got " + value.getClass().getName());
                    }
                    this.field.setShort(containingObj, (Short)value);
                    break;
                }
                case DOUBLE: {
                    if (!(value instanceof Double)) {
                        throw new IllegalArgumentException("Expected value of type Double; got " + value.getClass().getName());
                    }
                    this.field.setDouble(containingObj, (Double)value);
                    break;
                }
                case FLOAT: {
                    if (!(value instanceof Float)) {
                        throw new IllegalArgumentException("Expected value of type Float; got " + value.getClass().getName());
                    }
                    this.field.setFloat(containingObj, ((Float)value).floatValue());
                    break;
                }
                case BOOLEAN: {
                    if (!(value instanceof Boolean)) {
                        throw new IllegalArgumentException("Expected value of type Boolean; got " + value.getClass().getName());
                    }
                    this.field.setBoolean(containingObj, (Boolean)value);
                    break;
                }
                case BYTE: {
                    if (!(value instanceof Byte)) {
                        throw new IllegalArgumentException("Expected value of type Byte; got " + value.getClass().getName());
                    }
                    this.field.setByte(containingObj, (Byte)value);
                    break;
                }
                case CHARACTER: {
                    if (!(value instanceof Character)) {
                        throw new IllegalArgumentException("Expected value of type Character; got " + value.getClass().getName());
                    }
                    this.field.setChar(containingObj, ((Character)value).charValue());
                    break;
                }
                default: {
                    throw new IllegalArgumentException();
                }
            }
        }
        catch (IllegalAccessException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not set field " + this.field.getDeclaringClass().getName() + "." + this.field.getName(), e);
        }
    }

    public String toString() {
        return this.fieldTypePartiallyResolved + " " + this.field.getDeclaringClass().getName() + "." + this.field.getDeclaringClass().getName();
    }

    private static enum PrimitiveType {
        NON_PRIMITIVE,
        INTEGER,
        LONG,
        SHORT,
        DOUBLE,
        FLOAT,
        BOOLEAN,
        BYTE,
        CHARACTER,
        CLASS_REF;

    }
}

