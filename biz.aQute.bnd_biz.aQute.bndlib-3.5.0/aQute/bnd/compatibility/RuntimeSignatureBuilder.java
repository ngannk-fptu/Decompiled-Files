/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.compatibility;

import aQute.bnd.compatibility.Access;
import aQute.bnd.compatibility.GenericParameter;
import aQute.bnd.compatibility.GenericType;
import aQute.bnd.compatibility.Kind;
import aQute.bnd.compatibility.Scope;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public class RuntimeSignatureBuilder {
    final Scope root;

    public RuntimeSignatureBuilder(Scope root) {
        this.root = root;
    }

    public static String identity(Class<?> c) {
        return Scope.classIdentity(c.getName());
    }

    public static String identity(Method m) {
        return Scope.methodIdentity(m.getName(), RuntimeSignatureBuilder.getDescriptor(m.getReturnType(), m.getParameterTypes()));
    }

    public static String identity(Constructor<?> m) {
        return Scope.constructorIdentity(RuntimeSignatureBuilder.getDescriptor(Void.TYPE, m.getParameterTypes()));
    }

    public static String identity(Field m) {
        return Scope.fieldIdentity(m.getName(), RuntimeSignatureBuilder.getDescriptor(m.getType(), null));
    }

    public static String getDescriptor(Class<?> base, Class<?>[] parameters) {
        StringBuilder sb = new StringBuilder();
        if (parameters != null) {
            sb.append("(");
            for (Class<?> parameter : parameters) {
                sb.append(RuntimeSignatureBuilder.getDescriptor(parameter));
            }
            sb.append(")");
        }
        sb.append(RuntimeSignatureBuilder.getDescriptor(base));
        return sb.toString();
    }

    public Scope add(Class<?> c) {
        Scope local = this.add(this.root, this.getEnclosingScope(c), c.getModifiers(), c.getTypeParameters(), Kind.CLASS, RuntimeSignatureBuilder.identity(c), c.getGenericSuperclass(), c.getGenericInterfaces(), null);
        for (Field field : c.getDeclaredFields()) {
            this.add(local, local, field.getModifiers(), null, Kind.FIELD, RuntimeSignatureBuilder.identity(field), field.getGenericType(), null, null);
        }
        for (AccessibleObject accessibleObject : c.getConstructors()) {
            this.add(local, local, ((Constructor)accessibleObject).getModifiers(), ((Constructor)accessibleObject).getTypeParameters(), Kind.CONSTRUCTOR, RuntimeSignatureBuilder.identity(accessibleObject), Void.TYPE, ((Constructor)accessibleObject).getGenericParameterTypes(), ((Constructor)accessibleObject).getGenericExceptionTypes());
        }
        for (AccessibleObject accessibleObject : c.getDeclaredMethods()) {
            if (((Method)accessibleObject).getDeclaringClass() == Object.class) continue;
            this.add(local, local, ((Method)accessibleObject).getModifiers(), ((Method)accessibleObject).getTypeParameters(), Kind.METHOD, RuntimeSignatureBuilder.identity((Method)accessibleObject), ((Method)accessibleObject).getGenericReturnType(), ((Method)accessibleObject).getGenericParameterTypes(), ((Method)accessibleObject).getGenericExceptionTypes());
        }
        return local;
    }

    private Scope getEnclosingScope(Class<?> c) {
        Method m = c.getEnclosingMethod();
        if (m != null) {
            Scope s = this.getGlobalScope(m.getDeclaringClass());
            return s.getScope(RuntimeSignatureBuilder.identity(m));
        }
        Class<?> enclosingClass = c.getEnclosingClass();
        if (enclosingClass != null) {
            return this.getGlobalScope(enclosingClass);
        }
        return null;
    }

    private Scope getGlobalScope(Class<?> c) {
        if (c == null) {
            return null;
        }
        String id = RuntimeSignatureBuilder.identity(c);
        return this.root.getScope(id);
    }

    private Scope add(Scope declaring, Scope enclosing, int modifiers, TypeVariable<?>[] typeVariables, Kind kind, String id, Type mainType, Type[] parameterTypes, Type[] exceptionTypes) {
        Scope scope = declaring.getScope(id);
        assert (scope.access == Access.UNKNOWN);
        scope.setAccess(Access.modifier(modifiers));
        scope.setKind(kind);
        scope.setGenericParameter(this.convert(typeVariables));
        scope.setBase(this.convert(scope, mainType));
        scope.setParameterTypes(this.convert(parameterTypes));
        scope.setExceptionTypes(this.convert(exceptionTypes));
        scope.setDeclaring(declaring);
        scope.setEnclosing(enclosing);
        return scope;
    }

    private GenericType convert(Scope source, Type t) {
        if (t instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)t;
            this.root.getScope(RuntimeSignatureBuilder.identity((Class)pt.getRawType()));
            Type[] args = pt.getActualTypeArguments();
            GenericType[] arguments = new GenericType[args.length];
            int n = 0;
            for (Type arg : args) {
                arguments[n++] = this.convert(source, arg);
            }
        } else if (t instanceof TypeVariable || t instanceof WildcardType || t instanceof GenericArrayType) {
            // empty if block
        }
        if (!(t instanceof Class)) {
            throw new IllegalArgumentException(t.toString());
        }
        return null;
    }

    private GenericParameter[] convert(TypeVariable<?>[] vars) {
        if (vars == null) {
            return null;
        }
        GenericParameter[] out = new GenericParameter[vars.length];
        for (int i = 0; i < vars.length; ++i) {
            GenericType[] gss = this.convert(vars[i].getBounds());
            out[i] = new GenericParameter(vars[i].getName(), gss);
        }
        return out;
    }

    private GenericType[] convert(Type[] parameterTypes) {
        if (parameterTypes == null || parameterTypes.length == 0) {
            return GenericType.EMPTY;
        }
        GenericType[] tss = new GenericType[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; ++i) {
        }
        return tss;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static String getDescriptor(Class<?> c) {
        StringBuilder sb = new StringBuilder();
        if (c.isPrimitive()) {
            if (c == Boolean.TYPE) {
                sb.append("Z");
                return sb.toString();
            } else if (c == Byte.TYPE) {
                sb.append("Z");
                return sb.toString();
            } else if (c == Character.TYPE) {
                sb.append("C");
                return sb.toString();
            } else if (c == Short.TYPE) {
                sb.append("S");
                return sb.toString();
            } else if (c == Integer.TYPE) {
                sb.append("I");
                return sb.toString();
            } else if (c == Long.TYPE) {
                sb.append("J");
                return sb.toString();
            } else if (c == Float.TYPE) {
                sb.append("F");
                return sb.toString();
            } else if (c == Double.TYPE) {
                sb.append("D");
                return sb.toString();
            } else {
                if (c != Void.TYPE) throw new IllegalArgumentException("unknown primitive type: " + c);
                sb.append("V");
            }
            return sb.toString();
        } else if (c.isArray()) {
            sb.append("[");
            sb.append(RuntimeSignatureBuilder.getDescriptor(c));
            return sb.toString();
        } else {
            sb.append("L");
            sb.append(c.getName().replace('.', '/'));
            sb.append(";");
        }
        return sb.toString();
    }
}

