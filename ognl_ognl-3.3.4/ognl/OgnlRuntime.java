/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permission;
import java.security.Permissions;
import java.security.PrivilegedActionException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import ognl.ASTChain;
import ognl.ASTConst;
import ognl.AccessibleObjectHandler;
import ognl.AccessibleObjectHandlerJDK9Plus;
import ognl.AccessibleObjectHandlerPreJDK9;
import ognl.ArrayElementsAccessor;
import ognl.ArrayPropertyAccessor;
import ognl.ClassCacheInspector;
import ognl.ClassResolver;
import ognl.CollectionElementsAccessor;
import ognl.DefaultClassResolver;
import ognl.ElementsAccessor;
import ognl.EnumerationElementsAccessor;
import ognl.EnumerationPropertyAccessor;
import ognl.EvaluationPool;
import ognl.IteratorElementsAccessor;
import ognl.IteratorPropertyAccessor;
import ognl.ListPropertyAccessor;
import ognl.MapElementsAccessor;
import ognl.MapPropertyAccessor;
import ognl.MemberAccess;
import ognl.MethodAccessor;
import ognl.MethodFailedException;
import ognl.NoSuchPropertyException;
import ognl.Node;
import ognl.NullHandler;
import ognl.NumberElementsAccessor;
import ognl.ObjectArrayPool;
import ognl.ObjectElementsAccessor;
import ognl.ObjectIndexedPropertyDescriptor;
import ognl.ObjectMethodAccessor;
import ognl.ObjectNullHandler;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlInvokePermission;
import ognl.OgnlOps;
import ognl.PropertyAccessor;
import ognl.SetPropertyAccessor;
import ognl.TypeConverter;
import ognl.enhance.ExpressionCompiler;
import ognl.enhance.OgnlExpressionCompiler;
import ognl.internal.ClassCache;
import ognl.internal.ClassCacheImpl;
import ognl.security.OgnlSecurityManagerFactory;
import ognl.security.UserMethod;

