/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.primitives.Primitives
 */
package com.querydsl.core.types;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Primitives;
import com.querydsl.core.group.GroupExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionException;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.FactoryExpressionBase;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Visitor;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QBean<T>
extends FactoryExpressionBase<T> {
    private static final long serialVersionUID = -8210214512730989778L;
    private final ImmutableMap<String, Expression<?>> bindings;
    private final List<Field> fields;
    private final List<Method> setters;
    private final boolean fieldAccess;

    private static ImmutableMap<String, Expression<?>> createBindings(Expression<?> ... args) {
        ImmutableMap.Builder rv = ImmutableMap.builder();
        for (Expression<?> expr : args) {
            if (expr instanceof Path) {
                Path path = (Path)expr;
                rv.put((Object)path.getMetadata().getName(), expr);
                continue;
            }
            if (expr instanceof Operation) {
                Operation operation = (Operation)expr;
                if (operation.getOperator() == Ops.ALIAS && operation.getArg(1) instanceof Path) {
                    Path path = (Path)operation.getArg(1);
                    if (QBean.isCompoundExpression(operation.getArg(0))) {
                        rv.put((Object)path.getMetadata().getName(), operation.getArg(0));
                        continue;
                    }
                    rv.put((Object)path.getMetadata().getName(), (Object)operation);
                    continue;
                }
                throw new IllegalArgumentException("Unsupported expression " + expr);
            }
            throw new IllegalArgumentException("Unsupported expression " + expr);
        }
        return rv.build();
    }

    private static boolean isCompoundExpression(Expression<?> expr) {
        return expr instanceof FactoryExpression || expr instanceof GroupExpression;
    }

    private static Class<?> normalize(Class<?> cl) {
        return cl.isPrimitive() ? Primitives.wrap(cl) : cl;
    }

    private static boolean isAssignableFrom(Class<?> cl1, Class<?> cl2) {
        return QBean.normalize(cl1).isAssignableFrom(QBean.normalize(cl2));
    }

    protected QBean(Class<? extends T> type, Map<String, ? extends Expression<?>> bindings) {
        this(type, false, bindings);
    }

    protected QBean(Class<? extends T> type, Expression<?> ... args) {
        this(type, false, args);
    }

    protected QBean(Class<? extends T> type, boolean fieldAccess, Expression<?> ... args) {
        this(type, fieldAccess, (Map<String, Expression<?>>)QBean.createBindings(args));
    }

    protected QBean(Class<? extends T> type, boolean fieldAccess, Map<String, ? extends Expression<?>> bindings) {
        super(type);
        this.bindings = ImmutableMap.copyOf(bindings);
        this.fieldAccess = fieldAccess;
        if (fieldAccess) {
            this.fields = this.initFields(bindings);
            this.setters = ImmutableList.of();
        } else {
            this.fields = ImmutableList.of();
            this.setters = this.initMethods(bindings);
        }
    }

    private List<Field> initFields(Map<String, ? extends Expression<?>> args) {
        ArrayList<Field> fields = new ArrayList<Field>(args.size());
        for (Map.Entry<String, Expression<?>> entry : args.entrySet()) {
            String property = entry.getKey();
            Expression<?> expr = entry.getValue();
            Class<Object> beanType = this.getType();
            Field field = null;
            while (!beanType.equals(Object.class)) {
                try {
                    field = beanType.getDeclaredField(property);
                    field.setAccessible(true);
                    if (!QBean.isAssignableFrom(field.getType(), expr.getType())) {
                        this.typeMismatch(field.getType(), expr);
                    }
                    beanType = Object.class;
                }
                catch (SecurityException securityException) {
                }
                catch (NoSuchFieldException e) {
                    beanType = beanType.getSuperclass();
                }
            }
            if (field == null) {
                this.propertyNotFound(expr, property);
            }
            fields.add(field);
        }
        return fields;
    }

    private List<Method> initMethods(Map<String, ? extends Expression<?>> args) {
        try {
            ArrayList<Method> methods = new ArrayList<Method>(args.size());
            BeanInfo beanInfo = Introspector.getBeanInfo(this.getType());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (Map.Entry<String, Expression<?>> entry : args.entrySet()) {
                String property = entry.getKey();
                Expression<?> expr = entry.getValue();
                Method setter = null;
                for (PropertyDescriptor prop : propertyDescriptors) {
                    if (!prop.getName().equals(property)) continue;
                    setter = prop.getWriteMethod();
                    if (QBean.isAssignableFrom(prop.getPropertyType(), expr.getType())) break;
                    this.typeMismatch(prop.getPropertyType(), expr);
                    break;
                }
                if (setter == null) {
                    this.propertyNotFound(expr, property);
                }
                methods.add(setter);
            }
            return methods;
        }
        catch (IntrospectionException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected void propertyNotFound(Expression<?> expr, String property) {
    }

    protected void typeMismatch(Class<?> type, Expression<?> expr) {
        String msg = expr.getType().getName() + " is not compatible with " + type.getName();
        throw new IllegalArgumentException(msg);
    }

    @Override
    public T newInstance(Object ... a) {
        try {
            Object rv = this.create(this.getType());
            if (this.fieldAccess) {
                for (int i = 0; i < a.length; ++i) {
                    Field field;
                    Object value = a[i];
                    if (value == null || (field = this.fields.get(i)) == null) continue;
                    field.set(rv, value);
                }
            } else {
                for (int i = 0; i < a.length; ++i) {
                    Method setter;
                    Object value = a[i];
                    if (value == null || (setter = this.setters.get(i)) == null) continue;
                    setter.invoke(rv, value);
                }
            }
            return rv;
        }
        catch (InstantiationException e) {
            throw new ExpressionException(e.getMessage(), e);
        }
        catch (IllegalAccessException e) {
            throw new ExpressionException(e.getMessage(), e);
        }
        catch (InvocationTargetException e) {
            throw new ExpressionException(e.getMessage(), e);
        }
    }

    protected <T> T create(Class<T> type) throws IllegalAccessException, InstantiationException {
        return type.newInstance();
    }

    public Expression<T> as(Path<T> alias) {
        return ExpressionUtils.operation(this.getType(), (Operator)Ops.ALIAS, this, alias);
    }

    public Expression<T> as(String alias) {
        return this.as(ExpressionUtils.path(this.getType(), alias));
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof QBean) {
            QBean c = (QBean)obj;
            return this.getArgs().equals(c.getArgs()) && this.getType().equals(c.getType());
        }
        return false;
    }

    @Override
    public List<Expression<?>> getArgs() {
        return this.bindings.values().asList();
    }
}

