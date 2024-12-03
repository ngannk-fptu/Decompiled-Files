/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.JreCompat;
import javax.el.MethodNotFoundException;

class Util {
    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final boolean IS_SECURITY_ENABLED;
    private static final boolean GET_CLASSLOADER_USE_PRIVILEGED;
    private static final CacheValue nullTcclFactory;
    private static final Map<CacheKey, CacheValue> factoryCache;

    Util() {
    }

    static void handleThrowable(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath)t;
        }
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError)t;
        }
    }

    static String message(ELContext context, String name, Object ... props) {
        Locale locale = null;
        if (context != null) {
            locale = context.getLocale();
        }
        if (locale == null && (locale = Locale.getDefault()) == null) {
            return "";
        }
        ResourceBundle bundle = ResourceBundle.getBundle("javax.el.LocalStrings", locale);
        try {
            String template = bundle.getString(name);
            if (props != null) {
                template = MessageFormat.format(template, props);
            }
            return template;
        }
        catch (MissingResourceException e) {
            return "Missing Resource: '" + name + "' for Locale " + locale.getDisplayName();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static ExpressionFactory getExpressionFactory() {
        ClassLoader tccl = Util.getContextClassLoader();
        CacheValue cacheValue = null;
        ExpressionFactory factory = null;
        if (tccl == null) {
            cacheValue = nullTcclFactory;
        } else {
            CacheValue newCacheValue;
            CacheKey key = new CacheKey(tccl);
            cacheValue = factoryCache.get(key);
            if (cacheValue == null && (cacheValue = factoryCache.putIfAbsent(key, newCacheValue = new CacheValue())) == null) {
                cacheValue = newCacheValue;
            }
        }
        Lock readLock = cacheValue.getLock().readLock();
        readLock.lock();
        try {
            factory = cacheValue.getExpressionFactory();
        }
        finally {
            readLock.unlock();
        }
        if (factory == null) {
            Lock writeLock = cacheValue.getLock().writeLock();
            writeLock.lock();
            try {
                factory = cacheValue.getExpressionFactory();
                if (factory == null) {
                    factory = ExpressionFactory.newInstance();
                    cacheValue.setExpressionFactory(factory);
                }
            }
            finally {
                writeLock.unlock();
            }
        }
        return factory;
    }

    static Method findMethod(ELContext context, Class<?> clazz, Object base, String methodName, Class<?>[] paramTypes, Object[] paramValues) {
        if (clazz == null || methodName == null) {
            throw new MethodNotFoundException(Util.message(null, "util.method.notfound", clazz, methodName, Util.paramString(paramTypes)));
        }
        if (paramTypes == null) {
            paramTypes = Util.getTypesFromValues(paramValues);
        }
        Method[] methods = clazz.getMethods();
        List wrappers = Wrapper.wrap(methods, methodName);
        Wrapper result = Util.findWrapper(context, clazz, wrappers, methodName, paramTypes, paramValues);
        return Util.getMethod(clazz, base, (Method)result.unWrap());
    }

    private static <T> Wrapper<T> findWrapper(ELContext context, Class<?> clazz, List<Wrapper<T>> wrappers, String name, Class<?>[] paramTypes, Object[] paramValues) {
        HashMap<Wrapper<T>, MatchResult> candidates = new HashMap<Wrapper<T>, MatchResult>();
        int paramCount = paramTypes.length;
        for (Wrapper<T> w : wrappers) {
            Class<?>[] mParamTypes = w.getParameterTypes();
            int mParamCount = mParamTypes == null ? 0 : mParamTypes.length;
            if (!w.isVarArgs() && paramCount != mParamCount || w.isVarArgs() && paramCount < mParamCount - 1 || w.isVarArgs() && paramCount == mParamCount && paramValues != null && paramValues.length > paramCount && !paramTypes[mParamCount - 1].isArray() || w.isVarArgs() && paramCount > mParamCount && paramValues != null && paramValues.length != paramCount || !w.isVarArgs() && paramValues != null && paramCount != paramValues.length) continue;
            int exactMatch = 0;
            int assignableMatch = 0;
            int coercibleMatch = 0;
            int varArgsMatch = 0;
            boolean noMatch = false;
            block1: for (int i = 0; i < mParamCount; ++i) {
                if (w.isVarArgs() && i == mParamCount - 1) {
                    if (i == paramCount || paramValues != null && paramValues.length == i) {
                        varArgsMatch = Integer.MAX_VALUE;
                        break;
                    }
                    Class<?> varType = mParamTypes[i].getComponentType();
                    for (int j = i; j < paramCount; ++j) {
                        if (Util.isAssignableFrom(paramTypes[j], varType)) {
                            ++assignableMatch;
                            ++varArgsMatch;
                            continue;
                        }
                        if (paramValues == null) {
                            noMatch = true;
                            continue block1;
                        }
                        if (Util.isCoercibleFrom(context, paramValues[j], varType)) {
                            ++coercibleMatch;
                            ++varArgsMatch;
                            continue;
                        }
                        noMatch = true;
                        continue block1;
                    }
                    continue;
                }
                if (mParamTypes[i].equals(paramTypes[i])) {
                    ++exactMatch;
                    continue;
                }
                if (paramTypes[i] != null && Util.isAssignableFrom(paramTypes[i], mParamTypes[i])) {
                    ++assignableMatch;
                    continue;
                }
                if (paramValues == null) {
                    noMatch = true;
                    break;
                }
                if (Util.isCoercibleFrom(context, paramValues[i], mParamTypes[i])) {
                    ++coercibleMatch;
                    continue;
                }
                noMatch = true;
                break;
            }
            if (noMatch) continue;
            if (exactMatch == paramCount && varArgsMatch == 0) {
                return w;
            }
            candidates.put(w, new MatchResult(w.isVarArgs(), exactMatch, assignableMatch, coercibleMatch, varArgsMatch, w.isBridge()));
        }
        MatchResult bestMatch = new MatchResult(true, 0, 0, 0, 0, true);
        Wrapper match = null;
        boolean multiple = false;
        for (Map.Entry entry : candidates.entrySet()) {
            int cmp = ((MatchResult)entry.getValue()).compareTo(bestMatch);
            if (cmp > 0 || match == null) {
                bestMatch = (MatchResult)entry.getValue();
                match = (Wrapper)entry.getKey();
                multiple = false;
                continue;
            }
            if (cmp != 0) continue;
            multiple = true;
        }
        if (multiple && (match = bestMatch.getExactCount() == paramCount - 1 ? Util.resolveAmbiguousWrapper(candidates.keySet(), paramTypes) : null) == null) {
            throw new MethodNotFoundException(Util.message(null, "util.method.ambiguous", clazz, name, Util.paramString(paramTypes)));
        }
        if (match == null) {
            throw new MethodNotFoundException(Util.message(null, "util.method.notfound", clazz, name, Util.paramString(paramTypes)));
        }
        return match;
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

    private static <T> Wrapper<T> resolveAmbiguousWrapper(Set<Wrapper<T>> candidates, Class<?>[] paramTypes) {
        Wrapper<T> w = candidates.iterator().next();
        int nonMatchIndex = 0;
        Class<?> nonMatchClass = null;
        for (int i = 0; i < paramTypes.length; ++i) {
            if (w.getParameterTypes()[i] == paramTypes[i]) continue;
            nonMatchIndex = i;
            nonMatchClass = paramTypes[i];
            break;
        }
        if (nonMatchClass == null) {
            return null;
        }
        for (Wrapper<T> c : candidates) {
            if (c.getParameterTypes()[nonMatchIndex] != paramTypes[nonMatchIndex]) continue;
            return null;
        }
        for (Class superClass = nonMatchClass.getSuperclass(); superClass != null; superClass = superClass.getSuperclass()) {
            for (Wrapper wrapper : candidates) {
                if (!wrapper.getParameterTypes()[nonMatchIndex].equals(superClass)) continue;
                return wrapper;
            }
        }
        Wrapper<T> match = null;
        if (Number.class.isAssignableFrom(nonMatchClass)) {
            for (Wrapper<T> c : candidates) {
                Class<?> candidateType = c.getParameterTypes()[nonMatchIndex];
                if (!Number.class.isAssignableFrom(candidateType) && !candidateType.isPrimitive()) continue;
                if (match == null) {
                    match = c;
                    continue;
                }
                match = null;
                break;
            }
        }
        return match;
    }

    static boolean isAssignableFrom(Class<?> src, Class<?> target) {
        if (src == null) {
            return true;
        }
        Class<Object> targetClass = target.isPrimitive() ? (target == Boolean.TYPE ? Boolean.class : (target == Character.TYPE ? Character.class : (target == Byte.TYPE ? Byte.class : (target == Short.TYPE ? Short.class : (target == Integer.TYPE ? Integer.class : (target == Long.TYPE ? Long.class : (target == Float.TYPE ? Float.class : Double.class))))))) : target;
        return targetClass.isAssignableFrom(src);
    }

    private static boolean isCoercibleFrom(ELContext context, Object src, Class<?> target) {
        try {
            context.convertToType(src, target);
        }
        catch (ELException e) {
            return false;
        }
        return true;
    }

    private static Class<?>[] getTypesFromValues(Object[] values) {
        if (values == null) {
            return EMPTY_CLASS_ARRAY;
        }
        Class[] result = new Class[values.length];
        for (int i = 0; i < values.length; ++i) {
            result[i] = values[i] == null ? null : values[i].getClass();
        }
        return result;
    }

    static Method getMethod(Class<?> type, Object base, Method m) {
        JreCompat jreCompat = JreCompat.getInstance();
        if (m == null || Modifier.isPublic(type.getModifiers()) && (Modifier.isStatic(m.getModifiers()) && jreCompat.canAccess(null, m) || jreCompat.canAccess(base, m))) {
            return m;
        }
        Class<?>[] interfaces = type.getInterfaces();
        Method mp = null;
        for (Class<?> iface : interfaces) {
            try {
                mp = iface.getMethod(m.getName(), m.getParameterTypes());
                mp = Util.getMethod(mp.getDeclaringClass(), base, mp);
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
                mp = Util.getMethod(mp.getDeclaringClass(), base, mp);
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

    static Constructor<?> findConstructor(ELContext context, Class<?> clazz, Class<?>[] paramTypes, Object[] paramValues) {
        String methodName = "<init>";
        if (clazz == null) {
            throw new MethodNotFoundException(Util.message(null, "util.method.notfound", null, methodName, Util.paramString(paramTypes)));
        }
        if (paramTypes == null) {
            paramTypes = Util.getTypesFromValues(paramValues);
        }
        Constructor<?>[] constructors = clazz.getConstructors();
        List wrappers = Wrapper.wrap(constructors);
        Wrapper wrapper = Util.findWrapper(context, clazz, wrappers, methodName, paramTypes, paramValues);
        Constructor constructor = (Constructor)wrapper.unWrap();
        JreCompat jreCompat = JreCompat.getInstance();
        if (!Modifier.isPublic(clazz.getModifiers()) || !jreCompat.canAccess(null, constructor)) {
            throw new MethodNotFoundException(Util.message(null, "util.method.notfound", clazz, methodName, Util.paramString(paramTypes)));
        }
        return constructor;
    }

    static Object[] buildParameters(ELContext context, Class<?>[] parameterTypes, boolean isVarArgs, Object[] params) {
        Object[] parameters = null;
        if (parameterTypes.length > 0) {
            parameters = new Object[parameterTypes.length];
            if (params == null) {
                params = EMPTY_OBJECT_ARRAY;
            }
            int paramCount = params.length;
            if (isVarArgs) {
                int varArgIndex = parameterTypes.length - 1;
                for (int i = 0; i < varArgIndex; ++i) {
                    parameters[i] = context.convertToType(params[i], parameterTypes[i]);
                }
                Class<?> varArgClass = parameterTypes[varArgIndex].getComponentType();
                Object varargs = Array.newInstance(varArgClass, paramCount - varArgIndex);
                for (int i = varArgIndex; i < paramCount; ++i) {
                    Array.set(varargs, i - varArgIndex, context.convertToType(params[i], varArgClass));
                }
                parameters[varArgIndex] = varargs;
            } else {
                parameters = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; ++i) {
                    parameters[i] = context.convertToType(params[i], parameterTypes[i]);
                }
            }
        }
        return parameters;
    }

    static ClassLoader getContextClassLoader() {
        ClassLoader tccl;
        if (IS_SECURITY_ENABLED && GET_CLASSLOADER_USE_PRIVILEGED) {
            PrivilegedGetTccl pa = new PrivilegedGetTccl();
            tccl = AccessController.doPrivileged(pa);
        } else {
            tccl = Thread.currentThread().getContextClassLoader();
        }
        return tccl;
    }

    static {
        boolean bl = IS_SECURITY_ENABLED = System.getSecurityManager() != null;
        if (IS_SECURITY_ENABLED) {
            String value = AccessController.doPrivileged(() -> System.getProperty("org.apache.el.GET_CLASSLOADER_USE_PRIVILEGED", "true"));
            GET_CLASSLOADER_USE_PRIVILEGED = Boolean.parseBoolean(value);
        } else {
            GET_CLASSLOADER_USE_PRIVILEGED = false;
        }
        nullTcclFactory = new CacheValue();
        factoryCache = new ConcurrentHashMap<CacheKey, CacheValue>();
    }

    private static class CacheValue {
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private WeakReference<ExpressionFactory> ref;

        CacheValue() {
        }

        public ReadWriteLock getLock() {
            return this.lock;
        }

        public ExpressionFactory getExpressionFactory() {
            return this.ref != null ? (ExpressionFactory)this.ref.get() : null;
        }

        public void setExpressionFactory(ExpressionFactory factory) {
            this.ref = new WeakReference<ExpressionFactory>(factory);
        }
    }

    private static class CacheKey {
        private final int hash;
        private final WeakReference<ClassLoader> ref;

        CacheKey(ClassLoader key) {
            this.hash = key.hashCode();
            this.ref = new WeakReference<ClassLoader>(key);
        }

        public int hashCode() {
            return this.hash;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof CacheKey)) {
                return false;
            }
            ClassLoader thisKey = (ClassLoader)this.ref.get();
            if (thisKey == null) {
                return false;
            }
            return thisKey == ((CacheKey)obj).ref.get();
        }
    }

    private static abstract class Wrapper<T> {
        private Wrapper() {
        }

        public static List<Wrapper<Method>> wrap(Method[] methods, String name) {
            ArrayList<Wrapper<Method>> result = new ArrayList<Wrapper<Method>>();
            for (Method method : methods) {
                if (!method.getName().equals(name)) continue;
                result.add(new MethodWrapper(method));
            }
            return result;
        }

        public static List<Wrapper<Constructor<?>>> wrap(Constructor<?>[] constructors) {
            ArrayList result = new ArrayList();
            for (Constructor<?> constructor : constructors) {
                result.add(new ConstructorWrapper(constructor));
            }
            return result;
        }

        public abstract T unWrap();

        public abstract Class<?>[] getParameterTypes();

        public abstract boolean isVarArgs();

        public abstract boolean isBridge();
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

        public int getCoercibleCount() {
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
            if (cmp == 0 && (cmp = Integer.compare(this.getExactCount(), o.getExactCount())) == 0 && (cmp = Integer.compare(this.getAssignableCount(), o.getAssignableCount())) == 0 && (cmp = Integer.compare(this.getCoercibleCount(), o.getCoercibleCount())) == 0 && (cmp = Integer.compare(o.getVarArgsCount(), this.getVarArgsCount())) == 0) {
                cmp = Boolean.compare(o.isBridge(), this.isBridge());
            }
            return cmp;
        }

        public boolean equals(Object o) {
            return o == this || null != o && this.getClass().equals(o.getClass()) && ((MatchResult)o).getExactCount() == this.getExactCount() && ((MatchResult)o).getAssignableCount() == this.getAssignableCount() && ((MatchResult)o).getCoercibleCount() == this.getCoercibleCount() && ((MatchResult)o).getVarArgsCount() == this.getVarArgsCount() && ((MatchResult)o).isVarArgs() == this.isVarArgs() && ((MatchResult)o).isBridge() == this.isBridge();
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

    private static class ConstructorWrapper
    extends Wrapper<Constructor<?>> {
        private final Constructor<?> c;

        ConstructorWrapper(Constructor<?> c) {
            this.c = c;
        }

        @Override
        public Constructor<?> unWrap() {
            return this.c;
        }

        @Override
        public Class<?>[] getParameterTypes() {
            return this.c.getParameterTypes();
        }

        @Override
        public boolean isVarArgs() {
            return this.c.isVarArgs();
        }

        @Override
        public boolean isBridge() {
            return false;
        }
    }

    private static class MethodWrapper
    extends Wrapper<Method> {
        private final Method m;

        MethodWrapper(Method m) {
            this.m = m;
        }

        @Override
        public Method unWrap() {
            return this.m;
        }

        @Override
        public Class<?>[] getParameterTypes() {
            return this.m.getParameterTypes();
        }

        @Override
        public boolean isVarArgs() {
            return this.m.isVarArgs();
        }

        @Override
        public boolean isBridge() {
            return this.m.isBridge();
        }
    }
}

