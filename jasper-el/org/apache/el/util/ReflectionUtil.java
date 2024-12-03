/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELException
 *  javax.el.MethodNotFoundException
 */
package org.apache.el.util;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.el.ELException;
import javax.el.MethodNotFoundException;
import org.apache.el.lang.ELSupport;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.util.JreCompat;
import org.apache.el.util.MessageFactory;

public class ReflectionUtil {
    protected static final String[] PRIMITIVE_NAMES = new String[]{"boolean", "byte", "char", "double", "float", "int", "long", "short", "void"};
    protected static final Class<?>[] PRIMITIVES = new Class[]{Boolean.TYPE, Byte.TYPE, Character.TYPE, Double.TYPE, Float.TYPE, Integer.TYPE, Long.TYPE, Short.TYPE, Void.TYPE};

    private ReflectionUtil() {
    }

    public static Class<?> forName(String name) throws ClassNotFoundException {
        if (null == name || name.isEmpty()) {
            return null;
        }
        Class<?> c = ReflectionUtil.forNamePrimitive(name);
        if (c == null) {
            if (name.endsWith("[]")) {
                String nc = name.substring(0, name.length() - 2);
                c = Class.forName(nc, true, ReflectionUtil.getContextClassLoader());
                c = Array.newInstance(c, 0).getClass();
            } else {
                c = Class.forName(name, true, ReflectionUtil.getContextClassLoader());
            }
        }
        return c;
    }

    protected static Class<?> forNamePrimitive(String name) {
        int p;
        if (name.length() <= 8 && (p = Arrays.binarySearch(PRIMITIVE_NAMES, name)) >= 0) {
            return PRIMITIVES[p];
        }
        return null;
    }

    public static Class<?>[] toTypeArray(String[] s) throws ClassNotFoundException {
        if (s == null) {
            return null;
        }
        Class[] c = new Class[s.length];
        for (int i = 0; i < s.length; ++i) {
            c[i] = ReflectionUtil.forName(s[i]);
        }
        return c;
    }

    public static String[] toTypeNameArray(Class<?>[] c) {
        if (c == null) {
            return null;
        }
        String[] s = new String[c.length];
        for (int i = 0; i < c.length; ++i) {
            s[i] = c[i].getName();
        }
        return s;
    }

