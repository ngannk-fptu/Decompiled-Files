/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.blueprint.container;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.osgi.service.blueprint.container.ReifiedType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

class TypeFactory {
    private static final GenericsReifiedType OBJECT = new GenericsReifiedType(Object.class);

    TypeFactory() {
    }

    static ReifiedType getType(TypeDescriptor targetType) {
        return new GenericsReifiedType(targetType);
    }

    private static List<ReifiedType> getArguments(TypeDescriptor type) {
        if (type == null) {
            return Collections.emptyList();
        }
        if (type.isCollection() || type.isArray()) {
            ArrayList<ReifiedType> arguments = new ArrayList<ReifiedType>(1);
            Class elementType = type.getElementTypeDescriptor() == null ? null : type.getElementTypeDescriptor().getType();
            arguments.add(elementType != null ? new GenericsReifiedType(elementType) : OBJECT);
            return arguments;
        }
        if (type.isMap()) {
            ArrayList<ReifiedType> arguments = new ArrayList<ReifiedType>(2);
            Class keyType = type.getMapKeyTypeDescriptor() == null ? null : type.getMapKeyTypeDescriptor().getType();
            arguments.add(keyType != null ? new GenericsReifiedType(keyType) : OBJECT);
            Class valueType = type.getMapValueTypeDescriptor() == null ? null : type.getMapValueTypeDescriptor().getType();
            arguments.add(valueType != null ? new GenericsReifiedType(valueType) : OBJECT);
            return arguments;
        }
        TypeVariable<Class<T>>[] tvs = type.getType().getTypeParameters();
        ArrayList<ReifiedType> arguments = new ArrayList<ReifiedType>(tvs.length);
        for (TypeVariable tv : tvs) {
            ReifiedType rType = TypeFactory.getReifiedType(tv, new ArrayList<Type>());
            arguments.add(rType);
        }
        return arguments;
    }

    private static ReifiedType getReifiedType(Type targetType, Collection<Type> variableTypes) {
        if (targetType instanceof Class) {
            if (Object.class.equals((Object)targetType)) {
                return OBJECT;
            }
            return new GenericsReifiedType((Class)targetType);
        }
        if (targetType instanceof ParameterizedType) {
            Type ata = ((ParameterizedType)targetType).getActualTypeArguments()[0];
            return TypeFactory.getReifiedType(ata, variableTypes);
        }
        if (targetType instanceof WildcardType) {
            WildcardType wt = (WildcardType)targetType;
            Object[] lowerBounds = wt.getLowerBounds();
            if (ObjectUtils.isEmpty((Object[])lowerBounds)) {
                Type upperBound = wt.getUpperBounds()[0];
                return TypeFactory.getReifiedType(upperBound, variableTypes);
            }
            return TypeFactory.getReifiedType((Type)lowerBounds[0], variableTypes);
        }
        if (targetType instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable)targetType;
            if (variableTypes.contains(targetType)) {
                return OBJECT;
            }
            variableTypes.add(targetType);
            Type[] bounds = typeVariable.getBounds();
            return TypeFactory.getReifiedType(bounds[0], variableTypes);
        }
        if (targetType instanceof GenericArrayType) {
            return TypeFactory.getReifiedType(((GenericArrayType)targetType).getGenericComponentType(), variableTypes);
        }
        throw new IllegalArgumentException("Unknown type " + targetType.getClass());
    }

    private static class GenericsReifiedType
    extends ReifiedType {
        private final List<ReifiedType> arguments;
        private final int size;

        GenericsReifiedType(Class<?> clazz) {
            this(TypeDescriptor.valueOf(clazz));
        }

        GenericsReifiedType(TypeDescriptor descriptor) {
            super(descriptor == null ? Object.class : ClassUtils.resolvePrimitiveIfNecessary((Class)descriptor.getType()));
            this.arguments = TypeFactory.getArguments(descriptor);
            this.size = this.arguments.size();
        }

        @Override
        public ReifiedType getActualTypeArgument(int i) {
            if (i >= 0 && i < this.size) {
                return this.arguments.get(i);
            }
            if (i == 0) {
                return super.getActualTypeArgument(0);
            }
            throw new IllegalArgumentException("Invalid argument index given " + i);
        }

        @Override
        public int size() {
            return this.size;
        }
    }
}

