/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.GroovyRuntimeException;
import java.lang.reflect.Modifier;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ParameterTypes;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.MetaClassHelper;

public abstract class MetaMethod
extends ParameterTypes
implements Cloneable {
    private String signature;
    private String mopName;

    public MetaMethod() {
    }

    public MetaMethod(Class[] pt) {
        super(pt);
    }

    public abstract int getModifiers();

    public abstract String getName();

    public abstract Class getReturnType();

    public abstract CachedClass getDeclaringClass();

    public abstract Object invoke(Object var1, Object[] var2);

    public void checkParameters(Class[] arguments) {
        if (!this.isValidMethod(arguments)) {
            throw new IllegalArgumentException("Parameters to method: " + this.getName() + " do not match types: " + InvokerHelper.toString(this.getParameterTypes()) + " for arguments: " + InvokerHelper.toString(arguments));
        }
    }

    public boolean isMethod(MetaMethod method) {
        return this.getName().equals(method.getName()) && this.getModifiers() == method.getModifiers() && this.getReturnType().equals(method.getReturnType()) && MetaMethod.equal(this.getParameterTypes(), method.getParameterTypes());
    }

    protected static boolean equal(CachedClass[] a, Class[] b) {
        if (a.length == b.length) {
            int size = a.length;
            for (int i = 0; i < size; ++i) {
                if (a[i].getTheClass().equals(b[i])) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    protected static boolean equal(CachedClass[] a, CachedClass[] b) {
        if (a.length == b.length) {
            int size = a.length;
            for (int i = 0; i < size; ++i) {
                if (a[i] == b[i]) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    public String toString() {
        return super.toString() + "[name: " + this.getName() + " params: " + InvokerHelper.toString(this.getParameterTypes()) + " returns: " + this.getReturnType() + " owner: " + this.getDeclaringClass() + "]";
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new GroovyRuntimeException("This should never happen", e);
        }
    }

    public boolean isStatic() {
        return (this.getModifiers() & 8) != 0;
    }

    public boolean isAbstract() {
        return (this.getModifiers() & 0x400) != 0;
    }

    public final boolean isPrivate() {
        return (this.getModifiers() & 2) != 0;
    }

    public final boolean isProtected() {
        return (this.getModifiers() & 4) != 0;
    }

    public final boolean isPublic() {
        return (this.getModifiers() & 1) != 0;
    }

    public final boolean isSame(MetaMethod method) {
        return this.getName().equals(method.getName()) && MetaMethod.compatibleModifiers(this.getModifiers(), method.getModifiers()) && this.getReturnType().equals(method.getReturnType()) && MetaMethod.equal(this.getParameterTypes(), method.getParameterTypes());
    }

    private static boolean compatibleModifiers(int modifiersA, int modifiersB) {
        int mask = 15;
        return (modifiersA & mask) == (modifiersB & mask);
    }

    public boolean isCacheable() {
        return true;
    }

    public String getDescriptor() {
        return BytecodeHelper.getMethodDescriptor(this.getReturnType(), this.getNativeParameterTypes());
    }

    public synchronized String getSignature() {
        if (this.signature == null) {
            CachedClass[] parameters = this.getParameterTypes();
            String name = this.getName();
            StringBuilder buf = new StringBuilder(name.length() + parameters.length * 10);
            buf.append(this.getReturnType().getName());
            buf.append(' ');
            buf.append(name);
            buf.append('(');
            for (int i = 0; i < parameters.length; ++i) {
                if (i > 0) {
                    buf.append(", ");
                }
                buf.append(parameters[i].getName());
            }
            buf.append(')');
            this.signature = buf.toString();
        }
        return this.signature;
    }

    public String getMopName() {
        if (this.mopName == null) {
            String name = this.getName();
            CachedClass declaringClass = this.getDeclaringClass();
            this.mopName = Modifier.isPrivate(this.getModifiers()) ? "this$" + declaringClass.getSuperClassDistance() + "$" + name : "super$" + declaringClass.getSuperClassDistance() + "$" + name;
        }
        return this.mopName;
    }

    public final RuntimeException processDoMethodInvokeException(Exception e, Object object, Object[] argumentArray) {
        if (e instanceof RuntimeException) {
            return (RuntimeException)e;
        }
        return MetaClassHelper.createExceptionText("failed to invoke method: ", this, object, argumentArray, e, true);
    }

    public Object doMethodInvoke(Object object, Object[] argumentArray) {
        argumentArray = this.coerceArgumentsToClasses(argumentArray);
        try {
            return this.invoke(object, argumentArray);
        }
        catch (Exception e) {
            throw this.processDoMethodInvokeException(e, object, argumentArray);
        }
    }
}

