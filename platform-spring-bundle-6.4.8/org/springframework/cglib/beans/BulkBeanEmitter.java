/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cglib.beans;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Type;
import org.springframework.cglib.beans.BulkBeanException;
import org.springframework.cglib.core.Block;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.Local;
import org.springframework.cglib.core.MethodInfo;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;

class BulkBeanEmitter
extends ClassEmitter {
    private static final Signature GET_PROPERTY_VALUES = TypeUtils.parseSignature("void getPropertyValues(Object, Object[])");
    private static final Signature SET_PROPERTY_VALUES = TypeUtils.parseSignature("void setPropertyValues(Object, Object[])");
    private static final Signature CSTRUCT_EXCEPTION = TypeUtils.parseConstructor("Throwable, int");
    private static final Type BULK_BEAN = TypeUtils.parseType("org.springframework.cglib.beans.BulkBean");
    private static final Type BULK_BEAN_EXCEPTION = TypeUtils.parseType("org.springframework.cglib.beans.BulkBeanException");

    public BulkBeanEmitter(ClassVisitor v, String className, Class target, String[] getterNames, String[] setterNames, Class[] types) {
        super(v);
        Method[] getters = new Method[getterNames.length];
        Method[] setters = new Method[setterNames.length];
        BulkBeanEmitter.validate(target, getterNames, setterNames, types, getters, setters);
        this.begin_class(52, 1, className, BULK_BEAN, null, "<generated>");
        EmitUtils.null_constructor(this);
        this.generateGet(target, getters);
        this.generateSet(target, setters);
        this.end_class();
    }

    private void generateGet(Class target, Method[] getters) {
        CodeEmitter e = this.begin_method(1, GET_PROPERTY_VALUES, null);
        if (getters.length > 0) {
            e.load_arg(0);
            e.checkcast(Type.getType(target));
            Local bean2 = e.make_local();
            e.store_local(bean2);
            for (int i2 = 0; i2 < getters.length; ++i2) {
                if (getters[i2] == null) continue;
                MethodInfo getter = ReflectUtils.getMethodInfo(getters[i2]);
                e.load_arg(1);
                e.push(i2);
                e.load_local(bean2);
                e.invoke(getter);
                e.box(getter.getSignature().getReturnType());
                e.aastore();
            }
        }
        e.return_value();
        e.end_method();
    }

    private void generateSet(Class target, Method[] setters) {
        CodeEmitter e = this.begin_method(1, SET_PROPERTY_VALUES, null);
        if (setters.length > 0) {
            Local index = e.make_local(Type.INT_TYPE);
            e.push(0);
            e.store_local(index);
            e.load_arg(0);
            e.checkcast(Type.getType(target));
            e.load_arg(1);
            Block handler = e.begin_block();
            int lastIndex = 0;
            for (int i2 = 0; i2 < setters.length; ++i2) {
                if (setters[i2] == null) continue;
                MethodInfo setter = ReflectUtils.getMethodInfo(setters[i2]);
                int diff = i2 - lastIndex;
                if (diff > 0) {
                    e.iinc(index, diff);
                    lastIndex = i2;
                }
                e.dup2();
                e.aaload(i2);
                e.unbox(setter.getSignature().getArgumentTypes()[0]);
                e.invoke(setter);
            }
            handler.end();
            e.return_value();
            e.catch_exception(handler, Constants.TYPE_THROWABLE);
            e.new_instance(BULK_BEAN_EXCEPTION);
            e.dup_x1();
            e.swap();
            e.load_local(index);
            e.invoke_constructor(BULK_BEAN_EXCEPTION, CSTRUCT_EXCEPTION);
            e.athrow();
        } else {
            e.return_value();
        }
        e.end_method();
    }

    private static void validate(Class target, String[] getters, String[] setters, Class[] types, Method[] getters_out, Method[] setters_out) {
        int i2 = -1;
        if (setters.length != types.length || getters.length != types.length) {
            throw new BulkBeanException("accessor array length must be equal type array length", i2);
        }
        try {
            for (i2 = 0; i2 < types.length; ++i2) {
                Method method;
                if (getters[i2] != null) {
                    method = ReflectUtils.findDeclaredMethod(target, getters[i2], null);
                    if (method.getReturnType() != types[i2]) {
                        throw new BulkBeanException("Specified type " + types[i2] + " does not match declared type " + method.getReturnType(), i2);
                    }
                    if (Modifier.isPrivate(method.getModifiers())) {
                        throw new BulkBeanException("Property is private", i2);
                    }
                    getters_out[i2] = method;
                }
                if (setters[i2] == null) continue;
                method = ReflectUtils.findDeclaredMethod(target, setters[i2], new Class[]{types[i2]});
                if (Modifier.isPrivate(method.getModifiers())) {
                    throw new BulkBeanException("Property is private", i2);
                }
                setters_out[i2] = method;
            }
        }
        catch (NoSuchMethodException e) {
            throw new BulkBeanException("Cannot find specified property", i2);
        }
    }
}

