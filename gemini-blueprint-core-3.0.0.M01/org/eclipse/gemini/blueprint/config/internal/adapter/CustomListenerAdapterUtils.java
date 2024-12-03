/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.ReflectionUtils$MethodCallback
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.config.internal.adapter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public abstract class CustomListenerAdapterUtils {
    private static final Log log = LogFactory.getLog(CustomListenerAdapterUtils.class);

    static Map<Class<?>, List<Method>> determineCustomMethods(final Class<?> target, final String methodName, final Class<?>[] possibleArgumentTypes, final boolean onlyPublic) {
        if (!StringUtils.hasText((String)methodName)) {
            return Collections.emptyMap();
        }
        Assert.notEmpty((Object[])possibleArgumentTypes);
        if (System.getSecurityManager() != null) {
            return (Map)AccessController.doPrivileged(new PrivilegedAction<Map<Class<?>, List<Method>>>(){

                @Override
                public Map<Class<?>, List<Method>> run() {
                    return CustomListenerAdapterUtils.doDetermineCustomMethods(target, methodName, possibleArgumentTypes, onlyPublic);
                }
            });
        }
        return CustomListenerAdapterUtils.doDetermineCustomMethods(target, methodName, possibleArgumentTypes, onlyPublic);
    }

    private static Map<Class<?>, List<Method>> doDetermineCustomMethods(final Class<?> target, final String methodName, final Class<?>[] possibleArgumentTypes, final boolean onlyPublic) {
        final LinkedHashMap methods = new LinkedHashMap(3);
        final boolean trace = log.isTraceEnabled();
        ReflectionUtils.doWithMethods(target, (ReflectionUtils.MethodCallback)new ReflectionUtils.MethodCallback(){

            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                if (!method.isBridge() && methodName.equals(method.getName())) {
                    if (onlyPublic && !Modifier.isPublic(method.getModifiers())) {
                        if (trace) {
                            log.trace((Object)("Only public methods are considered; ignoring " + method));
                        }
                        return;
                    }
                    Class<?>[] args = method.getParameterTypes();
                    if (args != null) {
                        if (args.length == 1) {
                            this.addMethod(args[0], method, methods);
                        } else if (args.length == 2) {
                            Class<?> propType = args[1];
                            for (int i = 0; i < possibleArgumentTypes.length; ++i) {
                                Class clazz = possibleArgumentTypes[i];
                                if (!clazz.isAssignableFrom(propType)) continue;
                                this.addMethod(args[0], method, methods);
                            }
                        }
                    }
                }
            }

            private void addMethod(Class<?> key, Method mt, Map<Class<?>, List<Method>> methods2) {
                List<Method> mts;
                if (trace) {
                    log.trace((Object)("discovered custom method [" + mt.toString() + "] on " + target));
                }
                if ((mts = methods2.get(key)) == null) {
                    mts = new ArrayList<Method>(2);
                    methods2.put(key, mts);
                    ReflectionUtils.makeAccessible((Method)mt);
                    mts.add(mt);
                    return;
                }
                if (mts.size() == 1) {
                    Method m = mts.get(0);
                    if (m.getParameterTypes().length == mt.getParameterTypes().length) {
                        if (trace) {
                            log.trace((Object)("Method w/ signature " + this.methodSignature(m) + " has been already discovered; ignoring it"));
                        }
                    } else {
                        ReflectionUtils.makeAccessible((Method)mt);
                        mts.add(mt);
                    }
                }
            }

            private String methodSignature(Method m) {
                StringBuilder sb = new StringBuilder();
                int mod = m.getModifiers();
                if (mod != 0) {
                    sb.append(Modifier.toString(mod) + " ");
                }
                sb.append(m.getReturnType() + " ");
                sb.append(m.getName() + "(");
                Class<?>[] params = m.getParameterTypes();
                for (int j = 0; j < params.length; ++j) {
                    sb.append(params[j]);
                    if (j >= params.length - 1) continue;
                    sb.append(",");
                }
                sb.append(")");
                return sb.toString();
            }
        });
        return methods;
    }

    static Map<Class<?>, List<Method>> determineCustomMethods(Class<?> target, String methodName, boolean onlyPublic) {
        return CustomListenerAdapterUtils.determineCustomMethods(target, methodName, new Class[]{Dictionary.class, Map.class}, onlyPublic);
    }

    static void invokeCustomMethods(Object target, Map<Class<?>, List<Method>> methods, Object service, Map properties) {
        if (methods != null && !methods.isEmpty()) {
            boolean trace = log.isTraceEnabled();
            Object[] argsWMap = new Object[]{service, properties};
            Object[] argsWOMap = new Object[]{service};
            for (Map.Entry<Class<?>, List<Method>> entry : methods.entrySet()) {
                Class<?> key = entry.getKey();
                if (service != null && !key.isInstance(service)) continue;
                List<Method> mts = entry.getValue();
                for (Method method : mts) {
                    Class<?>[] argTypes;
                    if (trace) {
                        log.trace((Object)("Invoking listener custom method " + method));
                    }
                    Object[] arguments = (argTypes = method.getParameterTypes()).length > 1 ? argsWMap : argsWOMap;
                    try {
                        org.eclipse.gemini.blueprint.util.internal.ReflectionUtils.invokeMethod(method, target, arguments);
                    }
                    catch (Exception ex) {
                        Exception cause = org.eclipse.gemini.blueprint.util.internal.ReflectionUtils.getInvocationException(ex);
                        log.warn((Object)("Custom method [" + method + "] threw exception when passing service [" + ObjectUtils.identityToString((Object)service) + "]"), (Throwable)cause);
                    }
                }
            }
        }
    }
}

