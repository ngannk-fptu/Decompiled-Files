/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import net.sf.cglib.core.AbstractClassGenerator;
import net.sf.cglib.core.ClassEmitter;
import net.sf.cglib.core.CodeEmitter;
import net.sf.cglib.core.EmitUtils;
import net.sf.cglib.core.KeyFactory;
import net.sf.cglib.core.ReflectUtils;
import net.sf.cglib.core.TypeUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;

public abstract class ConstructorDelegate {
    private static final ConstructorKey KEY_FACTORY = (ConstructorKey)((Object)KeyFactory.create(class$net$sf$cglib$reflect$ConstructorDelegate$ConstructorKey == null ? (class$net$sf$cglib$reflect$ConstructorDelegate$ConstructorKey = ConstructorDelegate.class$("net.sf.cglib.reflect.ConstructorDelegate$ConstructorKey")) : class$net$sf$cglib$reflect$ConstructorDelegate$ConstructorKey, KeyFactory.CLASS_BY_NAME));
    static /* synthetic */ Class class$net$sf$cglib$reflect$ConstructorDelegate$ConstructorKey;
    static /* synthetic */ Class class$net$sf$cglib$reflect$ConstructorDelegate;

    protected ConstructorDelegate() {
    }

    public static ConstructorDelegate create(Class targetClass, Class iface) {
        Generator gen = new Generator();
        gen.setTargetClass(targetClass);
        gen.setInterface(iface);
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
        private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source((class$net$sf$cglib$reflect$ConstructorDelegate == null ? (class$net$sf$cglib$reflect$ConstructorDelegate = ConstructorDelegate.class$("net.sf.cglib.reflect.ConstructorDelegate")) : class$net$sf$cglib$reflect$ConstructorDelegate).getName());
        private static final Type CONSTRUCTOR_DELEGATE = TypeUtils.parseType("net.sf.cglib.reflect.ConstructorDelegate");
        private Class iface;
        private Class targetClass;

        public Generator() {
            super(SOURCE);
        }

        public void setInterface(Class iface) {
            this.iface = iface;
        }

        public void setTargetClass(Class targetClass) {
            this.targetClass = targetClass;
        }

        public ConstructorDelegate create() {
            this.setNamePrefix(this.targetClass.getName());
            Object key = KEY_FACTORY.newInstance(this.iface.getName(), this.targetClass.getName());
            return (ConstructorDelegate)super.create(key);
        }

        protected ClassLoader getDefaultClassLoader() {
            return this.targetClass.getClassLoader();
        }

        public void generateClass(ClassVisitor v) {
            Constructor constructor;
            this.setNamePrefix(this.targetClass.getName());
            Method newInstance = ReflectUtils.findNewInstance(this.iface);
            if (!newInstance.getReturnType().isAssignableFrom(this.targetClass)) {
                throw new IllegalArgumentException("incompatible return type");
            }
            try {
                constructor = this.targetClass.getDeclaredConstructor(newInstance.getParameterTypes());
            }
            catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("interface does not match any known constructor");
            }
            ClassEmitter ce = new ClassEmitter(v);
            ce.begin_class(46, 1, this.getClassName(), CONSTRUCTOR_DELEGATE, new Type[]{Type.getType(this.iface)}, "<generated>");
            Type declaring = Type.getType(constructor.getDeclaringClass());
            EmitUtils.null_constructor(ce);
            CodeEmitter e = ce.begin_method(1, ReflectUtils.getSignature(newInstance), ReflectUtils.getExceptionTypes(newInstance));
            e.new_instance(declaring);
            e.dup();
            e.load_args();
            e.invoke_constructor(declaring, ReflectUtils.getSignature(constructor));
            e.return_value();
            e.end_method();
            ce.end_class();
        }

        protected Object firstInstance(Class type) {
            return ReflectUtils.newInstance(type);
        }

        protected Object nextInstance(Object instance) {
            return instance;
        }
    }

    static interface ConstructorKey {
        public Object newInstance(String var1, String var2);
    }
}

