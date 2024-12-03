/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cglib.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Type;
import org.springframework.cglib.core.AbstractClassGenerator;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.MethodInfo;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;

public class ImmutableBean {
    private static final Type ILLEGAL_STATE_EXCEPTION = TypeUtils.parseType("IllegalStateException");
    private static final Signature CSTRUCT_OBJECT = TypeUtils.parseConstructor("Object");
    private static final Class[] OBJECT_CLASSES = new Class[]{Object.class};
    private static final String FIELD_NAME = "CGLIB$RWBean";

    private ImmutableBean() {
    }

    public static Object create(Object bean2) {
        Generator gen = new Generator();
        gen.setBean(bean2);
        return gen.create();
    }

    public static class Generator
    extends AbstractClassGenerator {
        private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(ImmutableBean.class.getName());
        private Object bean;
        private Class target;

        public Generator() {
            super(SOURCE);
        }

        public void setBean(Object bean2) {
            this.bean = bean2;
            this.target = bean2.getClass();
            this.setContextClass(this.target);
        }

        @Override
        protected ClassLoader getDefaultClassLoader() {
            return this.target.getClassLoader();
        }

        @Override
        protected ProtectionDomain getProtectionDomain() {
            return ReflectUtils.getProtectionDomain(this.target);
        }

        public Object create() {
            String name = this.target.getName();
            this.setNamePrefix(name);
            return super.create(name);
        }

        @Override
        public void generateClass(ClassVisitor v) {
            Type targetType = Type.getType(this.target);
            ClassEmitter ce = new ClassEmitter(v);
            ce.begin_class(52, 1, this.getClassName(), targetType, null, "<generated>");
            ce.declare_field(18, ImmutableBean.FIELD_NAME, targetType, null);
            CodeEmitter e = ce.begin_method(1, CSTRUCT_OBJECT, null);
            e.load_this();
            e.super_invoke_constructor();
            e.load_this();
            e.load_arg(0);
            e.checkcast(targetType);
            e.putfield(ImmutableBean.FIELD_NAME);
            e.return_value();
            e.end_method();
            PropertyDescriptor[] descriptors = ReflectUtils.getBeanProperties(this.target);
            Method[] getters = ReflectUtils.getPropertyMethods(descriptors, true, false);
            Method[] setters = ReflectUtils.getPropertyMethods(descriptors, false, true);
            for (Method getter2 : getters) {
                MethodInfo getter = ReflectUtils.getMethodInfo(getter2);
                e = EmitUtils.begin_method(ce, getter, 1);
                e.load_this();
                e.getfield(ImmutableBean.FIELD_NAME);
                e.invoke(getter);
                e.return_value();
                e.end_method();
            }
            for (Method setter2 : setters) {
                MethodInfo setter = ReflectUtils.getMethodInfo(setter2);
                e = EmitUtils.begin_method(ce, setter, 1);
                e.throw_exception(ILLEGAL_STATE_EXCEPTION, "Bean is immutable");
                e.end_method();
            }
            ce.end_class();
        }

        @Override
        protected Object firstInstance(Class type) {
            return ReflectUtils.newInstance(type, OBJECT_CLASSES, new Object[]{this.bean});
        }

        @Override
        protected Object nextInstance(Object instance) {
            return this.firstInstance(instance.getClass());
        }
    }
}

