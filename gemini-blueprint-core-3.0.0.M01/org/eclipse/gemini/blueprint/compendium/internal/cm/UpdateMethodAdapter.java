/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.ReflectionUtils$MethodCallback
 */
package org.eclipse.gemini.blueprint.compendium.internal.cm;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Dictionary;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.util.internal.ReflectionUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

class UpdateMethodAdapter {
    private static final Log log = LogFactory.getLog(UpdateMethodAdapter.class);
    private final Map methods;

    static Map determineUpdateMethod(final Class<?> target, final String methodName) {
        Assert.notNull(target);
        Assert.notNull((Object)methodName);
        final LinkedHashMap methods = new LinkedHashMap(2);
        final boolean trace = log.isTraceEnabled();
        org.springframework.util.ReflectionUtils.doWithMethods(target, (ReflectionUtils.MethodCallback)new ReflectionUtils.MethodCallback(){

            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                Class<Object> propertiesType;
                Class<?>[] args;
                if (!method.isBridge() && Modifier.isPublic(method.getModifiers()) && Void.TYPE.equals(method.getReturnType()) && methodName.equals(method.getName()) && (args = method.getParameterTypes()) != null && args.length == 1 && ((propertiesType = args[0]).isAssignableFrom(Map.class) || propertiesType.isAssignableFrom(Dictionary.class))) {
                    Method m;
                    if (trace) {
                        log.trace((Object)("Discovered custom method [" + method.toString() + "] on " + target));
                    }
                    if ((m = (Method)methods.get(propertiesType)) != null) {
                        if (trace) {
                            log.trace((Object)("Type " + propertiesType + " already has an associated method [" + m.toString() + "];ignoring " + method));
                        }
                    } else {
                        methods.put(propertiesType, method);
                    }
                }
            }
        });
        return methods;
    }

    static void invokeCustomMethods(Object target, Map methods, Map properties) {
        if (methods != null && !methods.isEmpty()) {
            boolean trace = log.isTraceEnabled();
            Object[] args = new Object[]{properties};
            for (Method method : methods.values()) {
                if (trace) {
                    log.trace((Object)("Invoking listener custom method " + method));
                }
                try {
                    ReflectionUtils.invokeMethod(method, target, args);
                }
                catch (Exception ex) {
                    Exception cause = ReflectionUtils.getInvocationException(ex);
                    log.warn((Object)("Custom method [" + method + "] threw exception when passing properties [" + properties + "]"), (Throwable)cause);
                }
            }
        }
    }

    UpdateMethodAdapter(String methodName, Class<?> type) {
        this.methods = UpdateMethodAdapter.determineUpdateMethod(type, methodName);
    }

    void invoke(Object instance, Map properties) {
        UpdateMethodAdapter.invokeCustomMethods(instance, this.methods, properties);
    }
}

