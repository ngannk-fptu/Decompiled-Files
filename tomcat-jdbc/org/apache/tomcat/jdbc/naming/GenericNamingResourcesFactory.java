/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.jdbc.naming;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.ClassLoaderUtil;

public class GenericNamingResourcesFactory
implements ObjectFactory {
    private static final Log log = LogFactory.getLog(GenericNamingResourcesFactory.class);

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        if (obj == null || !(obj instanceof Reference)) {
            return null;
        }
        Reference ref = (Reference)obj;
        Enumeration<RefAddr> refs = ref.getAll();
        String type = ref.getClassName();
        Object o = ClassLoaderUtil.loadClass(type, GenericNamingResourcesFactory.class.getClassLoader(), Thread.currentThread().getContextClassLoader()).getConstructor(new Class[0]).newInstance(new Object[0]);
        while (refs.hasMoreElements()) {
            RefAddr addr = refs.nextElement();
            String param = addr.getType();
            String value = null;
            if (addr.getContent() != null) {
                value = addr.getContent().toString();
            }
            if (GenericNamingResourcesFactory.setProperty(o, param, value)) continue;
            log.debug((Object)("Property not configured[" + param + "]. No setter found on[" + o + "]."));
        }
        return o;
    }

    private static boolean setProperty(Object o, String name, String value) {
        block36: {
            if (log.isDebugEnabled()) {
                log.debug((Object)("IntrospectionUtils: setProperty(" + o.getClass() + " " + name + "=" + value + ")"));
            }
            String setter = "set" + GenericNamingResourcesFactory.capitalize(name);
            try {
                int i;
                Method[] methods = o.getClass().getMethods();
                Method setPropertyMethodVoid = null;
                Method setPropertyMethodBool = null;
                for (i = 0; i < methods.length; ++i) {
                    Class<?>[] paramT = methods[i].getParameterTypes();
                    if (!setter.equals(methods[i].getName()) || paramT.length != 1 || !"java.lang.String".equals(paramT[0].getName())) continue;
                    methods[i].invoke(o, value);
                    return true;
                }
                for (i = 0; i < methods.length; ++i) {
                    boolean ok = true;
                    if (setter.equals(methods[i].getName()) && methods[i].getParameterTypes().length == 1) {
                        Class<?> paramType = methods[i].getParameterTypes()[0];
                        Object[] params = new Object[1];
                        if ("java.lang.Integer".equals(paramType.getName()) || "int".equals(paramType.getName())) {
                            try {
                                params[0] = Integer.valueOf(value);
                            }
                            catch (NumberFormatException ex) {
                                ok = false;
                            }
                        } else if ("java.lang.Long".equals(paramType.getName()) || "long".equals(paramType.getName())) {
                            try {
                                params[0] = Long.valueOf(value);
                            }
                            catch (NumberFormatException ex) {
                                ok = false;
                            }
                        } else if ("java.lang.Boolean".equals(paramType.getName()) || "boolean".equals(paramType.getName())) {
                            params[0] = Boolean.valueOf(value);
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
                        } else if (log.isDebugEnabled()) {
                            log.debug((Object)("IntrospectionUtils: Unknown type " + paramType.getName()));
                        }
                        if (ok) {
                            methods[i].invoke(o, params);
                            return true;
                        }
                    }
                    if (!"setProperty".equals(methods[i].getName())) continue;
                    if (methods[i].getReturnType() == Boolean.TYPE) {
                        setPropertyMethodBool = methods[i];
                        continue;
                    }
                    setPropertyMethodVoid = methods[i];
                }
                if (setPropertyMethodBool != null || setPropertyMethodVoid != null) {
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
            catch (IllegalArgumentException ex2) {
                log.warn((Object)("IAE " + o + " " + name + " " + value), (Throwable)ex2);
            }
            catch (SecurityException ex1) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("IntrospectionUtils: SecurityException for " + o.getClass() + " " + name + "=" + value + ")"), (Throwable)ex1);
                }
            }
            catch (IllegalAccessException iae) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("IntrospectionUtils: IllegalAccessException for " + o.getClass() + " " + name + "=" + value + ")"), (Throwable)iae);
                }
            }
            catch (InvocationTargetException ie) {
                Throwable cause = ie.getCause();
                if (cause instanceof ThreadDeath) {
                    throw (ThreadDeath)cause;
                }
                if (cause instanceof VirtualMachineError) {
                    throw (VirtualMachineError)cause;
                }
                if (!log.isDebugEnabled()) break block36;
                log.debug((Object)("IntrospectionUtils: InvocationTargetException for " + o.getClass() + " " + name + "=" + value + ")"), (Throwable)ie);
            }
        }
        return false;
    }

    public static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }
}

