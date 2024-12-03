/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.discovery.tools;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.apache.commons.discovery.DiscoveryException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ClassUtils {
    private static Log log = LogFactory.getLog(ClassUtils.class);

    @Deprecated
    public static void setLog(Log _log) {
        log = _log;
    }

    public static String getPackageName(Class<?> clazz) {
        String packageName;
        Package clazzPackage = clazz.getPackage();
        if (clazzPackage != null) {
            packageName = clazzPackage.getName();
        } else {
            String clazzName = clazz.getName();
            packageName = clazzName.substring(0, clazzName.lastIndexOf(46));
        }
        return packageName;
    }

    public static Method findPublicStaticMethod(Class<?> clazz, Class<?> returnType, String methodName, Class<?>[] paramTypes) {
        boolean problem = false;
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName, paramTypes);
        }
        catch (NoSuchMethodException e) {
            problem = true;
            log.debug((Object)("Class " + clazz.getName() + ": missing method '" + methodName + "(...)"), (Throwable)e);
        }
        if (!(problem || Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers()) && method.getReturnType() == returnType)) {
            if (log.isDebugEnabled()) {
                if (!Modifier.isPublic(method.getModifiers())) {
                    log.debug((Object)(methodName + "() is not public"));
                }
                if (!Modifier.isStatic(method.getModifiers())) {
                    log.debug((Object)(methodName + "() is not static"));
                }
                if (method.getReturnType() != returnType) {
                    log.debug((Object)("Method returns: " + method.getReturnType().getName() + "@@" + method.getReturnType().getClassLoader()));
                    log.debug((Object)("Should return:  " + returnType.getName() + "@@" + returnType.getClassLoader()));
                }
            }
            problem = true;
            method = null;
        }
        return method;
    }

    public static <T> T newInstance(Class<T> impl, Class<?>[] paramClasses, Object[] params) throws DiscoveryException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (paramClasses == null || params == null) {
            return impl.newInstance();
        }
        Constructor<T> constructor = impl.getConstructor(paramClasses);
        return constructor.newInstance(params);
    }

    public static void verifyAncestory(Class<?> spi, Class<?> impl) throws DiscoveryException {
        if (spi == null) {
            throw new DiscoveryException("No interface defined!");
        }
        if (impl == null) {
            throw new DiscoveryException("No implementation defined for " + spi.getName());
        }
        if (!spi.isAssignableFrom(impl)) {
            throw new DiscoveryException("Class " + impl.getName() + " does not implement " + spi.getName());
        }
    }
}

