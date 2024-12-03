/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeConverter
 *  javax.persistence.Converter
 */
package org.hibernate.cfg;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.boot.AttributeConverterInfo;
import org.hibernate.boot.model.convert.internal.InstanceBasedConverterDescriptor;
import org.hibernate.boot.model.convert.spi.ConverterDescriptor;
import org.hibernate.boot.spi.MetadataBuildingContext;

@Deprecated
public class AttributeConverterDefinition
implements AttributeConverterInfo {
    private final AttributeConverter attributeConverter;
    private final boolean autoApply;
    private final Class entityAttributeType;
    private final Class databaseColumnType;

    public static AttributeConverterDefinition from(Class<? extends AttributeConverter> attributeConverterClass, boolean autoApply) {
        return new AttributeConverterDefinition(AttributeConverterDefinition.instantiateAttributeConverter(attributeConverterClass), autoApply);
    }

    private static AttributeConverter instantiateAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass) {
        try {
            Constructor<? extends AttributeConverter> constructor = attributeConverterClass.getDeclaredConstructor(new Class[0]);
            constructor.setAccessible(true);
            return constructor.newInstance(new Object[0]);
        }
        catch (Exception e) {
            throw new AnnotationException("Unable to instantiate AttributeConverter [" + attributeConverterClass.getName() + "]", e);
        }
    }

    public static AttributeConverterDefinition from(Class<? extends AttributeConverter> attributeConverterClass) {
        return AttributeConverterDefinition.from(AttributeConverterDefinition.instantiateAttributeConverter(attributeConverterClass));
    }

    public static AttributeConverterDefinition from(AttributeConverter attributeConverter) {
        boolean autoApply = false;
        Converter converterAnnotation = attributeConverter.getClass().getAnnotation(Converter.class);
        if (converterAnnotation != null) {
            autoApply = converterAnnotation.autoApply();
        }
        return new AttributeConverterDefinition(attributeConverter, autoApply);
    }

    public static AttributeConverterDefinition from(AttributeConverter attributeConverter, boolean autoApply) {
        return new AttributeConverterDefinition(attributeConverter, autoApply);
    }

    public AttributeConverterDefinition(AttributeConverter attributeConverter, boolean autoApply) {
        this.attributeConverter = attributeConverter;
        this.autoApply = autoApply;
        Class<?> attributeConverterClass = attributeConverter.getClass();
        ParameterizedType attributeConverterSignature = this.extractAttributeConverterParameterizedType(attributeConverterClass);
        if (attributeConverterSignature == null) {
            throw new AssertionFailure("Could not extract ParameterizedType representation of AttributeConverter definition from AttributeConverter implementation class [" + attributeConverterClass.getName() + "]");
        }
        if (attributeConverterSignature.getActualTypeArguments().length < 2) {
            throw new AnnotationException("AttributeConverter [" + attributeConverterClass.getName() + "] did not retain parameterized type information");
        }
        if (attributeConverterSignature.getActualTypeArguments().length > 2) {
            throw new AnnotationException("AttributeConverter [" + attributeConverterClass.getName() + "] specified more than 2 parameterized types");
        }
        this.entityAttributeType = AttributeConverterDefinition.extractClass(attributeConverterSignature.getActualTypeArguments()[0]);
        if (this.entityAttributeType == null) {
            throw new AnnotationException("Could not determine 'entity attribute' type from given AttributeConverter [" + attributeConverterClass.getName() + "]");
        }
        this.databaseColumnType = AttributeConverterDefinition.extractClass(attributeConverterSignature.getActualTypeArguments()[1]);
        if (this.databaseColumnType == null) {
            throw new AnnotationException("Could not determine 'database column' type from given AttributeConverter [" + attributeConverterClass.getName() + "]");
        }
    }

    private ParameterizedType extractAttributeConverterParameterizedType(Type base) {
        if (base != null) {
            Class clazz = AttributeConverterDefinition.extractClass(base);
            ArrayList<Type> types = new ArrayList<Type>();
            types.add(clazz.getGenericSuperclass());
            types.addAll(Arrays.asList(clazz.getGenericInterfaces()));
            for (Type type : types) {
                ParameterizedType parameterizedType;
                if (ParameterizedType.class.isInstance(type = AttributeConverterDefinition.resolveType(type, base)) && AttributeConverter.class.equals((Object)(parameterizedType = (ParameterizedType)type).getRawType())) {
                    return parameterizedType;
                }
                parameterizedType = this.extractAttributeConverterParameterizedType(type);
                if (parameterizedType == null) continue;
                return parameterizedType;
            }
        }
        return null;
    }

    private static Type resolveType(Type target, Type context) {
        if (target instanceof ParameterizedType) {
            return AttributeConverterDefinition.resolveParameterizedType((ParameterizedType)target, context);
        }
        if (target instanceof TypeVariable) {
            return AttributeConverterDefinition.resolveTypeVariable((TypeVariable)target, (ParameterizedType)context);
        }
        return target;
    }

    private static ParameterizedType resolveParameterizedType(final ParameterizedType parameterizedType, Type context) {
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        final Type[] resolvedTypeArguments = new Type[actualTypeArguments.length];
        for (int idx = 0; idx < actualTypeArguments.length; ++idx) {
            resolvedTypeArguments[idx] = AttributeConverterDefinition.resolveType(actualTypeArguments[idx], context);
        }
        return new ParameterizedType(){

            @Override
            public Type[] getActualTypeArguments() {
                return resolvedTypeArguments;
            }

            @Override
            public Type getRawType() {
                return parameterizedType.getRawType();
            }

            @Override
            public Type getOwnerType() {
                return parameterizedType.getOwnerType();
            }
        };
    }

    private static Type resolveTypeVariable(TypeVariable typeVariable, ParameterizedType context) {
        Class clazz = AttributeConverterDefinition.extractClass(context.getRawType());
        TypeVariable<Class<T>>[] typeParameters = clazz.getTypeParameters();
        for (int idx = 0; idx < typeParameters.length; ++idx) {
            if (!typeVariable.getName().equals(typeParameters[idx].getName())) continue;
            return AttributeConverterDefinition.resolveType(context.getActualTypeArguments()[idx], context);
        }
        return typeVariable;
    }

    public AttributeConverter getAttributeConverter() {
        return this.attributeConverter;
    }

    public boolean isAutoApply() {
        return this.autoApply;
    }

    public Class getEntityAttributeType() {
        return this.entityAttributeType;
    }

    public Class getDatabaseColumnType() {
        return this.databaseColumnType;
    }

    private static Class extractClass(Type type) {
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof ParameterizedType) {
            return AttributeConverterDefinition.extractClass(((ParameterizedType)type).getRawType());
        }
        return null;
    }

    @Override
    public Class<? extends AttributeConverter> getConverterClass() {
        return this.attributeConverter.getClass();
    }

    public String toString() {
        return String.format("%s[converterClass=%s, domainType=%s, jdbcType=%s]", this.getClass().getName(), this.attributeConverter.getClass().getName(), this.entityAttributeType.getName(), this.databaseColumnType.getName());
    }

    @Override
    public ConverterDescriptor toConverterDescriptor(MetadataBuildingContext context) {
        return new InstanceBasedConverterDescriptor(this.getAttributeConverter(), (Boolean)this.isAutoApply(), context.getBootstrapContext().getClassmateContext());
    }
}

