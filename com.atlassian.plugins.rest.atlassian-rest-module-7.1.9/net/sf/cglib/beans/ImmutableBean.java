/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import net.sf.cglib.core.AbstractClassGenerator;
import net.sf.cglib.core.ClassEmitter;
import net.sf.cglib.core.CodeEmitter;
import net.sf.cglib.core.EmitUtils;
import net.sf.cglib.core.MethodInfo;
import net.sf.cglib.core.ReflectUtils;
import net.sf.cglib.core.Signature;
import net.sf.cglib.core.TypeUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;

public class ImmutableBean {
    private static final Type ILLEGAL_STATE_EXCEPTION = TypeUtils.parseType("IllegalStateException");
    private static final Signature CSTRUCT_OBJECT = TypeUtils.parseConstructor("Object");
    private static final Class[] OBJECT_CLASSES = new Class[]{class$java$lang$Object == null ? (class$java$lang$Object = ImmutableBean.class$("java.lang.Object")) : class$java$lang$Object};
    private static final String FIELD_NAME = "CGLIB$RWBean";
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$net$sf$cglib$beans$ImmutableBean;

    private ImmutableBean() {
    }

    public static Object create(Object bean) {
        Generator gen = new Generator();
        gen.setBean(bean);
        return gen.create();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    public static class Generator
    extends AbstractClassGenerator {
        private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source((class$net$sf$cglib$beans$ImmutableBean == null ? (class$net$sf$cglib$beans$ImmutableBean = ImmutableBean.class$("net.sf.cglib.beans.ImmutableBean")) : class$net$sf$cglib$beans$ImmutableBean).getName());
        private Object bean;
        private Class target;

        public Generator() {
            super(SOURCE);
        }

        public void setBean(Object bean) {
            this.bean = bean;
            this.target = bean.getClass();
        }

        protected ClassLoader getDefaultClassLoader() {
            return this.target.getClassLoader();
        }

        public Object create() {
            String name = this.target.getName();
            this.setNamePrefix(name);
            return super.create(name);
        }

        public void generateClass(ClassVisitor v) {
            int i;
            Type targetType = Type.getType(this.target);
            ClassEmitter ce = new ClassEmitter(v);
            ce.begin_class(46, 1, this.getClassName(), targetType, null, "<generated>");
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
            for (i = 0; i < getters.length; ++i) {
                MethodInfo getter = ReflectUtils.getMethodInfo(getters[i]);
                e = EmitUtils.begin_method(ce, getter, 1);
                e.load_this();
                e.getfield(ImmutableBean.FIELD_NAME);
                e.invoke(getter);
                e.return_value();
                e.end_method();
            }
            for (i = 0; i < setters.length; ++i) {
                MethodInfo setter = ReflectUtils.getMethodInfo(setters[i]);
                e = EmitUtils.begin_method(ce, setter, 1);
                e.throw_exception(ILLEGAL_STATE_EXCEPTION, "Bean is immutable");
                e.end_method();
            }
            ce.end_class();
        }

        protected Object firstInstance(Class type) {
            return ReflectUtils.newInstance(type, OBJECT_CLASSES, new Object[]{this.bean});
        }

        protected Object nextInstance(Object instance) {
            return this.firstInstance(instance.getClass());
        }
    }
}