    public static Method getMethod(EvaluationContext ctx, Object base, Object property, Class<?>[] paramTypes, Object[] paramValues) throws MethodNotFoundException {
        if (base == null || property == null) {
            throw new MethodNotFoundException(MessageFactory.get("error.method.notfound", base, property, ReflectionUtil.paramString(paramTypes)));
        }
        String methodName = property instanceof String ? (String)property : property.toString();
        int paramCount = paramTypes == null ? 0 : paramTypes.length;
        Method[] methods = base.getClass().getMethods();
        HashMap<Object, MatchResult> candidates = new HashMap<Object, MatchResult>();
        for (Method m : methods) {
            if (!m.getName().equals(methodName)) continue;
            Class<?>[] classArray = m.getParameterTypes();
            int mParamCount = classArray.length;
            if (!m.isVarArgs() && paramCount != mParamCount || m.isVarArgs() && paramCount < mParamCount - 1 || m.isVarArgs() && paramCount == mParamCount && paramValues != null && paramValues.length > paramCount && !paramTypes[mParamCount - 1].isArray() || m.isVarArgs() && paramCount > mParamCount && paramValues != null && paramValues.length != paramCount || !m.isVarArgs() && paramValues != null && paramCount != paramValues.length) continue;
            int exactMatch = 0;
            int assignableMatch = 0;
            int coercibleMatch = 0;
            int varArgsMatch = 0;
            boolean noMatch = false;
            block1: for (int i = 0; i < mParamCount; ++i) {
                if (m.isVarArgs() && i == mParamCount - 1) {
                    if (i == paramCount || paramValues != null && paramValues.length == i) {
                        varArgsMatch = Integer.MAX_VALUE;
                        break;
                    }
                    Class<?> varType = classArray[i].getComponentType();
                    for (int j = i; j < paramCount; ++j) {
                        if (ReflectionUtil.isAssignableFrom(paramTypes[j], varType)) {
                            ++assignableMatch;
                            ++varArgsMatch;
                            continue;
                        }
                        if (paramValues == null) {
                            noMatch = true;
                            continue block1;
                        }
                        if (ReflectionUtil.isCoercibleFrom(ctx, paramValues[j], varType)) {
                            ++coercibleMatch;
                            ++varArgsMatch;
                            continue;
                        }
                        noMatch = true;
                        continue block1;
                    }
                    continue;
                }
                if (classArray[i].equals(paramTypes[i])) {
                    ++exactMatch;
                    continue;
                }
                if (paramTypes[i] != null && ReflectionUtil.isAssignableFrom(paramTypes[i], classArray[i])) {
                    ++assignableMatch;
                    continue;
                }
                if (paramValues == null) {
                    noMatch = true;
                    break;
                }
                if (ReflectionUtil.isCoercibleFrom(ctx, paramValues[i], classArray[i])) {
                    ++coercibleMatch;
                    continue;
                }
                noMatch = true;
                break;
            }
            if (noMatch) continue;
            if (exactMatch == paramCount && varArgsMatch == 0) {
                Method result = ReflectionUtil.getMethod(base.getClass(), base, m);
                if (result == null) {
                    throw new MethodNotFoundException(MessageFactory.get("error.method.notfound", base, property, ReflectionUtil.paramString(paramTypes)));
                }
                return result;
            }
            candidates.put(m, new MatchResult(m.isVarArgs(), exactMatch, assignableMatch, coercibleMatch, varArgsMatch, m.isBridge()));
        }
        MatchResult bestMatch = new MatchResult(true, 0, 0, 0, 0, true);
        Method match = null;
        boolean multiple = false;
        for (Map.Entry entry : candidates.entrySet()) {
            int cmp = ((MatchResult)entry.getValue()).compareTo(bestMatch);
            if (cmp > 0 || match == null) {
                bestMatch = (MatchResult)entry.getValue();
                match = (Method)entry.getKey();
                multiple = false;
                continue;
            }
            if (cmp != 0) continue;
            multiple = true;
        }
        if (multiple && (match = bestMatch.getExactCount() == paramCount - 1 ? ReflectionUtil.resolveAmbiguousMethod(candidates.keySet(), paramTypes) : null) == null) {
            throw new MethodNotFoundException(MessageFactory.get("error.method.ambiguous", base, property, ReflectionUtil.paramString(paramTypes)));
        }
        if (match == null) {
            throw new MethodNotFoundException(MessageFactory.get("error.method.notfound", base, property, ReflectionUtil.paramString(paramTypes)));
        }
        Method result = ReflectionUtil.getMethod(base.getClass(), base, match);
        if (result == null) {
            throw new MethodNotFoundException(MessageFactory.get("error.method.notfound", base, property, ReflectionUtil.paramString(paramTypes)));
        }
        return result;
    }

    /*
     * WARNING - void declaration
     */
    private static Method resolveAmbiguousMethod(Set<Method> candidates, Class<?>[] paramTypes) {
        void var6_14;
        Method m = candidates.iterator().next();
        int nonMatchIndex = 0;
        Class<?> nonMatchClass = null;
        for (int i = 0; i < paramTypes.length; ++i) {
            if (m.getParameterTypes()[i] == paramTypes[i]) continue;
            nonMatchIndex = i;
            nonMatchClass = paramTypes[i];
            break;
        }
        if (nonMatchClass == null) {
            return null;
        }
        for (Method method : candidates) {
            if (method.getParameterTypes()[nonMatchIndex] != paramTypes[nonMatchIndex]) continue;
            return null;
        }
        for (Class superClass = nonMatchClass.getSuperclass(); superClass != null; superClass = superClass.getSuperclass()) {
            for (Method c : candidates) {
                if (!c.getParameterTypes()[nonMatchIndex].equals(superClass)) continue;
                return c;
            }
        }
        Object var6_10 = null;
        if (Number.class.isAssignableFrom(nonMatchClass)) {
            for (Method c : candidates) {
                void var6_11;
                Class<?> candidateType = c.getParameterTypes()[nonMatchIndex];
                if (!Number.class.isAssignableFrom(candidateType) && !candidateType.isPrimitive()) continue;
                if (var6_11 == null) {
                    Method method = c;
                    continue;
                }
                Object var6_13 = null;
                break;
            }
        }
        return var6_14;
    }

    private static boolean isAssignableFrom(Class<?> src, Class<?> target) {
        if (src == null) {
            return true;
        }
        Class<Object> targetClass = target.isPrimitive() ? (target == Boolean.TYPE ? Boolean.class : (target == Character.TYPE ? Character.class : (target == Byte.TYPE ? Byte.class : (target == Short.TYPE ? Short.class : (target == Integer.TYPE ? Integer.class : (target == Long.TYPE ? Long.class : (target == Float.TYPE ? Float.class : Double.class))))))) : target;
        return targetClass.isAssignableFrom(src);
    }

    private static boolean isCoercibleFrom(EvaluationContext ctx, Object src, Class<?> target) {
        try {
            ELSupport.coerceToType(ctx, src, target);
        }
        catch (ELException e) {
            return false;
        }
        return true;
    }