public class OgnlRuntime {
    public static final Object NotFound;
    public static final List NotFoundList;
    public static final Map NotFoundMap;
    public static final Object[] NoArguments;
    public static final Class[] NoArgumentTypes;
    public static final Object NoConversionPossible;
    public static int INDEXED_PROPERTY_NONE;
    public static int INDEXED_PROPERTY_INT;
    public static int INDEXED_PROPERTY_OBJECT;
    public static final String NULL_STRING;
    private static final String SET_PREFIX = "set";
    private static final String GET_PREFIX = "get";
    private static final String IS_PREFIX = "is";
    private static final Map HEX_PADDING;
    private static final int HEX_LENGTH = 8;
    private static final String NULL_OBJECT_STRING = "<null>";
    private static boolean _jdk15;
    private static boolean _jdkChecked;
    static final String USE_JDK9PLUS_ACESS_HANDLER = "ognl.UseJDK9PlusAccessHandler";
    static final String USE_STRICTER_INVOCATION = "ognl.UseStricterInvocation";
    private static final boolean _useJDK9PlusAccessHandler;
    private static final boolean _useStricterInvocation;
    private static final int _majorJavaVersion;
    private static final boolean _jdk9Plus;
    private static final AccessibleObjectHandler _accessibleObjectHandler;
    private static final Method SYS_CONSOLE_REF;
    private static final Method SYS_EXIT_REF;
    private static final Method AO_SETACCESSIBLE_REF;
    private static final Method AO_SETACCESSIBLE_ARR_REF;
    static final String OGNL_SECURITY_MANAGER = "ognl.security.manager";
    static final String OGNL_SM_FORCE_DISABLE_ON_INIT = "forceDisableOnInit";
    private static final boolean _disableOgnlSecurityManagerOnInit;
    static final String USE_FIRSTMATCH_GETSET_LOOKUP = "ognl.UseFirstMatchGetSetLookup";
    private static final boolean _useFirstMatchGetSetLookup;
    static final ClassCache _methodAccessors;
    static final ClassCache _propertyAccessors;
    static final ClassCache _elementsAccessors;
    static final ClassCache _nullHandlers;
    static final ClassCache _propertyDescriptorCache;
    static final ClassCache _constructorCache;
    static final ClassCache _staticMethodCache;
    static final ClassCache _instanceMethodCache;
    static final ClassCache _invokePermissionCache;
    static final ClassCache _fieldCache;
    static final List _superclasses;
    static final ClassCache[] _declaredMethods;
    static final Map _primitiveTypes;
    static final ClassCache _primitiveDefaults;
    static final Map _methodParameterTypesCache;
    static final Map _genericMethodParameterTypesCache;
    static final Map _ctorParameterTypesCache;
    static SecurityManager _securityManager;
    static final EvaluationPool _evaluationPool;
    static final ObjectArrayPool _objectArrayPool;
    static final Map<Method, Boolean> _methodAccessCache;
    static final Map<Method, Boolean> _methodPermCache;
    static final ClassPropertyMethodCache cacheSetMethod;
    static final ClassPropertyMethodCache cacheGetMethod;
    static ClassCacheInspector _cacheInspector;
    private static OgnlExpressionCompiler _compiler;
    private static final Class[] EMPTY_CLASS_ARRAY;
    private static IdentityHashMap PRIMITIVE_WRAPPER_CLASSES;
    private static final Map NUMERIC_CASTS;
    private static final Map NUMERIC_VALUES;
    private static final Map NUMERIC_LITERALS;
    private static final Map NUMERIC_DEFAULTS;
    public static final ArgsCompatbilityReport NoArgsReport;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void clearCache() {
        Object object = _methodParameterTypesCache;
        synchronized (object) {
            _methodParameterTypesCache.clear();
        }
        object = _ctorParameterTypesCache;
        synchronized (object) {
            _ctorParameterTypesCache.clear();
        }
        object = _propertyDescriptorCache;
        synchronized (object) {
            _propertyDescriptorCache.clear();
        }
        object = _constructorCache;
        synchronized (object) {
            _constructorCache.clear();
        }
        object = _staticMethodCache;
        synchronized (object) {
            _staticMethodCache.clear();
        }
        object = _instanceMethodCache;
        synchronized (object) {
            _instanceMethodCache.clear();
        }
        object = _invokePermissionCache;
        synchronized (object) {
            _invokePermissionCache.clear();
        }
        object = _fieldCache;
        synchronized (object) {
            _fieldCache.clear();
            _superclasses.clear();
        }
        object = _declaredMethods[0];
        synchronized (object) {
            _declaredMethods[0].clear();
        }
        object = _declaredMethods[1];
        synchronized (object) {
            _declaredMethods[1].clear();
        }
        _methodAccessCache.clear();
        _methodPermCache.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void clearAdditionalCache() {
        cacheSetMethod.clear();
        cacheGetMethod.clear();
        Map map = _genericMethodParameterTypesCache;
        synchronized (map) {
            _genericMethodParameterTypesCache.clear();
        }
    }

    public static int getMajorJavaVersion() {
        return _majorJavaVersion;
    }

    public static boolean isJdk9Plus() {
        return _jdk9Plus;
    }

    public static String getNumericValueGetter(Class type) {
        return (String)NUMERIC_VALUES.get(type);
    }

    public static Class getPrimitiveWrapperClass(Class primitiveClass) {
        return (Class)PRIMITIVE_WRAPPER_CLASSES.get(primitiveClass);
    }

    public static String getNumericCast(Class type) {
        return (String)NUMERIC_CASTS.get(type);
    }

    public static String getNumericLiteral(Class type) {
        return (String)NUMERIC_LITERALS.get(type);
    }

    public static void setCompiler(OgnlExpressionCompiler compiler) {
        _compiler = compiler;
    }

    public static OgnlExpressionCompiler getCompiler() {
        return _compiler;
    }

    public static void compileExpression(OgnlContext context, Node expression, Object root) throws Exception {
        _compiler.compileExpression(context, expression, root);
    }

    public static Class getTargetClass(Object o) {
        return o == null ? null : (o instanceof Class ? (Class<?>)o : o.getClass());
    }

    public static String getBaseName(Object o) {
        return o == null ? null : OgnlRuntime.getClassBaseName(o.getClass());
    }

    public static String getClassBaseName(Class c) {
        String s = c.getName();
        return s.substring(s.lastIndexOf(46) + 1);
    }

    public static String getClassName(Object o, boolean fullyQualified) {
        if (!(o instanceof Class)) {
            o = o.getClass();
        }
        return OgnlRuntime.getClassName(o, fullyQualified);
    }

    public static String getClassName(Class c, boolean fullyQualified) {
        return fullyQualified ? c.getName() : OgnlRuntime.getClassBaseName(c);
    }

    public static String getPackageName(Object o) {
        return o == null ? null : OgnlRuntime.getClassPackageName(o.getClass());
    }

    public static String getClassPackageName(Class c) {
        String s = c.getName();
        int i = s.lastIndexOf(46);
        return i < 0 ? null : s.substring(0, i);
    }

    public static String getPointerString(int num) {
        StringBuffer result = new StringBuffer();
        String hex = Integer.toHexString(num);
        Integer l = new Integer(hex.length());
        String pad = (String)HEX_PADDING.get(l);
        if (pad == null) {
            StringBuffer pb = new StringBuffer();
            for (int i = hex.length(); i < 8; ++i) {
                pb.append('0');
            }
            pad = new String(pb);
            HEX_PADDING.put(l, pad);
        }
        result.append(pad);
        result.append(hex);
        return new String(result);
    }

    public static String getPointerString(Object o) {
        return OgnlRuntime.getPointerString(o == null ? 0 : System.identityHashCode(o));
    }

    public static String getUniqueDescriptor(Object object, boolean fullyQualified) {
        StringBuffer result = new StringBuffer();
        if (object != null) {
            if (object instanceof Proxy) {
                Class<?> interfaceClass = object.getClass().getInterfaces()[0];
                result.append(OgnlRuntime.getClassName(interfaceClass, fullyQualified));
                result.append('^');
                object = Proxy.getInvocationHandler(object);
            }
            result.append(OgnlRuntime.getClassName(object, fullyQualified));
            result.append('@');
            result.append(OgnlRuntime.getPointerString(object));
        } else {
            result.append(NULL_OBJECT_STRING);
        }
        return new String(result);
    }

    public static String getUniqueDescriptor(Object object) {
        return OgnlRuntime.getUniqueDescriptor(object, false);
    }

    public static Object[] toArray(List list) {
        Object[] result;
        int size = list.size();
        if (size == 0) {
            result = NoArguments;
        } else {
            result = OgnlRuntime.getObjectArrayPool().create(list.size());
            for (int i = 0; i < size; ++i) {
                result[i] = list.get(i);
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Class[] getParameterTypes(Method m) {
        Map map = _methodParameterTypesCache;
        synchronized (map) {
            Class[] result = (Class[])_methodParameterTypesCache.get(m);
            if (result == null) {
                result = m.getParameterTypes();
                _methodParameterTypesCache.put(m, result);
            }
            return result;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Class[] findParameterTypes(Class type, Method m) {
        ParameterizedType genericSuperclass;
        Type[] genTypes = m.getGenericParameterTypes();
        Object[] types = new Class[genTypes.length];
        boolean noGenericParameter = true;
        for (int i = 0; i < genTypes.length; ++i) {
            if (!Class.class.isInstance(genTypes[i])) {
                noGenericParameter = false;
                break;
            }
            types[i] = (Class)genTypes[i];
        }
        if (noGenericParameter) {
            return types;
        }
        if (type == null) {
            return OgnlRuntime.getParameterTypes(m);
        }
        Type typeGenericSuperclass = type.getGenericSuperclass();
        if (typeGenericSuperclass == null || !ParameterizedType.class.isInstance(typeGenericSuperclass) || m.getDeclaringClass().getTypeParameters() == null) {
            return OgnlRuntime.getParameterTypes(m);
        }
        types = (Class[])_genericMethodParameterTypesCache.get(m);
        if (types != null && Arrays.equals(types, (genericSuperclass = (ParameterizedType)typeGenericSuperclass).getActualTypeArguments())) {
            return types;
        }
        ParameterizedType param = (ParameterizedType)typeGenericSuperclass;
        TypeVariable[] declaredTypes = m.getDeclaringClass().getTypeParameters();
        types = new Class[genTypes.length];
        for (int i = 0; i < genTypes.length; ++i) {
            TypeVariable paramType = null;
            if (TypeVariable.class.isInstance(genTypes[i])) {
                paramType = (TypeVariable)genTypes[i];
            } else if (GenericArrayType.class.isInstance(genTypes[i])) {
                paramType = (TypeVariable)((GenericArrayType)genTypes[i]).getGenericComponentType();
            } else {
                if (ParameterizedType.class.isInstance(genTypes[i])) {
                    types[i] = (Class)((ParameterizedType)genTypes[i]).getRawType();
                    continue;
                }
                if (Class.class.isInstance(genTypes[i])) {
                    types[i] = (Class)genTypes[i];
                    continue;
                }
            }
            Class<?> resolved = OgnlRuntime.resolveType(param, paramType, declaredTypes);
            if (resolved != null) {
                if (GenericArrayType.class.isInstance(genTypes[i])) {
                    resolved = Array.newInstance(resolved, 0).getClass();
                }
                types[i] = resolved;
                continue;
            }
            types[i] = m.getParameterTypes()[i];
        }
        Map map = _genericMethodParameterTypesCache;
        synchronized (map) {
            _genericMethodParameterTypesCache.put(m, types);
        }
        return types;
    }

    static Class resolveType(ParameterizedType param, TypeVariable var, TypeVariable[] declaredTypes) {
        if (param.getActualTypeArguments().length < 1) {
            return null;
        }
        for (int i = 0; i < declaredTypes.length; ++i) {
            if (TypeVariable.class.isInstance(param.getActualTypeArguments()[i]) || !declaredTypes[i].getName().equals(var.getName())) continue;
            return (Class)param.getActualTypeArguments()[i];
        }
        return null;
    }

    static Class findType(Type[] types, Class type) {
        for (int i = 0; i < types.length; ++i) {
            if (!Class.class.isInstance(types[i]) || !type.isAssignableFrom((Class)types[i])) continue;
            return (Class)types[i];
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Class[] getParameterTypes(Constructor c) {
        Class[] result = (Class[])_ctorParameterTypesCache.get(c);
        if (result == null) {
            Map map = _ctorParameterTypesCache;
            synchronized (map) {
                result = (Class[])_ctorParameterTypesCache.get(c);
                if (result == null) {
                    result = c.getParameterTypes();
                    _ctorParameterTypesCache.put(c, result);
                }
            }
        }
        return result;
    }

    public static SecurityManager getSecurityManager() {
        return _securityManager;
    }

    public static void setSecurityManager(SecurityManager value) {
        _securityManager = value;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Permission getPermission(Method method) {
        Permission result;
        Class<?> mc = method.getDeclaringClass();
        ClassCache classCache = _invokePermissionCache;
        synchronized (classCache) {
            HashMap<String, Permission> permissions = (HashMap<String, Permission>)_invokePermissionCache.get(mc);
            if (permissions == null) {
                permissions = new HashMap<String, Permission>(101);
                _invokePermissionCache.put(mc, permissions);
            }
            if ((result = (Permission)permissions.get(method.getName())) == null) {
                result = new OgnlInvokePermission("invoke." + mc.getName() + "." + method.getName());
                permissions.put(method.getName(), result);
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Object invokeMethod(Object target, Method method, Object[] argsArray) throws InvocationTargetException, IllegalAccessException {
        Object result;
        boolean checkPermission;
        boolean syncInvoke;
        GenericDeclaration methodDeclaringClass;
        if (_useStricterInvocation) {
            methodDeclaringClass = method.getDeclaringClass();
            if (AO_SETACCESSIBLE_REF != null && AO_SETACCESSIBLE_REF.equals(method) || AO_SETACCESSIBLE_ARR_REF != null && AO_SETACCESSIBLE_ARR_REF.equals(method) || SYS_EXIT_REF != null && SYS_EXIT_REF.equals(method) || SYS_CONSOLE_REF != null && SYS_CONSOLE_REF.equals(method) || AccessibleObjectHandler.class.isAssignableFrom((Class<?>)methodDeclaringClass) || ClassResolver.class.isAssignableFrom((Class<?>)methodDeclaringClass) || MethodAccessor.class.isAssignableFrom((Class<?>)methodDeclaringClass) || MemberAccess.class.isAssignableFrom((Class<?>)methodDeclaringClass) || OgnlContext.class.isAssignableFrom((Class<?>)methodDeclaringClass) || Runtime.class.isAssignableFrom((Class<?>)methodDeclaringClass) || ClassLoader.class.isAssignableFrom((Class<?>)methodDeclaringClass) || ProcessBuilder.class.isAssignableFrom((Class<?>)methodDeclaringClass) || AccessibleObjectHandlerJDK9Plus.unsafeOrDescendant(methodDeclaringClass)) {
                throw new IllegalAccessException("Method [" + method + "] cannot be called from within OGNL invokeMethod() under stricter invocation mode.");
            }
        }
        methodDeclaringClass = method;
        synchronized (methodDeclaringClass) {
            Boolean methodAccessCacheValue = _methodAccessCache.get(method);
            if (methodAccessCacheValue == null) {
                if (!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
                    if (!method.isAccessible()) {
                        methodAccessCacheValue = Boolean.TRUE;
                        _methodAccessCache.put(method, methodAccessCacheValue);
                    } else {
                        methodAccessCacheValue = Boolean.FALSE;
                        _methodAccessCache.put(method, methodAccessCacheValue);
                    }
                } else {
                    methodAccessCacheValue = Boolean.FALSE;
                    _methodAccessCache.put(method, methodAccessCacheValue);
                }
            }
            syncInvoke = Boolean.TRUE.equals(methodAccessCacheValue);
            Boolean methodPermCacheValue = _methodPermCache.get(method);
            if (methodPermCacheValue == null) {
                if (_securityManager != null) {
                    try {
                        _securityManager.checkPermission(OgnlRuntime.getPermission(method));
                        methodPermCacheValue = Boolean.TRUE;
                        _methodPermCache.put(method, methodPermCacheValue);
                    }
                    catch (SecurityException ex) {
                        methodPermCacheValue = Boolean.FALSE;
                        _methodPermCache.put(method, methodPermCacheValue);
                        throw new IllegalAccessException("Method [" + method + "] cannot be accessed.");
                    }
                } else {
                    methodPermCacheValue = Boolean.TRUE;
                    _methodPermCache.put(method, methodPermCacheValue);
                }
            }
            checkPermission = Boolean.FALSE.equals(methodPermCacheValue);
        }
        if (syncInvoke) {
            Method ex = method;
            synchronized (ex) {
                if (checkPermission) {
                    try {
                        _securityManager.checkPermission(OgnlRuntime.getPermission(method));
                    }
                    catch (SecurityException ex2) {
                        throw new IllegalAccessException("Method [" + method + "] cannot be accessed.");
                    }
                }
                _accessibleObjectHandler.setAccessible(method, true);
                try {
                    result = OgnlRuntime.invokeMethodInsideSandbox(target, method, argsArray);
                }
                finally {
                    _accessibleObjectHandler.setAccessible(method, false);
                }
            }
        }
        if (checkPermission) {
            try {
                _securityManager.checkPermission(OgnlRuntime.getPermission(method));
            }
            catch (SecurityException ex) {
                throw new IllegalAccessException("Method [" + method + "] cannot be accessed.");
            }
        }
        result = OgnlRuntime.invokeMethodInsideSandbox(target, method, argsArray);
        return result;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static Object invokeMethodInsideSandbox(Object target, Method method, Object[] argsArray) throws InvocationTargetException, IllegalAccessException {
        Object e2;
        Long token;
        if (_disableOgnlSecurityManagerOnInit) {
            return method.invoke(target, argsArray);
        }
        try {
            if (System.getProperty(OGNL_SECURITY_MANAGER) == null) {
                return method.invoke(target, argsArray);
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        if (ClassLoader.class.isAssignableFrom(method.getDeclaringClass())) {
            throw new IllegalAccessException("OGNL direct access to class loader denied!");
        }
        UserMethod userMethod = new UserMethod(target, method, argsArray);
        Permissions p = new Permissions();
        ProtectionDomain pd = new ProtectionDomain(null, p);
        AccessControlContext acc = new AccessControlContext(new ProtectionDomain[]{pd});
        Object ognlSecurityManager = OgnlSecurityManagerFactory.getOgnlSecurityManager();
        try {
            token = (Long)ognlSecurityManager.getClass().getMethod("enter", new Class[0]).invoke(ognlSecurityManager, new Object[0]);
        }
        catch (NoSuchMethodException e2) {
            throw new InvocationTargetException(e2);
        }
        if (token == null) {
            return method.invoke(target, argsArray);
        }
        try {
            e2 = AccessController.doPrivileged(userMethod, acc);
        }
        catch (PrivilegedActionException e2) {
            try {
                if (!(e2.getException() instanceof InvocationTargetException)) throw new InvocationTargetException(e2);
                throw (InvocationTargetException)e2.getException();
            }
            catch (Throwable throwable) {
                try {
                    ognlSecurityManager.getClass().getMethod("leave", Long.TYPE).invoke(ognlSecurityManager, token);
                    throw throwable;
                }
                catch (NoSuchMethodException e3) {
                    throw new InvocationTargetException(e3);
                }
            }
        }
        try {
            ognlSecurityManager.getClass().getMethod("leave", Long.TYPE).invoke(ognlSecurityManager, token);
            return e2;
        }
        catch (NoSuchMethodException e2) {
            throw new InvocationTargetException(e2);
        }
    }

    public static final Class getArgClass(Object arg) {
        if (arg == null) {
            return null;
        }
        Class<?> c = arg.getClass();
        if (c == Boolean.class) {
            return Boolean.TYPE;
        }
        if (c.getSuperclass() == Number.class) {
            if (c == Integer.class) {
                return Integer.TYPE;
            }
            if (c == Double.class) {
                return Double.TYPE;
            }
            if (c == Byte.class) {
                return Byte.TYPE;
            }
            if (c == Long.class) {
                return Long.TYPE;
            }
            if (c == Float.class) {
                return Float.TYPE;
            }
            if (c == Short.class) {
                return Short.TYPE;
            }
        } else if (c == Character.class) {
            return Character.TYPE;
        }
        return c;
    }

    public static Class[] getArgClasses(Object[] args) {
        if (args == null) {
            return null;
        }
        Class[] argClasses = new Class[args.length];
        for (int i = 0; i < args.length; ++i) {
            argClasses[i] = OgnlRuntime.getArgClass(args[i]);
        }
        return argClasses;
    }

    public static final boolean isTypeCompatible(Object object, Class c) {
        if (object == null) {
            return true;
        }
        ArgsCompatbilityReport report = new ArgsCompatbilityReport(0, new boolean[1]);
        if (!OgnlRuntime.isTypeCompatible(OgnlRuntime.getArgClass(object), c, 0, report)) {
            return false;
        }
        return !report.conversionNeeded[0];
    }

    public static final boolean isTypeCompatible(Class parameterClass, Class methodArgumentClass, int index, ArgsCompatbilityReport report) {
        if (parameterClass == null) {
            report.score += 500;
            return true;
        }
        if (parameterClass == methodArgumentClass) {
            return true;
        }
        if (methodArgumentClass.isArray()) {
            if (parameterClass.isArray()) {
                Class<?> pct = parameterClass.getComponentType();
                Class<?> mct = methodArgumentClass.getComponentType();
                if (mct.isAssignableFrom(pct)) {
                    report.score += 25;
                    return true;
                }
            }
            if (Collection.class.isAssignableFrom(parameterClass)) {
                Class<?> mct = methodArgumentClass.getComponentType();
                if (mct == Object.class) {
                    report.conversionNeeded[index] = true;
                    report.score += 30;
                    return true;
                }
                return false;
            }
        } else if (Collection.class.isAssignableFrom(methodArgumentClass)) {
            if (parameterClass.isArray()) {
                report.conversionNeeded[index] = true;
                report.score += 50;
                return true;
            }
            if (Collection.class.isAssignableFrom(parameterClass)) {
                if (methodArgumentClass.isAssignableFrom(parameterClass)) {
                    report.score += 2;
                    return true;
                }
                report.conversionNeeded[index] = true;
                report.score += 50;
                return true;
            }
        }
        if (methodArgumentClass.isAssignableFrom(parameterClass)) {
            report.score += 40;
            return true;
        }
        if (parameterClass.isPrimitive()) {
            Class ptc = (Class)PRIMITIVE_WRAPPER_CLASSES.get(parameterClass);
            if (methodArgumentClass == ptc) {
                report.score += 2;
                return true;
            }
            if (methodArgumentClass.isAssignableFrom(ptc)) {
                report.score += 10;
                return true;
            }
        }
        return false;
    }

    public static boolean areArgsCompatible(Object[] args, Class[] classes) {
        ArgsCompatbilityReport report = OgnlRuntime.areArgsCompatible(OgnlRuntime.getArgClasses(args), classes, null);
        if (report == null) {
            return false;
        }
        for (boolean conversionNeeded : report.conversionNeeded) {
            if (!conversionNeeded) continue;
            return false;
        }
        return true;
    }

    public static ArgsCompatbilityReport areArgsCompatible(Class[] args, Class[] classes, Method m) {
        boolean varArgs;
        boolean bl = varArgs = m != null && m.isVarArgs();
        if (args == null || args.length == 0) {
            if (classes == null || classes.length == 0) {
                return NoArgsReport;
            }
            if (varArgs) {
                return NoArgsReport;
            }
            return null;
        }
        if (args.length != classes.length && !varArgs) {
            return null;
        }
        if (varArgs) {
            ArgsCompatbilityReport report = new ArgsCompatbilityReport(1000, new boolean[args.length]);
            if (classes.length - 1 > args.length) {
                return null;
            }
            int count = classes.length - 1;
            for (int index = 0; index < count; ++index) {
                if (OgnlRuntime.isTypeCompatible(args[index], classes[index], index, report)) continue;
                return null;
            }
            Class<?> varArgsType = classes[classes.length - 1].getComponentType();
            int count2 = args.length;
            for (int index = classes.length - 1; index < count2; ++index) {
                if (OgnlRuntime.isTypeCompatible(args[index], varArgsType, index, report)) continue;
                return null;
            }
            return report;
        }
        ArgsCompatbilityReport report = new ArgsCompatbilityReport(0, new boolean[args.length]);
        int count = args.length;
        for (int index = 0; index < count; ++index) {
            if (OgnlRuntime.isTypeCompatible(args[index], classes[index], index, report)) continue;
            return null;
        }
        return report;
    }

    public static final boolean isMoreSpecific(Class[] classes1, Class[] classes2) {
        int count = classes1.length;
        for (int index = 0; index < count; ++index) {
            Class c1 = classes1[index];
            Class c2 = classes2[index];
            if (c1 == c2) continue;
            if (c1.isPrimitive()) {
                return true;
            }
            if (c1.isAssignableFrom(c2)) {
                return false;
            }
            if (!c2.isAssignableFrom(c1)) continue;
            return true;
        }
        return false;
    }

    public static String getModifierString(int modifiers) {
        String result = Modifier.isPublic(modifiers) ? "public" : (Modifier.isProtected(modifiers) ? "protected" : (Modifier.isPrivate(modifiers) ? "private" : ""));
        if (Modifier.isStatic(modifiers)) {
            result = "static " + result;
        }
        if (Modifier.isFinal(modifiers)) {
            result = "final " + result;
        }
        if (Modifier.isNative(modifiers)) {
            result = "native " + result;
        }
        if (Modifier.isSynchronized(modifiers)) {
            result = "synchronized " + result;
        }
        if (Modifier.isTransient(modifiers)) {
            result = "transient " + result;
        }
        return result;
    }

    public static Class classForName(OgnlContext context, String className) throws ClassNotFoundException {
        Class result = (Class)_primitiveTypes.get(className);
        if (result == null) {
            ClassResolver resolver;
            if (context == null || (resolver = context.getClassResolver()) == null) {
                resolver = new DefaultClassResolver();
            }
            result = resolver.classForName(className, context);
        }
        if (result == null) {
            throw new ClassNotFoundException("Unable to resolve class: " + className);
        }
        return result;
    }

    public static boolean isInstance(OgnlContext context, Object value, String className) throws OgnlException {
        try {
            Class c = OgnlRuntime.classForName(context, className);
            return c.isInstance(value);
        }
        catch (ClassNotFoundException e) {
            throw new OgnlException("No such class: " + className, e);
        }
    }

    public static Object getPrimitiveDefaultValue(Class forClass) {
        return _primitiveDefaults.get(forClass);
    }

    public static Object getNumericDefaultValue(Class forClass) {
        return NUMERIC_DEFAULTS.get(forClass);
    }

    public static Object getConvertedType(OgnlContext context, Object target, Member member, String propertyName, Object value, Class type) {
        return context.getTypeConverter().convertValue(context, target, member, propertyName, value, type);
    }

    public static boolean getConvertedTypes(OgnlContext context, Object target, Member member, String propertyName, Class[] parameterTypes, Object[] args, Object[] newArgs) {
        boolean result = false;
        if (parameterTypes.length == args.length) {
            result = true;
            int ilast = parameterTypes.length - 1;
            for (int i = 0; result && i <= ilast; ++i) {
                Object arg = args[i];
                Class type = parameterTypes[i];
                if (OgnlRuntime.isTypeCompatible(arg, type)) {
                    newArgs[i] = arg;
                    continue;
                }
                Object v = OgnlRuntime.getConvertedType(context, target, member, propertyName, arg, type);
                if (v == NoConversionPossible) {
                    result = false;
                    continue;
                }
                newArgs[i] = v;
            }
        }
        return result;
    }

    public static Constructor getConvertedConstructorAndArgs(OgnlContext context, Object target, List constructors, Object[] args, Object[] newArgs) {
        Constructor result = null;
        TypeConverter converter = context.getTypeConverter();
        if (converter != null && constructors != null) {
            int icount = constructors.size();
            for (int i = 0; result == null && i < icount; ++i) {
                Class[] parameterTypes;
                Constructor ctor = (Constructor)constructors.get(i);
                if (!OgnlRuntime.getConvertedTypes(context, target, ctor, null, parameterTypes = OgnlRuntime.getParameterTypes(ctor), args, newArgs)) continue;
                result = ctor;
            }
        }
        return result;
    }

    public static Method getAppropriateMethod(OgnlContext context, Object source, Object target, String propertyName, String methodName, List methods, Object[] args, Object[] actualArgs) {
        Method result = null;
        if (methods != null) {
            Class[] argClasses;
            MatchingMethod mm;
            Class typeClass;
            Class clazz = typeClass = target != null ? target.getClass() : null;
            if (typeClass == null && source != null && Class.class.isInstance(source)) {
                typeClass = (Class)source;
            }
            if ((mm = OgnlRuntime.findBestMethod(methods, typeClass, methodName, argClasses = OgnlRuntime.getArgClasses(args))) != null) {
                result = mm.mMethod;
                Class[] mParameterTypes = mm.mParameterTypes;
                System.arraycopy(args, 0, actualArgs, 0, args.length);
                if (actualArgs.length > 0) {
                    for (int j = 0; j < mParameterTypes.length; ++j) {
                        Class type = mParameterTypes[j];
                        if (!mm.report.conversionNeeded[j] && (!type.isPrimitive() || actualArgs[j] != null)) continue;
                        actualArgs[j] = OgnlRuntime.getConvertedType(context, source, result, propertyName, args[j], type);
                    }
                }
            }
        }
        if (result == null) {
            result = OgnlRuntime.getConvertedMethodAndArgs(context, target, propertyName, methods, args, actualArgs);
        }
        return result;
    }

    public static Method getConvertedMethodAndArgs(OgnlContext context, Object target, String propertyName, List methods, Object[] args, Object[] newArgs) {
        Method result = null;
        TypeConverter converter = context.getTypeConverter();
        if (converter != null && methods != null) {
            int icount = methods.size();
            for (int i = 0; result == null && i < icount; ++i) {
                Class[] parameterTypes;
                Method m = (Method)methods.get(i);
                if (!OgnlRuntime.getConvertedTypes(context, target, m, propertyName, parameterTypes = OgnlRuntime.findParameterTypes(target != null ? target.getClass() : null, m), args, newArgs)) continue;
                result = m;
            }
        }
        return result;
    }

    private static MatchingMethod findBestMethod(List methods, Class typeClass, String name, Class[] argClasses) {
        MatchingMethod mm = null;
        IllegalArgumentException failure = null;
        int icount = methods.size();
        for (int i = 0; i < icount; ++i) {
            Method m = (Method)methods.get(i);
            Class[] mParameterTypes = OgnlRuntime.findParameterTypes(typeClass, m);
            ArgsCompatbilityReport report = OgnlRuntime.areArgsCompatible(argClasses, mParameterTypes, m);
            if (report == null) continue;
            String methodName = m.getName();
            int score = report.score;
            if (!name.equals(methodName)) {
                score = name.equalsIgnoreCase(methodName) ? (score += 200) : (methodName.toLowerCase().endsWith(name.toLowerCase()) ? (score += 500) : (score += 5000));
            }
            if (mm == null || mm.score > score) {
                mm = new MatchingMethod(m, score, report, mParameterTypes);
                failure = null;
                continue;
            }
            if (mm.score != score) continue;
            if (Arrays.equals(mm.mMethod.getParameterTypes(), m.getParameterTypes()) && mm.mMethod.getName().equals(m.getName())) {
                if (Modifier.isPublic(mm.mMethod.getDeclaringClass().getModifiers()) || !Modifier.isPublic(m.getDeclaringClass().getModifiers())) continue;
                mm = new MatchingMethod(m, score, report, mParameterTypes);
                failure = null;
                continue;
            }
            if (m.isVarArgs() || mm.mMethod.isVarArgs()) {
                if (m.isVarArgs() && !mm.mMethod.isVarArgs()) continue;
                if (!m.isVarArgs() && mm.mMethod.isVarArgs()) {
                    mm = new MatchingMethod(m, score, report, mParameterTypes);
                    failure = null;
                    continue;
                }
                System.err.println("Two vararg methods with same score(" + score + "): \"" + mm.mMethod + "\" and \"" + m + "\" please report!");
                continue;
            }
            int scoreCurr = 0;
            int scoreOther = 0;
            for (int j = 0; j < argClasses.length; ++j) {
                Class argClass = argClasses[j];
                Class mcClass = mm.mParameterTypes[j];
                Class moClass = mParameterTypes[j];
                if (argClass == null) {
                    if (mcClass == moClass) continue;
                    if (mcClass.isAssignableFrom(moClass)) {
                        scoreOther += 1000;
                        continue;
                    }
                    if (moClass.isAssignableFrom(moClass)) {
                        scoreCurr += 1000;
                        continue;
                    }
                    failure = new IllegalArgumentException("Can't decide wich method to use: \"" + mm.mMethod + "\" or \"" + m + "\"");
                    continue;
                }
                if (mcClass == moClass) continue;
                if (mcClass == argClass) {
                    scoreOther += 100;
                    continue;
                }
                if (moClass == argClass) {
                    scoreCurr += 100;
                    continue;
                }
                if (mcClass.isAssignableFrom(moClass)) {
                    scoreOther += 50;
                    continue;
                }
                if (moClass.isAssignableFrom(moClass)) {
                    scoreCurr += 50;
                    continue;
                }
                failure = new IllegalArgumentException("Can't decide wich method to use: \"" + mm.mMethod + "\" or \"" + m + "\"");
            }
            if (scoreCurr == scoreOther) {
                boolean otherIsAbstract;
                boolean currentIsAbstract;
                if (failure != null || (currentIsAbstract = Modifier.isAbstract(mm.mMethod.getModifiers())) ^ (otherIsAbstract = Modifier.isAbstract(m.getModifiers()))) continue;
                System.err.println("Two methods with same score(" + score + "): \"" + mm.mMethod + "\" and \"" + m + "\" please report!");
                continue;
            }
            if (scoreCurr <= scoreOther) continue;
            mm = new MatchingMethod(m, score, report, mParameterTypes);
            failure = null;
        }
        if (failure != null) {
            throw failure;
        }
        return mm;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Object callAppropriateMethod(OgnlContext context, Object source, Object target, String methodName, String propertyName, List methods, Object[] args) throws MethodFailedException {
        Throwable reason = null;
        Object[] actualArgs = _objectArrayPool.create(args.length);
        try {
            Method method = OgnlRuntime.getAppropriateMethod(context, source, target, propertyName, methodName, methods, args, actualArgs);
            if (method == null || !OgnlRuntime.isMethodAccessible(context, source, method, propertyName)) {
                StringBuffer buffer = new StringBuffer();
                String className = "";
                if (target != null) {
                    className = target.getClass().getName() + ".";
                }
                int ilast = args.length - 1;
                for (int i = 0; i <= ilast; ++i) {
                    Object arg = args[i];
                    buffer.append(arg == null ? NULL_STRING : arg.getClass().getName());
                    if (i >= ilast) continue;
                    buffer.append(", ");
                }
                throw new NoSuchMethodException(className + methodName + "(" + buffer + ")");
            }
            Object[] convertedArgs = actualArgs;
            if (method.isVarArgs()) {
                Class<?>[] parmTypes = method.getParameterTypes();
                for (int i = 0; i < parmTypes.length; ++i) {
                    Object[] varArgs;
                    if (!parmTypes[i].isArray()) continue;
                    convertedArgs = new Object[i + 1];
                    if (actualArgs.length > 0) {
                        System.arraycopy(actualArgs, 0, convertedArgs, 0, convertedArgs.length);
                    }
                    if (actualArgs.length > i) {
                        ArrayList<Object> varArgsList = new ArrayList<Object>();
                        for (int j = i; j < actualArgs.length; ++j) {
                            if (actualArgs[j] == null) continue;
                            varArgsList.add(actualArgs[j]);
                        }
                        varArgs = actualArgs.length == 1 ? (Object[])Array.newInstance(args[0].getClass(), 1) : (Object[])Array.newInstance(parmTypes[i].getComponentType(), varArgsList.size());
                        System.arraycopy(varArgsList.toArray(), 0, varArgs, 0, varArgs.length);
                    } else {
                        varArgs = new Object[]{};
                    }
                    if (actualArgs.length == 1 && args[0].getClass().isArray()) {
                        convertedArgs = varArgs;
                        break;
                    }
                    convertedArgs[i] = varArgs;
                    break;
                }
            }
            Object object = OgnlRuntime.invokeMethod(target, method, convertedArgs);
            return object;
        }
        catch (NoSuchMethodException e) {
            reason = e;
        }
        catch (IllegalAccessException e) {
            reason = e;
        }
        catch (InvocationTargetException e) {
            reason = e.getTargetException();
        }
        finally {
            _objectArrayPool.recycle(actualArgs);
        }
        throw new MethodFailedException(source, methodName, reason);
    }

    public static Object callStaticMethod(OgnlContext context, String className, String methodName, Object[] args) throws OgnlException {
        try {
            Class targetClass = OgnlRuntime.classForName(context, className);
            if (targetClass == null) {
                throw new ClassNotFoundException("Unable to resolve class with name " + className);
            }
            MethodAccessor ma = OgnlRuntime.getMethodAccessor(targetClass);
            return ma.callStaticMethod(context, targetClass, methodName, args);
        }
        catch (ClassNotFoundException ex) {
            throw new MethodFailedException(className, methodName, ex);
        }
    }

    public static Object callMethod(OgnlContext context, Object target, String methodName, String propertyName, Object[] args) throws OgnlException {
        return OgnlRuntime.callMethod(context, target, methodName == null ? propertyName : methodName, args);
    }

    public static Object callMethod(OgnlContext context, Object target, String methodName, Object[] args) throws OgnlException {
        if (target == null) {
            throw new NullPointerException("target is null for method " + methodName);
        }
        return OgnlRuntime.getMethodAccessor(target.getClass()).callMethod(context, target, methodName, args);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Object callConstructor(OgnlContext context, String className, Object[] args) throws OgnlException {
        Throwable reason = null;
        Object[] actualArgs = args;
        try {
            Constructor ctor = null;
            Class[] ctorParameterTypes = null;
            Class target = OgnlRuntime.classForName(context, className);
            List constructors = OgnlRuntime.getConstructors(target);
            int icount = constructors.size();
            for (int i = 0; i < icount; ++i) {
                Constructor c = (Constructor)constructors.get(i);
                Class[] cParameterTypes = OgnlRuntime.getParameterTypes(c);
                if (!OgnlRuntime.areArgsCompatible(args, cParameterTypes) || ctor != null && !OgnlRuntime.isMoreSpecific(cParameterTypes, ctorParameterTypes)) continue;
                ctor = c;
                ctorParameterTypes = cParameterTypes;
            }
            if (ctor == null && (ctor = OgnlRuntime.getConvertedConstructorAndArgs(context, target, constructors, args, actualArgs = _objectArrayPool.create(args.length))) == null) {
                throw new NoSuchMethodException();
            }
            if (!context.getMemberAccess().isAccessible(context, target, ctor, null)) {
                throw new IllegalAccessException("access denied to " + target.getName() + "()");
            }
            Object t = ctor.newInstance(actualArgs);
            return t;
        }
        catch (ClassNotFoundException e) {
            reason = e;
        }
        catch (NoSuchMethodException e) {
            reason = e;
        }
        catch (IllegalAccessException e) {
            reason = e;
        }
        catch (InvocationTargetException e) {
            reason = e.getTargetException();
        }
        catch (InstantiationException e) {
            reason = e;
        }
        finally {
            if (actualArgs != args) {
                _objectArrayPool.recycle(actualArgs);
            }
        }
        throw new MethodFailedException(className, "new", reason);
    }

    @Deprecated
    public static final Object getMethodValue(OgnlContext context, Object target, String propertyName) throws OgnlException, IllegalAccessException, NoSuchMethodException, IntrospectionException {
        return OgnlRuntime.getMethodValue(context, target, propertyName, false);
    }

    public static final Object getMethodValue(OgnlContext context, Object target, String propertyName, boolean checkAccessAndExistence) throws OgnlException, IllegalAccessException, NoSuchMethodException, IntrospectionException {
        Object result = null;
        Method m = OgnlRuntime.getGetMethod(context, target == null ? null : target.getClass(), propertyName);
        if (m == null) {
            m = OgnlRuntime.getReadMethod(target == null ? null : target.getClass(), propertyName, null);
        }
        if (checkAccessAndExistence && (m == null || !context.getMemberAccess().isAccessible(context, target, m, propertyName))) {
            result = NotFound;
        }
        if (result == null) {
            if (m != null) {
                try {
                    result = OgnlRuntime.invokeMethod(target, m, NoArguments);
                }
                catch (InvocationTargetException ex) {
                    throw new OgnlException(propertyName, ex.getTargetException());
                }
            } else {
                throw new NoSuchMethodException(propertyName);
            }
        }
        return result;
    }

    @Deprecated
    public static boolean setMethodValue(OgnlContext context, Object target, String propertyName, Object value) throws OgnlException, IllegalAccessException, NoSuchMethodException, IntrospectionException {
        return OgnlRuntime.setMethodValue(context, target, propertyName, value, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean setMethodValue(OgnlContext context, Object target, String propertyName, Object value, boolean checkAccessAndExistence) throws OgnlException, IllegalAccessException, NoSuchMethodException, IntrospectionException {
        boolean result = true;
        Method m = OgnlRuntime.getSetMethod(context, target == null ? null : target.getClass(), propertyName);
        if (checkAccessAndExistence && (m == null || !context.getMemberAccess().isAccessible(context, target, m, propertyName))) {
            result = false;
        }
        if (result) {
            if (m != null) {
                Object[] args = _objectArrayPool.create(value);
                try {
                    OgnlRuntime.callAppropriateMethod(context, target, target, m.getName(), propertyName, Collections.nCopies(1, m), args);
                }
                finally {
                    _objectArrayPool.recycle(args);
                }
            } else {
                result = false;
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static List getConstructors(Class targetClass) {
        List<Constructor<?>> result = (List<Constructor<?>>)_constructorCache.get(targetClass);
        if (result == null) {
            ClassCache classCache = _constructorCache;
            synchronized (classCache) {
                result = (List)_constructorCache.get(targetClass);
                if (result == null) {
                    result = Arrays.asList(targetClass.getConstructors());
                    _constructorCache.put(targetClass, result);
                }
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Map getMethods(Class targetClass, boolean staticMethods) {
        ClassCache cache = staticMethods ? _staticMethodCache : _instanceMethodCache;
        HashMap result = (HashMap)cache.get(targetClass);
        if (result == null) {
            ClassCache classCache = cache;
            synchronized (classCache) {
                result = (Map)cache.get(targetClass);
                if (result == null) {
                    result = new HashMap(23);
                    OgnlRuntime.collectMethods(targetClass, result, staticMethods);
                    cache.put(targetClass, result);
                }
            }
        }
        return result;
    }

    private static void collectMethods(Class c, Map result, boolean staticMethods) {
        Method[] ma;
        try {
            ma = c.getDeclaredMethods();
        }
        catch (SecurityException ignored) {
            ma = c.getMethods();
        }
        int icount = ma.length;
        for (int i = 0; i < icount; ++i) {
            if (!OgnlRuntime.isMethodCallable_BridgeOrNonSynthetic(ma[i]) || Modifier.isStatic(ma[i].getModifiers()) != staticMethods) continue;
            OgnlRuntime.addMethodToResult(result, ma[i]);
        }
        Class superclass = c.getSuperclass();
        if (superclass != null) {
            OgnlRuntime.collectMethods(superclass, result, staticMethods);
        }
        for (Class<?> iface : c.getInterfaces()) {
            OgnlRuntime.collectMethods(iface, result, staticMethods);
        }
    }

    private static void addMethodToResult(Map result, Method method) {
        ArrayList<Method> ml = (ArrayList<Method>)result.get(method.getName());
        if (ml == null) {
            ml = new ArrayList<Method>();
            result.put(method.getName(), ml);
        }
        ml.add(method);
    }

    private static boolean isDefaultMethod(Method method) {
        return (method.getModifiers() & 0x409) == 1 && method.getDeclaringClass().isInterface();
    }

    private static boolean isNonDefaultPublicInterfaceMethod(Method method) {
        return (method.getModifiers() & 0x409) == 1025 && method.getDeclaringClass().isInterface();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Map getAllMethods(Class targetClass, boolean staticMethods) {
        ClassCache cache = staticMethods ? _staticMethodCache : _instanceMethodCache;
        HashMap<String, ArrayList<Method>> result = (HashMap<String, ArrayList<Method>>)cache.get(targetClass);
        if (result == null) {
            ClassCache classCache = cache;
            synchronized (classCache) {
                result = (Map)cache.get(targetClass);
                if (result == null) {
                    result = new HashMap<String, ArrayList<Method>>(23);
                    for (Class c = targetClass; c != null; c = c.getSuperclass()) {
                        Method[] ma = c.getMethods();
                        int icount = ma.length;
                        for (int i = 0; i < icount; ++i) {
                            if (!OgnlRuntime.isMethodCallable(ma[i]) || Modifier.isStatic(ma[i].getModifiers()) != staticMethods) continue;
                            ArrayList<Method> ml = (ArrayList<Method>)result.get(ma[i].getName());
                            if (ml == null) {
                                ml = new ArrayList<Method>();
                                result.put(ma[i].getName(), ml);
                            }
                            ml.add(ma[i]);
                        }
                    }
                    cache.put(targetClass, result);
                }
            }
        }
        return result;
    }

    public static List getMethods(Class targetClass, String name, boolean staticMethods) {
        return (List)OgnlRuntime.getMethods(targetClass, staticMethods).get(name);
    }

    public static List getAllMethods(Class targetClass, String name, boolean staticMethods) {
        return (List)OgnlRuntime.getAllMethods(targetClass, staticMethods).get(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Map getFields(Class targetClass) {
        HashMap<String, Field> result = (HashMap<String, Field>)_fieldCache.get(targetClass);
        if (result == null) {
            ClassCache classCache = _fieldCache;
            synchronized (classCache) {
                result = (Map)_fieldCache.get(targetClass);
                if (result == null) {
                    Field[] fa;
                    result = new HashMap<String, Field>(23);
                    try {
                        fa = targetClass.getDeclaredFields();
                    }
                    catch (SecurityException ignored) {
                        fa = targetClass.getFields();
                    }
                    for (int i = 0; i < fa.length; ++i) {
                        result.put(fa[i].getName(), fa[i]);
                    }
                    _fieldCache.put(targetClass, result);
                }
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Field getField(Class inClass, String name) {
        Field result = null;
        Object o = OgnlRuntime.getFields(inClass).get(name);
        if (o == null) {
            ClassCache classCache = _fieldCache;
            synchronized (classCache) {
                o = OgnlRuntime.getFields(inClass).get(name);
                if (o == null) {
                    _superclasses.clear();
                    for (Class sc = inClass; sc != null && (o = OgnlRuntime.getFields(sc).get(name)) != NotFound; sc = sc.getSuperclass()) {
                        _superclasses.add(sc);
                        result = (Field)o;
                        if (result != null) break;
                    }
                    int icount = _superclasses.size();
                    for (int i = 0; i < icount; ++i) {
                        OgnlRuntime.getFields((Class)_superclasses.get(i)).put(name, result == null ? NotFound : result);
                    }
                } else if (o instanceof Field) {
                    result = (Field)o;
                } else if (result == NotFound) {
                    result = null;
                }
            }
        } else if (o instanceof Field) {
            result = (Field)o;
        } else if (result == NotFound) {
            result = null;
        }
        return result;
    }

    @Deprecated
    public static Object getFieldValue(OgnlContext context, Object target, String propertyName) throws NoSuchFieldException {
        return OgnlRuntime.getFieldValue(context, target, propertyName, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Object getFieldValue(OgnlContext context, Object target, String propertyName, boolean checkAccessAndExistence) throws NoSuchFieldException {
        Object result;
        block9: {
            result = null;
            Field f = OgnlRuntime.getField(target == null ? null : target.getClass(), propertyName);
            if (checkAccessAndExistence && (f == null || !context.getMemberAccess().isAccessible(context, target, f, propertyName))) {
                result = NotFound;
            }
            if (result == null) {
                if (f == null) {
                    throw new NoSuchFieldException(propertyName);
                }
                try {
                    if (!Modifier.isStatic(f.getModifiers())) {
                        Object state = context.getMemberAccess().setup(context, target, f, propertyName);
                        try {
                            result = f.get(target);
                            break block9;
                        }
                        finally {
                            context.getMemberAccess().restore(context, target, f, propertyName, state);
                        }
                    }
                    throw new NoSuchFieldException(propertyName);
                }
                catch (IllegalAccessException ex) {
                    throw new NoSuchFieldException(propertyName);
                }
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean setFieldValue(OgnlContext context, Object target, String propertyName, Object value) throws OgnlException {
        boolean result;
        block6: {
            result = false;
            try {
                int fModifiers;
                Field f = OgnlRuntime.getField(target == null ? null : target.getClass(), propertyName);
                if (f == null || Modifier.isStatic(fModifiers = f.getModifiers()) || Modifier.isFinal(fModifiers)) break block6;
                Object state = context.getMemberAccess().setup(context, target, f, propertyName);
                try {
                    if (OgnlRuntime.isTypeCompatible(value, f.getType()) || (value = OgnlRuntime.getConvertedType(context, target, f, propertyName, value, f.getType())) != null) {
                        f.set(target, value);
                        result = true;
                    }
                }
                finally {
                    context.getMemberAccess().restore(context, target, f, propertyName, state);
                }
            }
            catch (IllegalAccessException ex) {
                throw new NoSuchPropertyException(target, propertyName, ex);
            }
        }
        return result;
    }

    public static boolean isFieldAccessible(OgnlContext context, Object target, Class inClass, String propertyName) {
        return OgnlRuntime.isFieldAccessible(context, target, OgnlRuntime.getField(inClass, propertyName), propertyName);
    }

    public static boolean isFieldAccessible(OgnlContext context, Object target, Field field, String propertyName) {
        return context.getMemberAccess().isAccessible(context, target, field, propertyName);
    }

    public static boolean hasField(OgnlContext context, Object target, Class inClass, String propertyName) {
        Field f = OgnlRuntime.getField(inClass, propertyName);
        return f != null && OgnlRuntime.isFieldAccessible(context, target, f, propertyName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Object getStaticField(OgnlContext context, String className, String fieldName) throws OgnlException {
        Exception reason = null;
        try {
            Field f;
            Class c = OgnlRuntime.classForName(context, className);
            if (c == null) {
                throw new OgnlException("Unable to find class " + className + " when resolving field name of " + fieldName);
            }
            if (fieldName.equals("class")) {
                return c;
            }
            if (c.isEnum()) {
                try {
                    return Enum.valueOf(c, fieldName);
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    // empty catch block
                }
            }
            if ((f = OgnlRuntime.getField(c, fieldName)) == null) {
                throw new NoSuchFieldException(fieldName);
            }
            if (!Modifier.isStatic(f.getModifiers())) {
                throw new OgnlException("Field " + fieldName + " of class " + className + " is not static");
            }
            Object result = null;
            if (context.getMemberAccess().isAccessible(context, null, f, null)) {
                Object state = context.getMemberAccess().setup(context, null, f, null);
                try {
                    result = f.get(null);
                }
                finally {
                    context.getMemberAccess().restore(context, null, f, null, state);
                }
            } else {
                throw new IllegalAccessException("Access to " + fieldName + " of class " + className + " is forbidden");
            }
            return result;
        }
        catch (ClassNotFoundException e) {
            reason = e;
        }
        catch (NoSuchFieldException e) {
            reason = e;
        }
        catch (SecurityException e) {
            reason = e;
        }
        catch (IllegalAccessException e) {
            reason = e;
        }
        throw new OgnlException("Could not get static field " + fieldName + " from class " + className, reason);
    }

    private static String capitalizeBeanPropertyName(String propertyName) {
        if (propertyName.length() == 1) {
            return propertyName.toUpperCase();
        }
        if (propertyName.startsWith(GET_PREFIX) && propertyName.endsWith("()") && Character.isUpperCase(propertyName.substring(3, 4).charAt(0))) {
            return propertyName;
        }
        if (propertyName.startsWith(SET_PREFIX) && propertyName.endsWith(")") && Character.isUpperCase(propertyName.substring(3, 4).charAt(0))) {
            return propertyName;
        }
        if (propertyName.startsWith(IS_PREFIX) && propertyName.endsWith("()") && Character.isUpperCase(propertyName.substring(2, 3).charAt(0))) {
            return propertyName;
        }
        char first = propertyName.charAt(0);
        char second = propertyName.charAt(1);
        if (Character.isLowerCase(first) && Character.isUpperCase(second)) {
            return propertyName;
        }
        char[] chars = propertyName.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static List getDeclaredMethods(Class targetClass, String propertyName, boolean findSets) {
        ArrayList result = null;
        ClassCache cache = _declaredMethods[findSets ? 0 : 1];
        HashMap propertyCache = (HashMap)cache.get(targetClass);
        if (propertyCache == null || (result = (ArrayList)propertyCache.get(propertyName)) == null) {
            ClassCache classCache = cache;
            synchronized (classCache) {
                propertyCache = (Map)cache.get(targetClass);
                if (propertyCache == null || (result = (List)propertyCache.get(propertyName)) == null) {
                    String baseName = OgnlRuntime.capitalizeBeanPropertyName(propertyName);
                    result = new ArrayList();
                    OgnlRuntime.collectAccessors(targetClass, baseName, result, findSets);
                    if (propertyCache == null) {
                        propertyCache = new HashMap(101);
                        cache.put(targetClass, propertyCache);
                    }
                    propertyCache.put(propertyName, result.isEmpty() ? NotFoundList : result);
                    return result.isEmpty() ? null : result;
                }
            }
        }
        return result == NotFoundList ? null : result;
    }

    private static void collectAccessors(Class c, String baseName, List result, boolean findSets) {
        Method[] methods;
        try {
            methods = c.getDeclaredMethods();
        }
        catch (SecurityException ignored) {
            methods = c.getMethods();
        }
        for (int i = 0; i < methods.length; ++i) {
            if (c.isInterface()) {
                if (!OgnlRuntime.isDefaultMethod(methods[i]) && !OgnlRuntime.isNonDefaultPublicInterfaceMethod(methods[i])) continue;
                OgnlRuntime.addIfAccessor(result, methods[i], baseName, findSets);
                continue;
            }
            if (!OgnlRuntime.isMethodCallable(methods[i])) continue;
            OgnlRuntime.addIfAccessor(result, methods[i], baseName, findSets);
        }
        Class superclass = c.getSuperclass();
        if (superclass != null) {
            OgnlRuntime.collectAccessors(superclass, baseName, result, findSets);
        }
        for (Class<?> iface : c.getInterfaces()) {
            OgnlRuntime.collectAccessors(iface, baseName, result, findSets);
        }
    }

    private static void addIfAccessor(List result, Method method, String baseName, boolean findSets) {
        String ms = method.getName();
        if (ms.endsWith(baseName)) {
            boolean isSet = false;
            boolean isIs = false;
            isSet = ms.startsWith(SET_PREFIX);
            if (isSet || ms.startsWith(GET_PREFIX) || (isIs = ms.startsWith(IS_PREFIX))) {
                int prefixLength;
                int n = prefixLength = isIs ? 2 : 3;
                if (isSet == findSets && baseName.length() == ms.length() - prefixLength) {
                    result.add(method);
                }
            }
        }
    }

    static boolean isMethodCallable(Method m) {
        return !m.isSynthetic() && !m.isBridge();
    }

    static boolean isMethodCallable_BridgeOrNonSynthetic(Method m) {
        return !m.isSynthetic() || m.isBridge();
    }

    public static Method getGetMethod(OgnlContext context, Class targetClass, String propertyName) throws IntrospectionException, OgnlException {
        Method method = cacheGetMethod.get(targetClass, propertyName);
        if (method == ClassPropertyMethodCache.NULL_REPLACEMENT) {
            return null;
        }
        if (method != null) {
            return method;
        }
        method = OgnlRuntime._getGetMethod(context, targetClass, propertyName);
        cacheGetMethod.put(targetClass, propertyName, method);
        return method;
    }

    private static Method _getGetMethod(OgnlContext context, Class targetClass, String propertyName) throws IntrospectionException, OgnlException {
        Method result = null;
        List methods = OgnlRuntime.getDeclaredMethods(targetClass, propertyName, false);
        if (methods != null) {
            Method firstGetter = null;
            Method firstPublicGetter = null;
            Method firstNonDefaultPublicInterfaceGetter = null;
            int icount = methods.size();
            for (int i = 0; i < icount; ++i) {
                Method m = (Method)methods.get(i);
                Class[] mParameterTypes = OgnlRuntime.findParameterTypes(targetClass, m);
                if (mParameterTypes.length != 0) continue;
                boolean declaringClassIsPublic = Modifier.isPublic(m.getDeclaringClass().getModifiers());
                if (firstGetter == null) {
                    firstGetter = m;
                    if (_useFirstMatchGetSetLookup) break;
                }
                if (firstPublicGetter == null && Modifier.isPublic(m.getModifiers()) && declaringClassIsPublic) {
                    firstPublicGetter = m;
                    break;
                }
                if (firstNonDefaultPublicInterfaceGetter != null || !OgnlRuntime.isNonDefaultPublicInterfaceMethod(m) || !declaringClassIsPublic) continue;
                firstNonDefaultPublicInterfaceGetter = m;
            }
            result = firstPublicGetter != null ? firstPublicGetter : (firstNonDefaultPublicInterfaceGetter != null ? firstNonDefaultPublicInterfaceGetter : firstGetter);
        }
        return result;
    }

    public static boolean isMethodAccessible(OgnlContext context, Object target, Method method, String propertyName) {
        return method != null && context.getMemberAccess().isAccessible(context, target, method, propertyName);
    }

    public static boolean hasGetMethod(OgnlContext context, Object target, Class targetClass, String propertyName) throws IntrospectionException, OgnlException {
        return OgnlRuntime.isMethodAccessible(context, target, OgnlRuntime.getGetMethod(context, targetClass, propertyName), propertyName);
    }

    public static Method getSetMethod(OgnlContext context, Class targetClass, String propertyName) throws IntrospectionException, OgnlException {
        Method method = cacheSetMethod.get(targetClass, propertyName);
        if (method == ClassPropertyMethodCache.NULL_REPLACEMENT) {
            return null;
        }
        if (method != null) {
            return method;
        }
        method = OgnlRuntime._getSetMethod(context, targetClass, propertyName);
        cacheSetMethod.put(targetClass, propertyName, method);
        return method;
    }

    private static Method _getSetMethod(OgnlContext context, Class targetClass, String propertyName) throws IntrospectionException, OgnlException {
        Method result = null;
        List methods = OgnlRuntime.getDeclaredMethods(targetClass, propertyName, true);
        if (methods != null) {
            Method firstSetter = null;
            Method firstPublicSetter = null;
            Method firstNonDefaultPublicInterfaceSetter = null;
            int icount = methods.size();
            for (int i = 0; i < icount; ++i) {
                Method m = (Method)methods.get(i);
                Class[] mParameterTypes = OgnlRuntime.findParameterTypes(targetClass, m);
                if (mParameterTypes.length != 1) continue;
                boolean declaringClassIsPublic = Modifier.isPublic(m.getDeclaringClass().getModifiers());
                if (firstSetter == null) {
                    firstSetter = m;
                    if (_useFirstMatchGetSetLookup) break;
                }
                if (firstPublicSetter == null && Modifier.isPublic(m.getModifiers()) && declaringClassIsPublic) {
                    firstPublicSetter = m;
                    break;
                }
                if (firstNonDefaultPublicInterfaceSetter != null || !OgnlRuntime.isNonDefaultPublicInterfaceMethod(m) || !declaringClassIsPublic) continue;
                firstNonDefaultPublicInterfaceSetter = m;
            }
            result = firstPublicSetter != null ? firstPublicSetter : (firstNonDefaultPublicInterfaceSetter != null ? firstNonDefaultPublicInterfaceSetter : firstSetter);
        }
        return result;
    }

    public static final boolean hasSetMethod(OgnlContext context, Object target, Class targetClass, String propertyName) throws IntrospectionException, OgnlException {
        return OgnlRuntime.isMethodAccessible(context, target, OgnlRuntime.getSetMethod(context, targetClass, propertyName), propertyName);
    }

    public static final boolean hasGetProperty(OgnlContext context, Object target, Object oname) throws IntrospectionException, OgnlException {
        Class<?> targetClass = target == null ? null : target.getClass();
        String name = oname.toString();
        return OgnlRuntime.hasGetMethod(context, target, targetClass, name) || OgnlRuntime.hasField(context, target, targetClass, name);
    }

    public static final boolean hasSetProperty(OgnlContext context, Object target, Object oname) throws IntrospectionException, OgnlException {
        Class<?> targetClass = target == null ? null : target.getClass();
        String name = oname.toString();
        return OgnlRuntime.hasSetMethod(context, target, targetClass, name) || OgnlRuntime.hasField(context, target, targetClass, name);
    }

    private static final boolean indexMethodCheck(List methods) {
        boolean result = false;
        if (methods.size() > 0) {
            Method fm = (Method)methods.get(0);
            Class[] fmpt = OgnlRuntime.getParameterTypes(fm);
            int fmpc = fmpt.length;
            Class<?> lastMethodClass = fm.getDeclaringClass();
            result = true;
            for (int i = 1; result && i < methods.size(); ++i) {
                Method m = (Method)methods.get(i);
                Class<?> c = m.getDeclaringClass();
                if (lastMethodClass == c) {
                    result = false;
                } else {
                    Class[] mpt = OgnlRuntime.getParameterTypes(fm);
                    int mpc = fmpt.length;
                    if (fmpc != mpc) {
                        result = false;
                    }
                    for (int j = 0; j < fmpc; ++j) {
                        if (fmpt[j] == mpt[j]) continue;
                        result = false;
                        break;
                    }
                }
                lastMethodClass = c;
            }
        }
        return result;
    }

    static void findObjectIndexedPropertyDescriptors(Class targetClass, Map intoMap) throws OgnlException {
        List methods;
        Map allMethods = OgnlRuntime.getMethods(targetClass, false);
        HashMap pairs = new HashMap(101);
        for (String methodName : allMethods.keySet()) {
            List<Method> pair;
            methods = (List)allMethods.get(methodName);
            if (!OgnlRuntime.indexMethodCheck(methods)) continue;
            boolean isGet = false;
            boolean isSet = false;
            Method m = (Method)methods.get(0);
            isSet = methodName.startsWith(SET_PREFIX);
            if (!isSet && !(isGet = methodName.startsWith(GET_PREFIX)) || methodName.length() <= 3) continue;
            String propertyName = Introspector.decapitalize(methodName.substring(3));
            Class[] parameterTypes = OgnlRuntime.getParameterTypes(m);
            int parameterCount = parameterTypes.length;
            if (isGet && parameterCount == 1 && m.getReturnType() != Void.TYPE) {
                pair = (ArrayList<Method>)pairs.get(propertyName);
                if (pair == null) {
                    pair = new ArrayList<Method>();
                    pairs.put(propertyName, pair);
                }
                pair.add(m);
            }
            if (!isSet || parameterCount != 2 || m.getReturnType() != Void.TYPE) continue;
            pair = (List)pairs.get(propertyName);
            if (pair == null) {
                pair = new ArrayList();
                pairs.put(propertyName, pair);
            }
            pair.add(m);
        }
        for (String propertyName : pairs.keySet()) {
            ObjectIndexedPropertyDescriptor propertyDescriptor;
            methods = (List)pairs.get(propertyName);
            if (methods.size() != 2) continue;
            Method method1 = (Method)methods.get(0);
            Method method2 = (Method)methods.get(1);
            Method setMethod = method1.getParameterTypes().length == 2 ? method1 : method2;
            Method getMethod = setMethod == method1 ? method2 : method1;
            Class<?> keyType = getMethod.getParameterTypes()[0];
            Class<?> propertyType = getMethod.getReturnType();
            if (keyType != setMethod.getParameterTypes()[0] || propertyType != setMethod.getParameterTypes()[1]) continue;
            try {
                propertyDescriptor = new ObjectIndexedPropertyDescriptor(propertyName, propertyType, getMethod, setMethod);
            }
            catch (Exception ex) {
                throw new OgnlException("creating object indexed property descriptor for '" + propertyName + "' in " + targetClass, ex);
            }
            intoMap.put(propertyName, propertyDescriptor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Map getPropertyDescriptors(Class targetClass) throws IntrospectionException, OgnlException {
        HashMap<String, PropertyDescriptor> result = (HashMap<String, PropertyDescriptor>)_propertyDescriptorCache.get(targetClass);
        if (result == null) {
            ClassCache classCache = _propertyDescriptorCache;
            synchronized (classCache) {
                result = (Map)_propertyDescriptorCache.get(targetClass);
                if (result == null) {
                    PropertyDescriptor[] pda = Introspector.getBeanInfo(targetClass).getPropertyDescriptors();
                    result = new HashMap<String, PropertyDescriptor>(101);
                    int icount = pda.length;
                    for (int i = 0; i < icount; ++i) {
                        if (pda[i].getReadMethod() != null && !OgnlRuntime.isMethodCallable(pda[i].getReadMethod())) {
                            pda[i].setReadMethod(OgnlRuntime.findClosestMatchingMethod(targetClass, pda[i].getReadMethod(), pda[i].getName(), pda[i].getPropertyType(), true));
                        }
                        if (pda[i].getWriteMethod() != null && !OgnlRuntime.isMethodCallable(pda[i].getWriteMethod())) {
                            pda[i].setWriteMethod(OgnlRuntime.findClosestMatchingMethod(targetClass, pda[i].getWriteMethod(), pda[i].getName(), pda[i].getPropertyType(), false));
                        }
                        result.put(pda[i].getName(), pda[i]);
                    }
                    OgnlRuntime.findObjectIndexedPropertyDescriptors(targetClass, result);
                    _propertyDescriptorCache.put(targetClass, result);
                }
            }
        }
        return result;
    }

    public static PropertyDescriptor getPropertyDescriptor(Class targetClass, String propertyName) throws IntrospectionException, OgnlException {
        if (targetClass == null) {
            return null;
        }
        return (PropertyDescriptor)OgnlRuntime.getPropertyDescriptors(targetClass).get(propertyName);
    }

    static Method findClosestMatchingMethod(Class targetClass, Method m, String propertyName, Class propertyType, boolean isReadMethod) {
        List methods = OgnlRuntime.getDeclaredMethods(targetClass, propertyName, !isReadMethod);
        if (methods != null) {
            for (Object method1 : methods) {
                Method method = (Method)method1;
                if (!method.getName().equals(m.getName()) || !m.getReturnType().isAssignableFrom(m.getReturnType()) || method.getReturnType() != propertyType || method.getParameterTypes().length != m.getParameterTypes().length) continue;
                return method;
            }
        }
        return m;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static PropertyDescriptor[] getPropertyDescriptorsArray(Class targetClass) throws IntrospectionException {
        PropertyDescriptor[] result = null;
        if (targetClass != null && (result = (PropertyDescriptor[])_propertyDescriptorCache.get(targetClass)) == null) {
            ClassCache classCache = _propertyDescriptorCache;
            synchronized (classCache) {
                result = (PropertyDescriptor[])_propertyDescriptorCache.get(targetClass);
                if (result == null) {
                    result = Introspector.getBeanInfo(targetClass).getPropertyDescriptors();
                    _propertyDescriptorCache.put(targetClass, result);
                }
            }
        }
        return result;
    }

    public static PropertyDescriptor getPropertyDescriptorFromArray(Class targetClass, String name) throws IntrospectionException {
        PropertyDescriptor result = null;
        PropertyDescriptor[] pda = OgnlRuntime.getPropertyDescriptorsArray(targetClass);
        int icount = pda.length;
        for (int i = 0; result == null && i < icount; ++i) {
            if (pda[i].getName().compareTo(name) != 0) continue;
            result = pda[i];
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void setMethodAccessor(Class cls, MethodAccessor accessor) {
        ClassCache classCache = _methodAccessors;
        synchronized (classCache) {
            _methodAccessors.put(cls, accessor);
        }
    }

    public static MethodAccessor getMethodAccessor(Class cls) throws OgnlException {
        MethodAccessor answer = (MethodAccessor)OgnlRuntime.getHandler(cls, _methodAccessors);
        if (answer != null) {
            return answer;
        }
        throw new OgnlException("No method accessor for " + cls);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void setPropertyAccessor(Class cls, PropertyAccessor accessor) {
        ClassCache classCache = _propertyAccessors;
        synchronized (classCache) {
            _propertyAccessors.put(cls, accessor);
        }
    }

    public static PropertyAccessor getPropertyAccessor(Class cls) throws OgnlException {
        PropertyAccessor answer = (PropertyAccessor)OgnlRuntime.getHandler(cls, _propertyAccessors);
        if (answer != null) {
            return answer;
        }
        throw new OgnlException("No property accessor for class " + cls);
    }

    public static ElementsAccessor getElementsAccessor(Class cls) throws OgnlException {
        ElementsAccessor answer = (ElementsAccessor)OgnlRuntime.getHandler(cls, _elementsAccessors);
        if (answer != null) {
            return answer;
        }
        throw new OgnlException("No elements accessor for class " + cls);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void setElementsAccessor(Class cls, ElementsAccessor accessor) {
        ClassCache classCache = _elementsAccessors;
        synchronized (classCache) {
            _elementsAccessors.put(cls, accessor);
        }
    }

    public static NullHandler getNullHandler(Class cls) throws OgnlException {
        NullHandler answer = (NullHandler)OgnlRuntime.getHandler(cls, _nullHandlers);
        if (answer != null) {
            return answer;
        }
        throw new OgnlException("No null handler for class " + cls);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void setNullHandler(Class cls, NullHandler handler) {
        ClassCache classCache = _nullHandlers;
        synchronized (classCache) {
            _nullHandlers.put(cls, handler);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Object getHandler(Class forClass, ClassCache handlers) {
        Object answer = null;
        answer = handlers.get(forClass);
        if (answer == null) {
            ClassCache classCache = handlers;
            synchronized (classCache) {
                answer = handlers.get(forClass);
                if (answer == null) {
                    Class<Object> keyFound;
                    if (forClass.isArray()) {
                        answer = handlers.get(Object[].class);
                        keyFound = null;
                    } else {
                        keyFound = forClass;
                        block3: for (Class c = forClass; c != null; c = c.getSuperclass()) {
                            answer = handlers.get(c);
                            if (answer == null) {
                                for (Class<?> iface : c.getInterfaces()) {
                                    answer = handlers.get(iface);
                                    if (answer == null) {
                                        answer = OgnlRuntime.getHandler(iface, handlers);
                                    }
                                    if (answer == null) continue;
                                    keyFound = iface;
                                    break block3;
                                }
                                continue;
                            }
                            keyFound = c;
                            break;
                        }
                    }
                    if (answer != null && keyFound != forClass) {
                        handlers.put(forClass, answer);
                    }
                }
            }
        }
        return answer;
    }

    public static Object getProperty(OgnlContext context, Object source, Object name) throws OgnlException {
        if (source == null) {
            throw new OgnlException("source is null for getProperty(null, \"" + name + "\")");
        }
        PropertyAccessor accessor = OgnlRuntime.getPropertyAccessor(OgnlRuntime.getTargetClass(source));
        if (accessor == null) {
            throw new OgnlException("No property accessor for " + OgnlRuntime.getTargetClass(source).getName());
        }
        return accessor.getProperty(context, source, name);
    }

    public static void setProperty(OgnlContext context, Object target, Object name, Object value) throws OgnlException {
        if (target == null) {
            throw new OgnlException("target is null for setProperty(null, \"" + name + "\", " + value + ")");
        }
        PropertyAccessor accessor = OgnlRuntime.getPropertyAccessor(OgnlRuntime.getTargetClass(target));
        if (accessor == null) {
            throw new OgnlException("No property accessor for " + OgnlRuntime.getTargetClass(target).getName());
        }
        accessor.setProperty(context, target, name, value);
    }

    public static int getIndexedPropertyType(OgnlContext context, Class sourceClass, String name) throws OgnlException {
        int result = INDEXED_PROPERTY_NONE;
        try {
            PropertyDescriptor pd = OgnlRuntime.getPropertyDescriptor(sourceClass, name);
            if (pd != null) {
                if (pd instanceof IndexedPropertyDescriptor) {
                    result = INDEXED_PROPERTY_INT;
                } else if (pd instanceof ObjectIndexedPropertyDescriptor) {
                    result = INDEXED_PROPERTY_OBJECT;
                }
            }
        }
        catch (Exception ex) {
            throw new OgnlException("problem determining if '" + name + "' is an indexed property", ex);
        }
        return result;
    }

    public static Object getIndexedProperty(OgnlContext context, Object source, String name, Object index) throws OgnlException {
        Object[] args = _objectArrayPool.create(index);
        try {
            Method m;
            PropertyDescriptor pd = OgnlRuntime.getPropertyDescriptor(source == null ? null : source.getClass(), name);
            if (pd instanceof IndexedPropertyDescriptor) {
                m = ((IndexedPropertyDescriptor)pd).getIndexedReadMethod();
            } else if (pd instanceof ObjectIndexedPropertyDescriptor) {
                m = ((ObjectIndexedPropertyDescriptor)pd).getIndexedReadMethod();
            } else {
                throw new OgnlException("property '" + name + "' is not an indexed property");
            }
            Object object = OgnlRuntime.callMethod(context, source, m.getName(), args);
            return object;
        }
        catch (OgnlException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new OgnlException("getting indexed property descriptor for '" + name + "'", ex);
        }
        finally {
            _objectArrayPool.recycle(args);
        }
    }

    public static void setIndexedProperty(OgnlContext context, Object source, String name, Object index, Object value) throws OgnlException {
        Object[] args = _objectArrayPool.create(index, value);
        try {
            Method m;
            PropertyDescriptor pd = OgnlRuntime.getPropertyDescriptor(source == null ? null : source.getClass(), name);
            if (pd instanceof IndexedPropertyDescriptor) {
                m = ((IndexedPropertyDescriptor)pd).getIndexedWriteMethod();
            } else if (pd instanceof ObjectIndexedPropertyDescriptor) {
                m = ((ObjectIndexedPropertyDescriptor)pd).getIndexedWriteMethod();
            } else {
                throw new OgnlException("property '" + name + "' is not an indexed property");
            }
            OgnlRuntime.callMethod(context, source, m.getName(), args);
        }
        catch (OgnlException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new OgnlException("getting indexed property descriptor for '" + name + "'", ex);
        }
        finally {
            _objectArrayPool.recycle(args);
        }
    }

    public static EvaluationPool getEvaluationPool() {
        return _evaluationPool;
    }

    public static ObjectArrayPool getObjectArrayPool() {
        return _objectArrayPool;
    }

    public static void setClassCacheInspector(ClassCacheInspector inspector) {
        _cacheInspector = inspector;
        _propertyDescriptorCache.setClassInspector(_cacheInspector);
        _constructorCache.setClassInspector(_cacheInspector);
        _staticMethodCache.setClassInspector(_cacheInspector);
        _instanceMethodCache.setClassInspector(_cacheInspector);
        _invokePermissionCache.setClassInspector(_cacheInspector);
        _fieldCache.setClassInspector(_cacheInspector);
        _declaredMethods[0].setClassInspector(_cacheInspector);
        _declaredMethods[1].setClassInspector(_cacheInspector);
    }

    public static Method getMethod(OgnlContext context, Class target, String name, Node[] children, boolean includeStatic) throws Exception {
        Class[] parms;
        if (children != null && children.length > 0) {
            parms = new Class[children.length];
            Class currType = context.getCurrentType();
            Class currAccessor = context.getCurrentAccessor();
            Object cast = context.get("_preCast");
            context.setCurrentObject(context.getRoot());
            context.setCurrentType(context.getRoot() != null ? context.getRoot().getClass() : null);
            context.setCurrentAccessor(null);
            context.setPreviousType(null);
            for (int i = 0; i < children.length; ++i) {
                children[i].toGetSourceString(context, context.getRoot());
                parms[i] = context.getCurrentType();
            }
            context.put("_preCast", cast);
            context.setCurrentType(currType);
            context.setCurrentAccessor(currAccessor);
            context.setCurrentObject(target);
        } else {
            parms = EMPTY_CLASS_ARRAY;
        }
        List methods = OgnlRuntime.getMethods(target, name, includeStatic);
        if (methods == null) {
            return null;
        }
        for (int i = 0; i < methods.size(); ++i) {
            Method m = (Method)methods.get(i);
            boolean varArgs = m.isVarArgs();
            if (parms.length != m.getParameterTypes().length && !varArgs) continue;
            Class<?>[] mparms = m.getParameterTypes();
            boolean matched = true;
            for (int p = 0; p < mparms.length; ++p) {
                if (varArgs && mparms[p].isArray()) continue;
                if (parms[p] == null) {
                    matched = false;
                    break;
                }
                if (parms[p] == mparms[p] || mparms[p].isPrimitive() && Character.TYPE != mparms[p] && Byte.TYPE != mparms[p] && Number.class.isAssignableFrom(parms[p]) && OgnlRuntime.getPrimitiveWrapperClass(parms[p]) == mparms[p]) continue;
                matched = false;
                break;
            }
            if (!matched) continue;
            return m;
        }
        return null;
    }

    public static Method getReadMethod(Class target, String name) {
        return OgnlRuntime.getReadMethod(target, name, null);
    }

    public static Method getReadMethod(Class target, String name, Class[] argClasses) {
        try {
            Method ret;
            MatchingMethod mm;
            int i;
            if (name.indexOf(34) >= 0) {
                name = name.replaceAll("\"", "");
            }
            name = name.toLowerCase();
            Method[] methods = target.getMethods();
            ArrayList<Method> candidates = new ArrayList<Method>();
            for (i = 0; i < methods.length; ++i) {
                if (!OgnlRuntime.isMethodCallable_BridgeOrNonSynthetic(methods[i]) || !methods[i].getName().equalsIgnoreCase(name) && !methods[i].getName().toLowerCase().equals(GET_PREFIX + name) && !methods[i].getName().toLowerCase().equals("has" + name) && !methods[i].getName().toLowerCase().equals(IS_PREFIX + name) || methods[i].getName().startsWith(SET_PREFIX)) continue;
                candidates.add(methods[i]);
            }
            if (!candidates.isEmpty() && (mm = OgnlRuntime.findBestMethod(candidates, target, name, argClasses)) != null) {
                return mm.mMethod;
            }
            for (i = 0; i < methods.length; ++i) {
                Method m;
                if (!OgnlRuntime.isMethodCallable_BridgeOrNonSynthetic(methods[i]) || !methods[i].getName().equalsIgnoreCase(name) || methods[i].getName().startsWith(SET_PREFIX) || methods[i].getName().startsWith(GET_PREFIX) || methods[i].getName().startsWith(IS_PREFIX) || methods[i].getName().startsWith("has") || methods[i].getReturnType() == Void.TYPE || candidates.contains(m = methods[i])) continue;
                candidates.add(m);
            }
            if (!candidates.isEmpty() && (mm = OgnlRuntime.findBestMethod(candidates, target, name, argClasses)) != null) {
                return mm.mMethod;
            }
            if (!name.startsWith(GET_PREFIX) && (ret = OgnlRuntime.getReadMethod(target, GET_PREFIX + name, argClasses)) != null) {
                return ret;
            }
            if (!candidates.isEmpty()) {
                int reqArgCount = argClasses == null ? 0 : argClasses.length;
                for (Method m : candidates) {
                    if (m.getParameterTypes().length != reqArgCount) continue;
                    return m;
                }
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return null;
    }

    public static Method getWriteMethod(Class target, String name) {
        return OgnlRuntime.getWriteMethod(target, name, null);
    }

    public static Method getWriteMethod(Class target, String name, Class[] argClasses) {
        try {
            Method ret;
            MatchingMethod mm;
            MatchingMethod mm2;
            if (name.indexOf(34) >= 0) {
                name = name.replaceAll("\"", "");
            }
            BeanInfo info = Introspector.getBeanInfo(target);
            MethodDescriptor[] methods = info.getMethodDescriptors();
            ArrayList<Method> candidates = new ArrayList<Method>();
            for (int i = 0; i < methods.length; ++i) {
                if (!OgnlRuntime.isMethodCallable_BridgeOrNonSynthetic(methods[i].getMethod()) || !methods[i].getName().equalsIgnoreCase(name) && !methods[i].getName().toLowerCase().equals(name.toLowerCase()) && !methods[i].getName().toLowerCase().equals(SET_PREFIX + name.toLowerCase()) || methods[i].getName().startsWith(GET_PREFIX)) continue;
                candidates.add(methods[i].getMethod());
            }
            if (!candidates.isEmpty() && (mm2 = OgnlRuntime.findBestMethod(candidates, target, name, argClasses)) != null) {
                return mm2.mMethod;
            }
            Method[] cmethods = target.getClass().getMethods();
            for (int i = 0; i < cmethods.length; ++i) {
                Method m;
                if (!OgnlRuntime.isMethodCallable_BridgeOrNonSynthetic(cmethods[i]) || !cmethods[i].getName().equalsIgnoreCase(name) && !cmethods[i].getName().toLowerCase().equals(name.toLowerCase()) && !cmethods[i].getName().toLowerCase().equals(SET_PREFIX + name.toLowerCase()) || cmethods[i].getName().startsWith(GET_PREFIX) || candidates.contains(m = methods[i].getMethod())) continue;
                candidates.add(m);
            }
            if (!candidates.isEmpty() && (mm = OgnlRuntime.findBestMethod(candidates, target, name, argClasses)) != null) {
                return mm.mMethod;
            }
            if (!name.startsWith(SET_PREFIX) && (ret = OgnlRuntime.getReadMethod(target, SET_PREFIX + name, argClasses)) != null) {
                return ret;
            }
            if (!candidates.isEmpty()) {
                int reqArgCount = argClasses == null ? 0 : argClasses.length;
                for (Method m : candidates) {
                    if (m.getParameterTypes().length != reqArgCount) continue;
                    return m;
                }
                if (argClasses == null && candidates.size() == 1) {
                    return (Method)candidates.get(0);
                }
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return null;
    }

    public static PropertyDescriptor getProperty(Class target, String name) {
        try {
            BeanInfo info = Introspector.getBeanInfo(target);
            PropertyDescriptor[] pds = info.getPropertyDescriptors();
            for (int i = 0; i < pds.length; ++i) {
                if (!pds[i].getName().equalsIgnoreCase(name) && !pds[i].getName().toLowerCase().equals(name.toLowerCase()) && !pds[i].getName().toLowerCase().endsWith(name.toLowerCase())) continue;
                return pds[i];
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return null;
    }

    public static boolean isBoolean(String expression) {
        if (expression == null) {
            return false;
        }
        return "true".equals(expression) || "false".equals(expression) || "!true".equals(expression) || "!false".equals(expression) || "(true)".equals(expression) || "!(true)".equals(expression) || "(false)".equals(expression) || "!(false)".equals(expression) || expression.startsWith("ognl.OgnlOps");
    }

    public static boolean shouldConvertNumericTypes(OgnlContext context) {
        if (context.getCurrentType() == null || context.getPreviousType() == null) {
            return true;
        }
        if (context.getCurrentType() == context.getPreviousType() && context.getCurrentType().isPrimitive() && context.getPreviousType().isPrimitive()) {
            return false;
        }
        return context.getCurrentType() != null && !context.getCurrentType().isArray() && context.getPreviousType() != null && !context.getPreviousType().isArray();
    }

    public static String getChildSource(OgnlContext context, Object target, Node child) throws OgnlException {
        return OgnlRuntime.getChildSource(context, target, child, false);
    }

    public static String getChildSource(OgnlContext context, Object target, Node child, boolean forceConversion) throws OgnlException {
        String pre = (String)context.get("_currentChain");
        if (pre == null) {
            pre = "";
        }
        try {
            child.getValue(context, target);
        }
        catch (NullPointerException nullPointerException) {
        }
        catch (ArithmeticException e) {
            context.setCurrentType(Integer.TYPE);
            return "0";
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        String source = null;
        try {
            source = child.toGetSourceString(context, target);
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        if (!(ASTConst.class.isInstance(child) || target != null && context.getRoot() == target)) {
            source = pre + source;
        }
        if (context.getRoot() != null) {
            source = ExpressionCompiler.getRootExpression(child, context.getRoot(), context) + source;
            context.setCurrentAccessor(context.getRoot().getClass());
        }
        if (ASTChain.class.isInstance(child)) {
            String cast = (String)context.remove("_preCast");
            if (cast == null) {
                cast = "";
            }
            source = cast + source;
        }
        if (source == null || source.trim().length() < 1) {
            source = "null";
        }
        return source;
    }

    static int detectMajorJavaVersion() {
        int majorVersion = -1;
        try {
            majorVersion = OgnlRuntime.parseMajorJavaVersion(System.getProperty("java.version"));
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (majorVersion == -1) {
            majorVersion = 5;
        }
        return majorVersion;
    }

    static int parseMajorJavaVersion(String versionString) {
        int majorVersion = -1;
        try {
            String[] sections;
            if (versionString != null && versionString.length() > 0 && (sections = versionString.split("[\\.\\-\\+]")).length > 0 && sections[0].length() > 0) {
                int secondSection;
                int firstSection;
                if (sections.length > 1 && sections[1].length() > 0) {
                    firstSection = Integer.parseInt(sections[0]);
                    secondSection = sections[1].matches("\\d+") ? Integer.parseInt(sections[1]) : -1;
                } else {
                    firstSection = Integer.parseInt(sections[0]);
                    secondSection = -1;
                }
                majorVersion = firstSection == 1 && secondSection != -1 ? secondSection : firstSection;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (majorVersion == -1) {
            majorVersion = 5;
        }
        return majorVersion;
    }

    public static boolean getUseJDK9PlusAccessHandlerValue() {
        return _useJDK9PlusAccessHandler;
    }

    public static boolean getUseStricterInvocationValue() {
        return _useStricterInvocation;
    }

    public static boolean getDisableOgnlSecurityManagerOnInitValue() {
        return _disableOgnlSecurityManagerOnInit;
    }

    public static boolean usingJDK9PlusAccessHandler() {
        return _jdk9Plus && _useJDK9PlusAccessHandler;
    }

    public static boolean getUseFirstMatchGetSetLookupValue() {
        return _useFirstMatchGetSetLookup;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        String propertyString2;
        NotFound = new Object();
        NotFoundList = new ArrayList();
        NotFoundMap = new HashMap();
        NoArguments = new Object[0];
        NoArgumentTypes = new Class[0];
        NoConversionPossible = "ognl.NoConversionPossible";
        INDEXED_PROPERTY_NONE = 0;
        INDEXED_PROPERTY_INT = 1;
        INDEXED_PROPERTY_OBJECT = 2;
        NULL_STRING = "" + null;
        HEX_PADDING = new HashMap();
        _jdk15 = false;
        _jdkChecked = false;
        boolean initialFlagState = false;
        try {
            propertyString2 = System.getProperty(USE_JDK9PLUS_ACESS_HANDLER);
            if (propertyString2 != null && propertyString2.length() > 0) {
                initialFlagState = Boolean.parseBoolean(propertyString2);
            }
        }
        catch (Exception propertyString2) {
            // empty catch block
        }
        _useJDK9PlusAccessHandler = initialFlagState;
        initialFlagState = true;
        try {
            propertyString2 = System.getProperty(USE_STRICTER_INVOCATION);
            if (propertyString2 != null && propertyString2.length() > 0) {
                initialFlagState = Boolean.parseBoolean(propertyString2);
            }
        }
        catch (Exception propertyString3) {
            // empty catch block
        }
        _useStricterInvocation = initialFlagState;
        _majorJavaVersion = OgnlRuntime.detectMajorJavaVersion();
        _jdk9Plus = _majorJavaVersion >= 9;
        _accessibleObjectHandler = OgnlRuntime.usingJDK9PlusAccessHandler() ? AccessibleObjectHandlerJDK9Plus.createHandler() : AccessibleObjectHandlerPreJDK9.createHandler();
        Method setAccessibleMethod = null;
        Method setAccessibleMethodArray = null;
        Method systemExitMethod = null;
        Method systemConsoleMethod = null;
        try {
            setAccessibleMethod = AccessibleObject.class.getMethod("setAccessible", Boolean.TYPE);
        }
        catch (NoSuchMethodException noSuchMethodException) {
        }
        catch (SecurityException securityException) {
        }
        finally {
            AO_SETACCESSIBLE_REF = setAccessibleMethod;
        }
        try {
            setAccessibleMethodArray = AccessibleObject.class.getMethod("setAccessible", AccessibleObject[].class, Boolean.TYPE);
        }
        catch (NoSuchMethodException noSuchMethodException) {
        }
        catch (SecurityException securityException) {
        }
        finally {
            AO_SETACCESSIBLE_ARR_REF = setAccessibleMethodArray;
        }
        try {
            systemExitMethod = System.class.getMethod("exit", Integer.TYPE);
        }
        catch (NoSuchMethodException noSuchMethodException) {
        }
        catch (SecurityException securityException) {
        }
        finally {
            SYS_EXIT_REF = systemExitMethod;
        }
        try {
            systemConsoleMethod = System.class.getMethod("console", new Class[0]);
        }
        catch (NoSuchMethodException noSuchMethodException) {
        }
        catch (SecurityException securityException) {
        }
        finally {
            SYS_CONSOLE_REF = systemConsoleMethod;
        }
        initialFlagState = false;
        try {
            propertyString2 = System.getProperty(OGNL_SECURITY_MANAGER);
            if (propertyString2 != null && propertyString2.length() > 0) {
                initialFlagState = OGNL_SM_FORCE_DISABLE_ON_INIT.equalsIgnoreCase(propertyString2);
            }
        }
        catch (Exception propertyString4) {
            // empty catch block
        }
        _disableOgnlSecurityManagerOnInit = initialFlagState;
        initialFlagState = false;
        try {
            propertyString2 = System.getProperty(USE_FIRSTMATCH_GETSET_LOOKUP);
            if (propertyString2 != null && propertyString2.length() > 0) {
                initialFlagState = Boolean.parseBoolean(propertyString2);
            }
        }
        catch (Exception propertyString5) {
            // empty catch block
        }
        _useFirstMatchGetSetLookup = initialFlagState;
        _methodAccessors = new ClassCacheImpl();
        _propertyAccessors = new ClassCacheImpl();
        _elementsAccessors = new ClassCacheImpl();
        _nullHandlers = new ClassCacheImpl();
        _propertyDescriptorCache = new ClassCacheImpl();
        _constructorCache = new ClassCacheImpl();
        _staticMethodCache = new ClassCacheImpl();
        _instanceMethodCache = new ClassCacheImpl();
        _invokePermissionCache = new ClassCacheImpl();
        _fieldCache = new ClassCacheImpl();
        _superclasses = new ArrayList();
        _declaredMethods = new ClassCache[]{new ClassCacheImpl(), new ClassCacheImpl()};
        _primitiveTypes = new HashMap(101);
        _primitiveDefaults = new ClassCacheImpl();
        _methodParameterTypesCache = new HashMap(101);
        _genericMethodParameterTypesCache = new HashMap(101);
        _ctorParameterTypesCache = new HashMap(101);
        _securityManager = System.getSecurityManager();
        _evaluationPool = new EvaluationPool();
        _objectArrayPool = new ObjectArrayPool();
        _methodAccessCache = new ConcurrentHashMap<Method, Boolean>();
        _methodPermCache = new ConcurrentHashMap<Method, Boolean>();
        cacheSetMethod = new ClassPropertyMethodCache();
        cacheGetMethod = new ClassPropertyMethodCache();
        try {
            Class.forName("javassist.ClassPool");
            _compiler = new ExpressionCompiler();
        }
        catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Javassist library is missing in classpath! Please add missed dependency!", e);
        }
        catch (RuntimeException rt) {
            throw new IllegalStateException("Javassist library cannot be loaded, is it restricted by runtime environment?");
        }
        EMPTY_CLASS_ARRAY = new Class[0];
        PRIMITIVE_WRAPPER_CLASSES = new IdentityHashMap();
        PRIMITIVE_WRAPPER_CLASSES.put(Boolean.TYPE, Boolean.class);
        PRIMITIVE_WRAPPER_CLASSES.put(Boolean.class, Boolean.TYPE);
        PRIMITIVE_WRAPPER_CLASSES.put(Byte.TYPE, Byte.class);
        PRIMITIVE_WRAPPER_CLASSES.put(Byte.class, Byte.TYPE);
        PRIMITIVE_WRAPPER_CLASSES.put(Character.TYPE, Character.class);
        PRIMITIVE_WRAPPER_CLASSES.put(Character.class, Character.TYPE);
        PRIMITIVE_WRAPPER_CLASSES.put(Short.TYPE, Short.class);
        PRIMITIVE_WRAPPER_CLASSES.put(Short.class, Short.TYPE);
        PRIMITIVE_WRAPPER_CLASSES.put(Integer.TYPE, Integer.class);
        PRIMITIVE_WRAPPER_CLASSES.put(Integer.class, Integer.TYPE);
        PRIMITIVE_WRAPPER_CLASSES.put(Long.TYPE, Long.class);
        PRIMITIVE_WRAPPER_CLASSES.put(Long.class, Long.TYPE);
        PRIMITIVE_WRAPPER_CLASSES.put(Float.TYPE, Float.class);
        PRIMITIVE_WRAPPER_CLASSES.put(Float.class, Float.TYPE);
        PRIMITIVE_WRAPPER_CLASSES.put(Double.TYPE, Double.class);
        PRIMITIVE_WRAPPER_CLASSES.put(Double.class, Double.TYPE);
        NUMERIC_CASTS = new HashMap();
        NUMERIC_CASTS.put(Double.class, "(double)");
        NUMERIC_CASTS.put(Float.class, "(float)");
        NUMERIC_CASTS.put(Integer.class, "(int)");
        NUMERIC_CASTS.put(Long.class, "(long)");
        NUMERIC_CASTS.put(BigDecimal.class, "(double)");
        NUMERIC_CASTS.put(BigInteger.class, "");
        NUMERIC_VALUES = new HashMap();
        NUMERIC_VALUES.put(Double.class, "doubleValue()");
        NUMERIC_VALUES.put(Float.class, "floatValue()");
        NUMERIC_VALUES.put(Integer.class, "intValue()");
        NUMERIC_VALUES.put(Long.class, "longValue()");
        NUMERIC_VALUES.put(Short.class, "shortValue()");
        NUMERIC_VALUES.put(Byte.class, "byteValue()");
        NUMERIC_VALUES.put(BigDecimal.class, "doubleValue()");
        NUMERIC_VALUES.put(BigInteger.class, "doubleValue()");
        NUMERIC_VALUES.put(Boolean.class, "booleanValue()");
        NUMERIC_LITERALS = new HashMap();
        NUMERIC_LITERALS.put(Integer.class, "");
        NUMERIC_LITERALS.put(Integer.TYPE, "");
        NUMERIC_LITERALS.put(Long.class, "l");
        NUMERIC_LITERALS.put(Long.TYPE, "l");
        NUMERIC_LITERALS.put(BigInteger.class, "d");
        NUMERIC_LITERALS.put(Float.class, "f");
        NUMERIC_LITERALS.put(Float.TYPE, "f");
        NUMERIC_LITERALS.put(Double.class, "d");
        NUMERIC_LITERALS.put(Double.TYPE, "d");
        NUMERIC_LITERALS.put(BigInteger.class, "d");
        NUMERIC_LITERALS.put(BigDecimal.class, "d");
        NUMERIC_DEFAULTS = new HashMap();
        NUMERIC_DEFAULTS.put(Boolean.class, Boolean.FALSE);
        NUMERIC_DEFAULTS.put(Byte.class, new Byte(0));
        NUMERIC_DEFAULTS.put(Short.class, new Short(0));
        NUMERIC_DEFAULTS.put(Character.class, new Character('\u0000'));
        NUMERIC_DEFAULTS.put(Integer.class, new Integer(0));
        NUMERIC_DEFAULTS.put(Long.class, new Long(0L));
        NUMERIC_DEFAULTS.put(Float.class, new Float(0.0f));
        NUMERIC_DEFAULTS.put(Double.class, new Double(0.0));
        NUMERIC_DEFAULTS.put(BigInteger.class, new BigInteger("0"));
        NUMERIC_DEFAULTS.put(BigDecimal.class, new BigDecimal(0.0));
        ArrayPropertyAccessor p = new ArrayPropertyAccessor();
        OgnlRuntime.setPropertyAccessor(Object.class, new ObjectPropertyAccessor());
        OgnlRuntime.setPropertyAccessor(byte[].class, p);
        OgnlRuntime.setPropertyAccessor(short[].class, p);
        OgnlRuntime.setPropertyAccessor(char[].class, p);
        OgnlRuntime.setPropertyAccessor(int[].class, p);
        OgnlRuntime.setPropertyAccessor(long[].class, p);
        OgnlRuntime.setPropertyAccessor(float[].class, p);
        OgnlRuntime.setPropertyAccessor(double[].class, p);
        OgnlRuntime.setPropertyAccessor(Object[].class, p);
        OgnlRuntime.setPropertyAccessor(List.class, new ListPropertyAccessor());
        OgnlRuntime.setPropertyAccessor(Map.class, new MapPropertyAccessor());
        OgnlRuntime.setPropertyAccessor(Set.class, new SetPropertyAccessor());
        OgnlRuntime.setPropertyAccessor(Iterator.class, new IteratorPropertyAccessor());
        OgnlRuntime.setPropertyAccessor(Enumeration.class, new EnumerationPropertyAccessor());
        ArrayElementsAccessor e = new ArrayElementsAccessor();
        OgnlRuntime.setElementsAccessor(Object.class, new ObjectElementsAccessor());
        OgnlRuntime.setElementsAccessor(byte[].class, e);
        OgnlRuntime.setElementsAccessor(short[].class, e);
        OgnlRuntime.setElementsAccessor(char[].class, e);
        OgnlRuntime.setElementsAccessor(int[].class, e);
        OgnlRuntime.setElementsAccessor(long[].class, e);
        OgnlRuntime.setElementsAccessor(float[].class, e);
        OgnlRuntime.setElementsAccessor(double[].class, e);
        OgnlRuntime.setElementsAccessor(Object[].class, e);
        OgnlRuntime.setElementsAccessor(Collection.class, new CollectionElementsAccessor());
        OgnlRuntime.setElementsAccessor(Map.class, new MapElementsAccessor());
        OgnlRuntime.setElementsAccessor(Iterator.class, new IteratorElementsAccessor());
        OgnlRuntime.setElementsAccessor(Enumeration.class, new EnumerationElementsAccessor());
        OgnlRuntime.setElementsAccessor(Number.class, new NumberElementsAccessor());
        ObjectNullHandler nh = new ObjectNullHandler();
        OgnlRuntime.setNullHandler(Object.class, nh);
        OgnlRuntime.setNullHandler(byte[].class, nh);
        OgnlRuntime.setNullHandler(short[].class, nh);
        OgnlRuntime.setNullHandler(char[].class, nh);
        OgnlRuntime.setNullHandler(int[].class, nh);
        OgnlRuntime.setNullHandler(long[].class, nh);
        OgnlRuntime.setNullHandler(float[].class, nh);
        OgnlRuntime.setNullHandler(double[].class, nh);
        OgnlRuntime.setNullHandler(Object[].class, nh);
        ObjectMethodAccessor ma = new ObjectMethodAccessor();
        OgnlRuntime.setMethodAccessor(Object.class, ma);
        OgnlRuntime.setMethodAccessor(byte[].class, ma);
        OgnlRuntime.setMethodAccessor(short[].class, ma);
        OgnlRuntime.setMethodAccessor(char[].class, ma);
        OgnlRuntime.setMethodAccessor(int[].class, ma);
        OgnlRuntime.setMethodAccessor(long[].class, ma);
        OgnlRuntime.setMethodAccessor(float[].class, ma);
        OgnlRuntime.setMethodAccessor(double[].class, ma);
        OgnlRuntime.setMethodAccessor(Object[].class, ma);
        _primitiveTypes.put("boolean", Boolean.TYPE);
        _primitiveTypes.put("byte", Byte.TYPE);
        _primitiveTypes.put("short", Short.TYPE);
        _primitiveTypes.put("char", Character.TYPE);
        _primitiveTypes.put("int", Integer.TYPE);
        _primitiveTypes.put("long", Long.TYPE);
        _primitiveTypes.put("float", Float.TYPE);
        _primitiveTypes.put("double", Double.TYPE);
        _primitiveDefaults.put(Boolean.TYPE, Boolean.FALSE);
        _primitiveDefaults.put(Boolean.class, Boolean.FALSE);
        _primitiveDefaults.put(Byte.TYPE, new Byte(0));
        _primitiveDefaults.put(Byte.class, new Byte(0));
        _primitiveDefaults.put(Short.TYPE, new Short(0));
        _primitiveDefaults.put(Short.class, new Short(0));
        _primitiveDefaults.put(Character.TYPE, new Character('\u0000'));
        _primitiveDefaults.put(Integer.TYPE, new Integer(0));
        _primitiveDefaults.put(Long.TYPE, new Long(0L));
        _primitiveDefaults.put(Float.TYPE, new Float(0.0f));
        _primitiveDefaults.put(Double.TYPE, new Double(0.0));
        _primitiveDefaults.put(BigInteger.class, new BigInteger("0"));
        _primitiveDefaults.put(BigDecimal.class, new BigDecimal(0.0));
        NoArgsReport = new ArgsCompatbilityReport(0, new boolean[0]);
    }

    private static final class ClassPropertyMethodCache {
        private static final Method NULL_REPLACEMENT;
        private final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, Method>> cache = new ConcurrentHashMap();

        ClassPropertyMethodCache() {
        }

        Method get(Class clazz, String propertyName) {
            ConcurrentHashMap<String, Method> methodsByPropertyName = this.cache.get(clazz);
            if (methodsByPropertyName == null) {
                return null;
            }
            Method method = methodsByPropertyName.get(propertyName);
            return method;
        }

        void put(Class clazz, String propertyName, Method method) {
            ConcurrentHashMap<String, Method> old;
            ConcurrentHashMap<String, Method> methodsByPropertyName = this.cache.get(clazz);
            if (methodsByPropertyName == null && null != (old = this.cache.putIfAbsent(clazz, methodsByPropertyName = new ConcurrentHashMap()))) {
                methodsByPropertyName = old;
            }
            methodsByPropertyName.putIfAbsent(propertyName, method == null ? NULL_REPLACEMENT : method);
        }

        void clear() {
            this.cache.clear();
        }

        static {
            try {
                NULL_REPLACEMENT = ClassPropertyMethodCache.class.getDeclaredMethod(OgnlRuntime.GET_PREFIX, Class.class, String.class);
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class MatchingMethod {
        Method mMethod;
        int score;
        ArgsCompatbilityReport report;
        Class[] mParameterTypes;

        private MatchingMethod(Method method, int score, ArgsCompatbilityReport report, Class[] mParameterTypes) {
            this.mMethod = method;
            this.score = score;
            this.report = report;
            this.mParameterTypes = mParameterTypes;
        }
    }

    public static class ArgsCompatbilityReport {
        int score;
        boolean[] conversionNeeded;

        public ArgsCompatbilityReport(int score, boolean[] conversionNeeded) {
            this.score = score;
            this.conversionNeeded = conversionNeeded;
        }
    }
}

