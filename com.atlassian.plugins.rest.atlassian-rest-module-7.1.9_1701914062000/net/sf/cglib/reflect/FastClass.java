/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.sf.cglib.core.AbstractClassGenerator;
import net.sf.cglib.core.Constants;
import net.sf.cglib.core.ReflectUtils;
import net.sf.cglib.core.Signature;
import net.sf.cglib.reflect.FastClassEmitter;
import net.sf.cglib.reflect.FastConstructor;
import net.sf.cglib.reflect.FastMethod;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;

public abstract class FastClass {
    private Class type;
    static /* synthetic */ Class class$net$sf$cglib$reflect$FastClass;
    static /* synthetic */ Class class$java$lang$Class;

    protected FastClass() {
        throw new Error("Using the FastClass empty constructor--please report to the cglib-devel mailing list");
    }

    protected FastClass(Class type) {
        this.type = type;
    }

    public static FastClass create(Class type) {
        return FastClass.create(type.getClassLoader(), type);
    }

    public static FastClass create(ClassLoader loader, Class type) {
        Generator gen = new Generator();
        gen.setType(type);
        gen.setClassLoader(loader);
        return gen.create();
    }

    public Object invoke(String name, Class[] parameterTypes, Object obj, Object[] args) throws InvocationTargetException {
        return this.invoke(this.getIndex(name, parameterTypes), obj, args);
    }

    public Object newInstance() throws InvocationTargetException {
        return this.newInstance(this.getIndex(Constants.EMPTY_CLASS_ARRAY), null);
    }

    public Object newInstance(Class[] parameterTypes, Object[] args) throws InvocationTargetException {
        return this.newInstance(this.getIndex(parameterTypes), args);
    }

    public FastMethod getMethod(Method method) {
        return new FastMethod(this, method);
    }

    public FastConstructor getConstructor(Constructor constructor) {
        return new FastConstructor(this, constructor);
    }

    public FastMethod getMethod(String name, Class[] parameterTypes) {
        try {
            return this.getMethod(this.type.getMethod(name, parameterTypes));
        }
        catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }

    public FastConstructor getConstructor(Class[] parameterTypes) {
        try {
            return this.getConstructor(this.type.getConstructor(parameterTypes));
        }
        catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }

    public String getName() {
        return this.type.getName();
    }

    public Class getJavaClass() {
        return this.type;
    }

    public String toString() {
        return this.type.toString();
    }

    public int hashCode() {
        return this.type.hashCode();
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof FastClass)) {
            return false;
        }
        return this.type.equals(((FastClass)o).type);
    }

    public abstract int getIndex(String var1, Class[] var2);

    public abstract int getIndex(Class[] var1);

    public abstract Object invoke(int var1, Object var2, Object[] var3) throws InvocationTargetException;

    public abstract Object newInstance(int var1, Object[] var2) throws InvocationTargetException;

    public abstract int getIndex(Signature var1);

    public abstract int getMaxIndex();

    protected static String getSignatureWithoutReturnType(String name, Class[] parameterTypes) {
        StringBuffer sb = new StringBuffer();
        sb.append(name);
        sb.append('(');
        for (int i = 0; i < parameterTypes.length; ++i) {
            sb.append(Type.getDescriptor(parameterTypes[i]));
        }
        sb.append(')');
        return sb.toString();
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
        private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source((class$net$sf$cglib$reflect$FastClass == null ? (class$net$sf$cglib$reflect$FastClass = FastClass.class$("net.sf.cglib.reflect.FastClass")) : class$net$sf$cglib$reflect$FastClass).getName());
        private Class type;

        public Generator() {
            super(SOURCE);
        }

        public void setType(Class type) {
            this.type = type;
        }

        public FastClass create() {
            this.setNamePrefix(this.type.getName());
            return (FastClass)super.create(this.type.getName());
        }

        protected ClassLoader getDefaultClassLoader() {
            return this.type.getClassLoader();
        }

        public void generateClass(ClassVisitor v) throws Exception {
            new FastClassEmitter(v, this.getClassName(), this.type);
        }

        protected Object firstInstance(Class type) {
            return ReflectUtils.newInstance(type, new Class[]{class$java$lang$Class == null ? (class$java$lang$Class = FastClass.class$("java.lang.Class")) : class$java$lang$Class}, new Object[]{this.type});
        }

        protected Object nextInstance(Object instance) {
            return instance;
        }
    }
}