    private static Method getMethod(Class<?> type, Object base, Method m) {
        JreCompat jreCompat = JreCompat.getInstance();
        if (m == null || Modifier.isPublic(type.getModifiers()) && (Modifier.isStatic(m.getModifiers()) && jreCompat.canAccess(null, m) || jreCompat.canAccess(base, m))) {
            return m;
        }
        Class<?>[] interfaces = type.getInterfaces();
        Method mp = null;
        for (Class<?> iface : interfaces) {
            try {
                mp = iface.getMethod(m.getName(), m.getParameterTypes());
                mp = ReflectionUtil.getMethod(mp.getDeclaringClass(), base, mp);
                if (mp == null) continue;
                return mp;
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
        }
        Class<?> sup = type.getSuperclass();
        if (sup != null) {
            try {
                mp = sup.getMethod(m.getName(), m.getParameterTypes());
                mp = ReflectionUtil.getMethod(mp.getDeclaringClass(), base, mp);
                if (mp != null) {
                    return mp;
                }
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
        }
        return null;
    }

    private static String paramString(Class<?>[] types) {
        if (types != null) {
            StringBuilder sb = new StringBuilder();
            for (Class<?> type : types) {
                if (type == null) {
                    sb.append("null, ");
                    continue;
                }
                sb.append(type.getName()).append(", ");
            }
            if (sb.length() > 2) {
                sb.setLength(sb.length() - 2);
            }
            return sb.toString();
        }
        return null;
    }

    private static ClassLoader getContextClassLoader() {
        ClassLoader tccl;
        if (System.getSecurityManager() != null) {
            PrivilegedGetTccl pa = new PrivilegedGetTccl();
            tccl = AccessController.doPrivileged(pa);
        } else {
            tccl = Thread.currentThread().getContextClassLoader();
        }
        return tccl;
    }

    private static class MatchResult
    implements Comparable<MatchResult> {
        private final boolean varArgs;
        private final int exactCount;
        private final int assignableCount;
        private final int coercibleCount;
        private final int varArgsCount;
        private final boolean bridge;

        MatchResult(boolean varArgs, int exactCount, int assignableCount, int coercibleCount, int varArgsCount, boolean bridge) {
            this.varArgs = varArgs;
            this.exactCount = exactCount;
            this.assignableCount = assignableCount;
            this.coercibleCount = coercibleCount;
            this.varArgsCount = varArgsCount;
            this.bridge = bridge;
        }

        public boolean isVarArgs() {
            return this.varArgs;
        }

        public int getExactCount() {
            return this.exactCount;
        }

        public int getAssignableCount() {
            return this.assignableCount;
        }

        public int getCoercible() {
            return this.coercibleCount;
        }

        public int getVarArgsCount() {
            return this.varArgsCount;
        }

        public boolean isBridge() {
            return this.bridge;
        }

        @Override
        public int compareTo(MatchResult o) {
            int cmp = Boolean.compare(o.isVarArgs(), this.isVarArgs());
            if (cmp == 0 && (cmp = Integer.compare(this.getExactCount(), o.getExactCount())) == 0 && (cmp = Integer.compare(this.getAssignableCount(), o.getAssignableCount())) == 0 && (cmp = Integer.compare(this.getCoercible(), o.getCoercible())) == 0 && (cmp = Integer.compare(o.getVarArgsCount(), this.getVarArgsCount())) == 0) {
                cmp = Boolean.compare(o.isBridge(), this.isBridge());
            }
            return cmp;
        }

        public boolean equals(Object o) {
            return o == this || null != o && this.getClass().equals(o.getClass()) && ((MatchResult)o).getExactCount() == this.getExactCount() && ((MatchResult)o).getAssignableCount() == this.getAssignableCount() && ((MatchResult)o).getCoercible() == this.getCoercible() && ((MatchResult)o).getVarArgsCount() == this.getVarArgsCount() && ((MatchResult)o).isVarArgs() == this.isVarArgs() && ((MatchResult)o).isBridge() == this.isBridge();
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + this.assignableCount;
            result = 31 * result + (this.bridge ? 1231 : 1237);
            result = 31 * result + this.coercibleCount;
            result = 31 * result + this.exactCount;
            result = 31 * result + (this.varArgs ? 1231 : 1237);
            result = 31 * result + this.varArgsCount;
            return result;
        }
    }

    private static class PrivilegedGetTccl
    implements PrivilegedAction<ClassLoader> {
        private PrivilegedGetTccl() {
        }

        @Override
        public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
        }
    }
}

