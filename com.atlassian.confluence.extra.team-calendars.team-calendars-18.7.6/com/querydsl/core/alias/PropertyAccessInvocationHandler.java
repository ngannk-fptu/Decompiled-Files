/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.sf.cglib.proxy.MethodInterceptor
 *  net.sf.cglib.proxy.MethodProxy
 */
package com.querydsl.core.alias;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.alias.AliasFactory;
import com.querydsl.core.alias.MethodType;
import com.querydsl.core.alias.PathFactory;
import com.querydsl.core.alias.TypeSystem;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ParameterizedExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.util.BeanUtils;
import com.querydsl.core.util.ReflectionUtils;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

class PropertyAccessInvocationHandler
implements MethodInterceptor {
    private static final int RETURN_VALUE = 42;
    private final Expression<?> hostExpression;
    private final AliasFactory aliasFactory;
    private final Map<Object, Expression<?>> propToExpr = new HashMap();
    private final Map<Object, Object> propToObj = new HashMap<Object, Object>();
    private final PathFactory pathFactory;
    private final TypeSystem typeSystem;

    public PropertyAccessInvocationHandler(Expression<?> host, AliasFactory aliasFactory, PathFactory pathFactory, TypeSystem typeSystem) {
        this.hostExpression = host;
        this.aliasFactory = aliasFactory;
        this.pathFactory = pathFactory;
        this.typeSystem = typeSystem;
    }

    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object rv = null;
        MethodType methodType = MethodType.get(method);
        if (methodType == MethodType.GETTER) {
            String ptyName = this.propertyNameForGetter(method);
            Class<?> ptyClass = method.getReturnType();
            Type genericType = method.getGenericReturnType();
            if (this.propToObj.containsKey(ptyName)) {
                rv = this.propToObj.get(ptyName);
            } else {
                PathMetadata pm = this.createPropertyPath((Path)this.hostExpression, ptyName);
                rv = this.newInstance(ptyClass, genericType, proxy, ptyName, pm);
            }
            this.aliasFactory.setCurrent(this.propToExpr.get(ptyName));
        } else if (methodType == MethodType.SCALA_GETTER) {
            String ptyName = method.getName();
            Class<?> ptyClass = method.getReturnType();
            Type genericType = method.getGenericReturnType();
            if (this.propToObj.containsKey(ptyName)) {
                rv = this.propToObj.get(ptyName);
            } else {
                PathMetadata pm = this.createPropertyPath((Path)this.hostExpression, ptyName);
                rv = this.newInstance(ptyClass, genericType, proxy, ptyName, pm);
            }
            this.aliasFactory.setCurrent(this.propToExpr.get(ptyName));
        } else if (methodType == MethodType.LIST_ACCESS || methodType == MethodType.SCALA_LIST_ACCESS) {
            ImmutableList propKey = ImmutableList.of((Object)((Object)MethodType.LIST_ACCESS), (Object)args[0]);
            if (this.propToObj.containsKey(propKey)) {
                rv = this.propToObj.get(propKey);
            } else {
                PathMetadata pm = this.createListAccessPath((Path)this.hostExpression, (Integer)args[0]);
                Class<?> elementType = ((ParameterizedExpression)this.hostExpression).getParameter(0);
                rv = this.newInstance(elementType, elementType, proxy, propKey, pm);
            }
            this.aliasFactory.setCurrent(this.propToExpr.get(propKey));
        } else if (methodType == MethodType.MAP_ACCESS || methodType == MethodType.SCALA_MAP_ACCESS) {
            ImmutableList propKey = ImmutableList.of((Object)((Object)MethodType.MAP_ACCESS), (Object)args[0]);
            if (this.propToObj.containsKey(propKey)) {
                rv = this.propToObj.get(propKey);
            } else {
                PathMetadata pm = this.createMapAccessPath((Path)this.hostExpression, args[0]);
                Class<?> valueType = ((ParameterizedExpression)this.hostExpression).getParameter(1);
                rv = this.newInstance(valueType, valueType, proxy, propKey, pm);
            }
            this.aliasFactory.setCurrent(this.propToExpr.get(propKey));
        } else if (methodType == MethodType.TO_STRING) {
            rv = this.hostExpression.toString();
        } else if (methodType == MethodType.HASH_CODE) {
            rv = this.hostExpression.hashCode();
        } else if (methodType == MethodType.GET_MAPPED_PATH) {
            rv = this.hostExpression;
        } else {
            throw new IllegalArgumentException("Invocation of " + method.getName() + " with types " + Arrays.asList(method.getParameterTypes()) + " not supported");
        }
        return rv;
    }

    @Nullable
    protected <T> T newInstance(Class<T> type, Type genericType, Object parent, Object propKey, PathMetadata metadata) {
        Object rv;
        Path<Object> path;
        if (String.class.equals(type)) {
            path = this.pathFactory.createStringPath(metadata);
            rv = null;
        } else if (Integer.class.equals(type) || Integer.TYPE.equals(type)) {
            path = this.pathFactory.createNumberPath(Integer.class, metadata);
            rv = 42;
        } else if (Byte.class.equals(type) || Byte.TYPE.equals(type)) {
            path = this.pathFactory.createNumberPath(Byte.class, metadata);
            rv = (byte)42;
        } else if (java.util.Date.class.equals(type)) {
            path = this.pathFactory.createDateTimePath(type, metadata);
            rv = new java.util.Date();
        } else if (Timestamp.class.equals(type)) {
            path = this.pathFactory.createDateTimePath(type, metadata);
            rv = new Timestamp(System.currentTimeMillis());
        } else if (Date.class.equals(type)) {
            path = this.pathFactory.createDatePath(type, metadata);
            rv = new Date(System.currentTimeMillis());
        } else if (Time.class.equals(type)) {
            path = this.pathFactory.createTimePath(type, metadata);
            rv = new Time(System.currentTimeMillis());
        } else if (Long.class.equals(type) || Long.TYPE.equals(type)) {
            path = this.pathFactory.createNumberPath(Long.class, metadata);
            rv = 42L;
        } else if (Short.class.equals(type) || Short.TYPE.equals(type)) {
            path = this.pathFactory.createNumberPath(Short.class, metadata);
            rv = (short)42;
        } else if (Double.class.equals(type) || Double.TYPE.equals(type)) {
            path = this.pathFactory.createNumberPath(Double.class, metadata);
            rv = 42.0;
        } else if (Float.class.equals(type) || Float.TYPE.equals(type)) {
            path = this.pathFactory.createNumberPath(Float.class, metadata);
            rv = Float.valueOf(42.0f);
        } else if (BigInteger.class.equals(type)) {
            path = this.pathFactory.createNumberPath(type, metadata);
            rv = BigInteger.valueOf(42L);
        } else if (BigDecimal.class.equals(type)) {
            path = this.pathFactory.createNumberPath(type, metadata);
            rv = BigDecimal.valueOf(42L);
        } else if (Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {
            path = this.pathFactory.createBooleanPath(metadata);
            rv = Boolean.TRUE;
        } else if (this.typeSystem.isMapType(type)) {
            Class keyType = (Class)ReflectionUtils.getTypeParameter(genericType, 0);
            Class valueType = (Class)ReflectionUtils.getTypeParameter(genericType, 1);
            path = this.pathFactory.createMapPath(keyType, valueType, metadata);
            rv = this.aliasFactory.createAliasForProperty(type, path);
        } else if (this.typeSystem.isListType(type)) {
            Class elementType = (Class)ReflectionUtils.getTypeParameter(genericType, 0);
            path = this.pathFactory.createListPath(elementType, metadata);
            rv = this.aliasFactory.createAliasForProperty(type, path);
        } else if (this.typeSystem.isSetType(type)) {
            Class<?> elementType = ReflectionUtils.getTypeParameterAsClass(genericType, 0);
            path = this.pathFactory.createSetPath(elementType, metadata);
            rv = this.aliasFactory.createAliasForProperty(type, path);
        } else if (this.typeSystem.isCollectionType(type)) {
            Class<?> elementType = ReflectionUtils.getTypeParameterAsClass(genericType, 0);
            path = this.pathFactory.createCollectionPath(elementType, metadata);
            rv = this.aliasFactory.createAliasForProperty(type, path);
        } else if (Enum.class.isAssignableFrom(type)) {
            path = this.pathFactory.createEnumPath(type, metadata);
            rv = type.getEnumConstants()[0];
        } else if (type.isArray()) {
            path = this.pathFactory.createArrayPath(type, metadata);
            rv = Array.newInstance(type.getComponentType(), 5);
        } else {
            path = Number.class.isAssignableFrom(type) ? this.pathFactory.createNumberPath(type, metadata) : (Comparable.class.isAssignableFrom(type) ? this.pathFactory.createComparablePath(type, metadata) : this.pathFactory.createEntityPath(type, metadata));
            rv = !Modifier.isFinal(type.getModifiers()) ? this.aliasFactory.createAliasForProperty(type, path) : null;
        }
        this.propToObj.put(propKey, rv);
        this.propToExpr.put(propKey, path);
        return (T)rv;
    }

    protected String propertyNameForGetter(Method method) {
        String name = method.getName();
        name = name.startsWith("is") ? name.substring(2) : name.substring(3);
        return BeanUtils.uncapitalize(name);
    }

    protected PathMetadata createPropertyPath(Path<?> path, String propertyName) {
        return PathMetadataFactory.forProperty(path, propertyName);
    }

    protected PathMetadata createListAccessPath(Path<?> path, Integer index) {
        return PathMetadataFactory.forListAccess(path, index);
    }

    protected PathMetadata createMapAccessPath(Path<?> path, Object key) {
        return PathMetadataFactory.forMapAccess(path, key);
    }
}

