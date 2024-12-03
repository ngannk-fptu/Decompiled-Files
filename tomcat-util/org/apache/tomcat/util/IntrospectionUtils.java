/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.XReflectionIntrospectionUtils;
import org.apache.tomcat.util.res.StringManager;

public final class IntrospectionUtils {
    private static final Log log = LogFactory.getLog(IntrospectionUtils.class);
    private static final StringManager sm = StringManager.getManager(IntrospectionUtils.class);
    private static final Map<Class<?>, Method[]> objectMethods = new ConcurrentHashMap();

    public static boolean setProperty(Object o, String name, String value) {
        return IntrospectionUtils.setProperty(o, name, value, true, null);
    }

    public static boolean setProperty(Object o, String name, String value, boolean invokeSetProperty) {
        return IntrospectionUtils.setProperty(o, name, value, invokeSetProperty, null);
    }

    public static boolean setProperty(Object o, String name, String value, boolean invokeSetProperty, StringBuilder actualMethod) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("IntrospectionUtils: setProperty(" + o.getClass() + " " + name + "=" + value + ")"));
        }
        if (actualMethod == null && XReflectionIntrospectionUtils.isEnabled()) {
            return XReflectionIntrospectionUtils.setPropertyInternal(o, name, value, invokeSetProperty);
        }
        String setter = "set" + IntrospectionUtils.capitalize(name);
        try {
            Method[] methods = IntrospectionUtils.findMethods(o.getClass());
            Method setPropertyMethodVoid = null;
            Method setPropertyMethodBool = null;
            for (Method item : methods) {
                Class<?>[] paramT = item.getParameterTypes();
                if (!setter.equals(item.getName()) || paramT.length != 1 || !"java.lang.String".equals(paramT[0].getName())) continue;
                item.invoke(o, value);
                if (actualMethod != null) {
                    actualMethod.append(item.getName()).append("(\"").append(IntrospectionUtils.escape(value)).append("\")");
                }
                return true;
            }
            for (Method method : methods) {
                boolean ok = true;
                if (setter.equals(method.getName()) && method.getParameterTypes().length == 1) {
                    Class<?> paramType = method.getParameterTypes()[0];
                    Object[] params = new Object[1];
                    if ("java.lang.Integer".equals(paramType.getName()) || "int".equals(paramType.getName())) {
                        try {
                            params[0] = Integer.valueOf(value);
                        }
                        catch (NumberFormatException ex) {
                            ok = false;
                        }
                        if (actualMethod != null) {
                            actualMethod.append(method.getName()).append("(Integer.valueOf(\"").append(value).append("\"))");
                        }
                    } else if ("java.lang.Long".equals(paramType.getName()) || "long".equals(paramType.getName())) {
                        try {
                            params[0] = Long.valueOf(value);
                        }
                        catch (NumberFormatException ex) {
                            ok = false;
                        }
                        if (actualMethod != null) {
                            actualMethod.append(method.getName()).append("(Long.valueOf(\"").append(value).append("\"))");
                        }
                    } else if ("java.lang.Boolean".equals(paramType.getName()) || "boolean".equals(paramType.getName())) {
                        params[0] = Boolean.valueOf(value);
                        if (actualMethod != null) {
                            actualMethod.append(method.getName()).append("(Boolean.valueOf(\"").append(value).append("\"))");
                        }
                    } else if ("java.net.InetAddress".equals(paramType.getName())) {
                        try {
                            params[0] = InetAddress.getByName(value);
                        }
                        catch (UnknownHostException exc) {
                            if (log.isDebugEnabled()) {
                                log.debug((Object)("IntrospectionUtils: Unable to resolve host name:" + value));
                            }
                            ok = false;
                        }
                        if (actualMethod != null) {
                            actualMethod.append(method.getName()).append("(InetAddress.getByName(\"").append(value).append("\"))");
                        }
                    } else if (log.isDebugEnabled()) {
                        log.debug((Object)("IntrospectionUtils: Unknown type " + paramType.getName()));
                    }
                    if (ok) {
                        method.invoke(o, params);
                        return true;
                    }
                }
                if (!"setProperty".equals(method.getName())) continue;
                if (method.getReturnType() == Boolean.TYPE) {
                    setPropertyMethodBool = method;
                    continue;
                }
                setPropertyMethodVoid = method;
            }
            if (invokeSetProperty && (setPropertyMethodBool != null || setPropertyMethodVoid != null)) {
                if (actualMethod != null) {
                    actualMethod.append("setProperty(\"").append(name).append("\", \"").append(IntrospectionUtils.escape(value)).append("\")");
                }
                Object[] params = new Object[]{name, value};
                if (setPropertyMethodBool != null) {
                    try {
                        return (Boolean)setPropertyMethodBool.invoke(o, params);
                    }
                    catch (IllegalArgumentException biae) {
                        if (setPropertyMethodVoid != null) {
                            setPropertyMethodVoid.invoke(o, params);
                            return true;
                        }
                        throw biae;
                    }
                }
                setPropertyMethodVoid.invoke(o, params);
                return true;
            }
        }
        catch (IllegalAccessException | IllegalArgumentException | SecurityException e) {
            log.warn((Object)sm.getString("introspectionUtils.setPropertyError", name, value, o.getClass()), (Throwable)e);
        }
        catch (InvocationTargetException e) {
            ExceptionUtils.handleThrowable(e.getCause());
            log.warn((Object)sm.getString("introspectionUtils.setPropertyError", name, value, o.getClass()), (Throwable)e);
        }
        return false;
    }

    public static String escape(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '\"') {
                b.append('\\').append('\"');
                continue;
            }
            if (c == '\\') {
                b.append('\\').append('\\');
                continue;
            }
            if (c == '\n') {
                b.append('\\').append('n');
                continue;
            }
            if (c == '\r') {
                b.append('\\').append('r');
                continue;
            }
            b.append(c);
        }
        return b.toString();
    }

    public static Object getProperty(Object o, String name) {
        if (XReflectionIntrospectionUtils.isEnabled()) {
            return XReflectionIntrospectionUtils.getPropertyInternal(o, name);
        }
        String getter = "get" + IntrospectionUtils.capitalize(name);
        String isGetter = "is" + IntrospectionUtils.capitalize(name);
        try {
            Method[] methods = IntrospectionUtils.findMethods(o.getClass());
            Method getPropertyMethod = null;
            for (Method method : methods) {
                Class<?>[] paramT = method.getParameterTypes();
                if (getter.equals(method.getName()) && paramT.length == 0) {
                    return method.invoke(o, (Object[])null);
                }
                if (isGetter.equals(method.getName()) && paramT.length == 0) {
                    return method.invoke(o, (Object[])null);
                }
                if (!"getProperty".equals(method.getName())) continue;
                getPropertyMethod = method;
            }
            if (getPropertyMethod != null) {
                Object[] params = new Object[]{name};
                return getPropertyMethod.invoke(o, params);
            }
        }
        catch (IllegalAccessException | IllegalArgumentException | SecurityException e) {
            log.warn((Object)sm.getString("introspectionUtils.getPropertyError", name, o.getClass()), (Throwable)e);
        }
        catch (InvocationTargetException e) {
            if (e.getCause() instanceof NullPointerException) {
                return null;
            }
            ExceptionUtils.handleThrowable(e.getCause());
            log.warn((Object)sm.getString("introspectionUtils.getPropertyError", name, o.getClass()), (Throwable)e);
        }
        return null;
    }

    @Deprecated
    public static String replaceProperties(String value, Hashtable<Object, Object> staticProp, PropertySource[] dynamicProp) {
        return IntrospectionUtils.replaceProperties(value, staticProp, dynamicProp, null);
    }

    public static String replaceProperties(String value, Hashtable<Object, Object> staticProp, PropertySource[] dynamicProp, ClassLoader classLoader) {
        int pos;
        if (value == null || value.indexOf(36) < 0) {
            return value;
        }
        StringBuilder sb = new StringBuilder();
        int prev = 0;
        while ((pos = value.indexOf(36, prev)) >= 0) {
            if (pos > 0) {
                sb.append(value.substring(prev, pos));
            }
            if (pos == value.length() - 1) {
                sb.append('$');
                prev = pos + 1;
                continue;
            }
            if (value.charAt(pos + 1) != '{') {
                sb.append('$');
                prev = pos + 1;
                continue;
            }
            int endName = value.indexOf(125, pos);
            if (endName < 0) {
                sb.append(value.substring(pos));
                prev = value.length();
                continue;
            }
            String n = value.substring(pos + 2, endName);
            String v = IntrospectionUtils.getProperty(n, staticProp, dynamicProp, classLoader);
            if (v == null) {
                int col = n.indexOf(":-");
                if (col != -1) {
                    String dV = n.substring(col + 2);
                    v = IntrospectionUtils.getProperty(n = n.substring(0, col), staticProp, dynamicProp, classLoader);
                    if (v == null) {
                        v = dV;
                    }
                } else {
                    v = "${" + n + "}";
                }
            }
            sb.append(v);
            prev = endName + 1;
        }
        if (prev < value.length()) {
            sb.append(value.substring(prev));
        }
        return sb.toString();
    }

    private static String getProperty(String name, Hashtable<Object, Object> staticProp, PropertySource[] dynamicProp, ClassLoader classLoader) {
        String v = null;
        if (staticProp != null) {
            v = (String)staticProp.get(name);
        }
        if (v == null && dynamicProp != null) {
            PropertySource propertySource;
            PropertySource[] propertySourceArray = dynamicProp;
            int n = propertySourceArray.length;
            for (int i = 0; i < n && (v = (propertySource = propertySourceArray[i]) instanceof SecurePropertySource ? ((SecurePropertySource)propertySource).getProperty(name, classLoader) : propertySource.getProperty(name)) == null; ++i) {
            }
        }
        return v;
    }

    public static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static void clear() {
        objectMethods.clear();
    }

    public static Method[] findMethods(Class<?> c) {
        Method[] methods = objectMethods.get(c);
        if (methods != null) {
            return methods;
        }
        methods = c.getMethods();
        objectMethods.put(c, methods);
        return methods;
    }

    public static Method findMethod(Class<?> c, String name, Class<?>[] params) {
        Method[] methods;
        for (Method method : methods = IntrospectionUtils.findMethods(c)) {
            if (!method.getName().equals(name)) continue;
            Class<?>[] methodParams = method.getParameterTypes();
            if (params == null) {
                if (methodParams.length != 0) continue;
                return method;
            }
            if (params.length != methodParams.length) continue;
            boolean found = true;
            for (int j = 0; j < params.length; ++j) {
                if (params[j] == methodParams[j]) continue;
                found = false;
                break;
            }
            if (!found) continue;
            return method;
        }
        return null;
    }

    public static Object callMethod1(Object target, String methodN, Object param1, String typeParam1, ClassLoader cl) throws Exception {
        if (target == null || methodN == null || param1 == null) {
            throw new IllegalArgumentException(sm.getString("introspectionUtils.nullParameter"));
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("IntrospectionUtils: callMethod1 " + target.getClass().getName() + " " + param1.getClass().getName() + " " + typeParam1));
        }
        Class[] params = new Class[]{typeParam1 == null ? param1.getClass() : cl.loadClass(typeParam1)};
        Method m = IntrospectionUtils.findMethod(target.getClass(), methodN, params);
        if (m == null) {
            throw new NoSuchMethodException(target.getClass().getName() + " " + methodN);
        }
        try {
            return m.invoke(target, param1);
        }
        catch (InvocationTargetException ie) {
            ExceptionUtils.handleThrowable(ie.getCause());
            throw ie;
        }
    }

    public static Object callMethodN(Object target, String methodN, Object[] params, Class<?>[] typeParams) throws Exception {
        Method m = null;
        m = IntrospectionUtils.findMethod(target.getClass(), methodN, typeParams);
        if (m == null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("IntrospectionUtils: Can't find method " + methodN + " in " + target + " CLASS " + target.getClass()));
            }
            return null;
        }
        try {
            Object o = m.invoke(target, params);
            if (log.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName()).append('.').append(methodN).append('(');
                for (int i = 0; i < params.length; ++i) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    sb.append(params[i]);
                }
                sb.append(')');
                log.debug((Object)("IntrospectionUtils:" + sb.toString()));
            }
            return o;
        }
        catch (InvocationTargetException ie) {
            ExceptionUtils.handleThrowable(ie.getCause());
            throw ie;
        }
    }

    public static Object convert(String object, Class<?> paramType) {
        Object result = null;
        if ("java.lang.String".equals(paramType.getName())) {
            result = object;
        } else if ("java.lang.Integer".equals(paramType.getName()) || "int".equals(paramType.getName())) {
            try {
                result = Integer.valueOf(object);
            }
            catch (NumberFormatException numberFormatException) {}
        } else if ("java.lang.Boolean".equals(paramType.getName()) || "boolean".equals(paramType.getName())) {
            result = Boolean.valueOf(object);
        } else if ("java.net.InetAddress".equals(paramType.getName())) {
            try {
                result = InetAddress.getByName(object);
            }
            catch (UnknownHostException exc) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("IntrospectionUtils: Unable to resolve host name:" + object));
                }
            }
        } else if (log.isDebugEnabled()) {
            log.debug((Object)("IntrospectionUtils: Unknown type " + paramType.getName()));
        }
        if (result == null) {
            throw new IllegalArgumentException(sm.getString("introspectionUtils.conversionError", object, paramType.getName()));
        }
        return result;
    }

    public static boolean isInstance(Class<?> clazz, String type) {
        Class<?>[] ifaces;
        if (type.equals(clazz.getName())) {
            return true;
        }
        for (Class<?> iface : ifaces = clazz.getInterfaces()) {
            if (!IntrospectionUtils.isInstance(iface, type)) continue;
            return true;
        }
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz == null) {
            return false;
        }
        return IntrospectionUtils.isInstance(superClazz, type);
    }

    public static interface PropertySource {
        public String getProperty(String var1);
    }

    public static interface SecurePropertySource
    extends PropertySource {
        public String getProperty(String var1, ClassLoader var2);
    }
}

