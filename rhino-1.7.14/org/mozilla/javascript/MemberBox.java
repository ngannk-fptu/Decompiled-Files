/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContinuationPending;
import org.mozilla.javascript.Delegator;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaMembers;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.VMBridge;

final class MemberBox
implements Serializable {
    private static final long serialVersionUID = 6358550398665688245L;
    private transient Member memberObject;
    transient Class<?>[] argTypes;
    transient boolean vararg;
    transient Function asGetterFunction;
    transient Function asSetterFunction;
    transient Object delegateTo;
    private static final Class<?>[] primitives = new Class[]{Boolean.TYPE, Byte.TYPE, Character.TYPE, Double.TYPE, Float.TYPE, Integer.TYPE, Long.TYPE, Short.TYPE, Void.TYPE};

    MemberBox(Method method) {
        this.init(method);
    }

    MemberBox(Constructor<?> constructor) {
        this.init(constructor);
    }

    private void init(Method method) {
        this.memberObject = method;
        this.argTypes = method.getParameterTypes();
        this.vararg = method.isVarArgs();
    }

    private void init(Constructor<?> constructor) {
        this.memberObject = constructor;
        this.argTypes = constructor.getParameterTypes();
        this.vararg = constructor.isVarArgs();
    }

    Method method() {
        return (Method)this.memberObject;
    }

    Constructor<?> ctor() {
        return (Constructor)this.memberObject;
    }

    Member member() {
        return this.memberObject;
    }

    boolean isMethod() {
        return this.memberObject instanceof Method;
    }

    boolean isCtor() {
        return this.memberObject instanceof Constructor;
    }

    boolean isStatic() {
        return Modifier.isStatic(this.memberObject.getModifiers());
    }

    boolean isPublic() {
        return Modifier.isPublic(this.memberObject.getModifiers());
    }

    String getName() {
        return this.memberObject.getName();
    }

    Class<?> getDeclaringClass() {
        return this.memberObject.getDeclaringClass();
    }

    String toJavaDeclaration() {
        StringBuilder sb = new StringBuilder();
        if (this.isMethod()) {
            Method method = this.method();
            sb.append(method.getReturnType());
            sb.append(' ');
            sb.append(method.getName());
        } else {
            Constructor<?> ctor = this.ctor();
            String name = ctor.getDeclaringClass().getName();
            int lastDot = name.lastIndexOf(46);
            if (lastDot >= 0) {
                name = name.substring(lastDot + 1);
            }
            sb.append(name);
        }
        sb.append(JavaMembers.liveConnectSignature(this.argTypes));
        return sb.toString();
    }

    public String toString() {
        return this.memberObject.toString();
    }

    Function asGetterFunction(final String name, Scriptable scope) {
        if (this.asGetterFunction == null) {
            this.asGetterFunction = new BaseFunction(scope, ScriptableObject.getFunctionPrototype(scope)){

                @Override
                public Object call(Context cx, Scriptable callScope, Scriptable thisObj, Object[] originalArgs) {
                    Object[] args;
                    Object getterThis;
                    MemberBox nativeGetter = MemberBox.this;
                    if (nativeGetter.delegateTo == null) {
                        getterThis = thisObj;
                        args = ScriptRuntime.emptyArgs;
                    } else {
                        getterThis = nativeGetter.delegateTo;
                        args = new Object[]{thisObj};
                    }
                    return nativeGetter.invoke(getterThis, args);
                }

                @Override
                public String getFunctionName() {
                    return name;
                }
            };
        }
        return this.asGetterFunction;
    }

    Function asSetterFunction(final String name, Scriptable scope) {
        if (this.asSetterFunction == null) {
            this.asSetterFunction = new BaseFunction(scope, ScriptableObject.getFunctionPrototype(scope)){

                @Override
                public Object call(Context cx, Scriptable callScope, Scriptable thisObj, Object[] originalArgs) {
                    Object[] args;
                    Object setterThis;
                    Object value;
                    MemberBox nativeSetter = MemberBox.this;
                    Object object = value = originalArgs.length > 0 ? originalArgs[0] : Undefined.instance;
                    if (nativeSetter.delegateTo == null) {
                        setterThis = thisObj;
                        args = new Object[]{value};
                    } else {
                        setterThis = nativeSetter.delegateTo;
                        args = new Object[]{thisObj, value};
                    }
                    return nativeSetter.invoke(setterThis, args);
                }

                @Override
                public String getFunctionName() {
                    return name;
                }
            };
        }
        return this.asSetterFunction;
    }

    Object invoke(Object target, Object[] args) {
        Method method = this.method();
        if (target instanceof Delegator) {
            target = ((Delegator)target).getDelegee();
        }
        for (int i = 0; i < args.length; ++i) {
            if (!(args[i] instanceof Delegator)) continue;
            args[i] = ((Delegator)args[i]).getDelegee();
        }
        try {
            try {
                return method.invoke(target, args);
            }
            catch (IllegalAccessException ex) {
                Method accessible = MemberBox.searchAccessibleMethod(method, this.argTypes);
                if (accessible != null) {
                    this.memberObject = accessible;
                    method = accessible;
                } else if (!VMBridge.instance.tryToMakeAccessible(method)) {
                    throw Context.throwAsScriptRuntimeEx(ex);
                }
                return method.invoke(target, args);
            }
        }
        catch (InvocationTargetException ite) {
            Throwable e = ite;
            while ((e = e.getTargetException()) instanceof InvocationTargetException) {
            }
            if (e instanceof ContinuationPending) {
                throw (ContinuationPending)e;
            }
            throw Context.throwAsScriptRuntimeEx(e);
        }
        catch (Exception ex) {
            throw Context.throwAsScriptRuntimeEx(ex);
        }
    }

    Object newInstance(Object[] args) {
        Constructor<?> ctor = this.ctor();
        try {
            try {
                return ctor.newInstance(args);
            }
            catch (IllegalAccessException ex) {
                if (!VMBridge.instance.tryToMakeAccessible(ctor)) {
                    throw Context.throwAsScriptRuntimeEx(ex);
                }
                return ctor.newInstance(args);
            }
        }
        catch (Exception ex) {
            throw Context.throwAsScriptRuntimeEx(ex);
        }
    }

    private static Method searchAccessibleMethod(Method method, Class<?>[] params) {
        Class<?> c;
        int modifiers = method.getModifiers();
        if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers) && !Modifier.isPublic((c = method.getDeclaringClass()).getModifiers())) {
            String name = method.getName();
            Class<?>[] intfs = c.getInterfaces();
            int N = intfs.length;
            for (int i = 0; i != N; ++i) {
                Class<?> intf = intfs[i];
                if (!Modifier.isPublic(intf.getModifiers())) continue;
                try {
                    return intf.getMethod(name, params);
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    continue;
                }
                catch (SecurityException securityException) {
                    // empty catch block
                }
            }
            while ((c = c.getSuperclass()) != null) {
                if (!Modifier.isPublic(c.getModifiers())) continue;
                try {
                    Method m = c.getMethod(name, params);
                    int mModifiers = m.getModifiers();
                    if (!Modifier.isPublic(mModifiers) || Modifier.isStatic(mModifiers)) continue;
                    return m;
                }
                catch (NoSuchMethodException noSuchMethodException) {
                }
                catch (SecurityException securityException) {
                }
            }
        }
        return null;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        Member member = MemberBox.readMember(in);
        if (member instanceof Method) {
            this.init((Method)member);
        } else {
            this.init((Constructor)member);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        MemberBox.writeMember(out, this.memberObject);
    }

    private static void writeMember(ObjectOutputStream out, Member member) throws IOException {
        if (member == null) {
            out.writeBoolean(false);
            return;
        }
        out.writeBoolean(true);
        if (!(member instanceof Method) && !(member instanceof Constructor)) {
            throw new IllegalArgumentException("not Method or Constructor");
        }
        out.writeBoolean(member instanceof Method);
        out.writeObject(member.getName());
        out.writeObject(member.getDeclaringClass());
        if (member instanceof Method) {
            MemberBox.writeParameters(out, ((Method)member).getParameterTypes());
        } else {
            MemberBox.writeParameters(out, ((Constructor)member).getParameterTypes());
        }
    }

    private static Member readMember(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if (!in.readBoolean()) {
            return null;
        }
        boolean isMethod = in.readBoolean();
        String name = (String)in.readObject();
        Class declaring = (Class)in.readObject();
        Class<?>[] parms = MemberBox.readParameters(in);
        try {
            if (isMethod) {
                return declaring.getMethod(name, parms);
            }
            return declaring.getConstructor(parms);
        }
        catch (NoSuchMethodException e) {
            throw new IOException("Cannot find member: " + e);
        }
    }

    private static void writeParameters(ObjectOutputStream out, Class<?>[] parms) throws IOException {
        out.writeShort(parms.length);
        block0: for (int i = 0; i < parms.length; ++i) {
            Class<?> parm = parms[i];
            boolean primitive = parm.isPrimitive();
            out.writeBoolean(primitive);
            if (!primitive) {
                out.writeObject(parm);
                continue;
            }
            for (int j = 0; j < primitives.length; ++j) {
                if (!parm.equals(primitives[j])) continue;
                out.writeByte(j);
                continue block0;
            }
            throw new IllegalArgumentException("Primitive " + parm + " not found");
        }
    }

    private static Class<?>[] readParameters(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Class[] result = new Class[in.readShort()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = !in.readBoolean() ? (Class)in.readObject() : primitives[in.readByte()];
        }
        return result;
    }
}

