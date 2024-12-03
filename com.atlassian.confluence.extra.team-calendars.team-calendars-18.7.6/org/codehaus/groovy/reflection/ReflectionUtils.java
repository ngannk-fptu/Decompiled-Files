/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ReflectionUtils {
    private static final Set<String> IGNORED_PACKAGES = new HashSet<String>();
    private static final ClassContextHelper HELPER;

    public static boolean isCallingClassReflectionAvailable() {
        return true;
    }

    public static Class getCallingClass() {
        return ReflectionUtils.getCallingClass(1);
    }

    public static Class getCallingClass(int matchLevel) {
        return ReflectionUtils.getCallingClass(matchLevel, Collections.EMPTY_SET);
    }

    public static Class getCallingClass(int matchLevel, Collection<String> extraIgnoredPackages) {
        Class[] classContext = HELPER.getClassContext();
        int depth = 0;
        try {
            Class sc;
            Class c;
            do {
                sc = (c = classContext[depth++]) != null ? c.getSuperclass() : null;
            } while (ReflectionUtils.classShouldBeIgnored(c, extraIgnoredPackages) || ReflectionUtils.superClassShouldBeIgnored(sc) || c != null && matchLevel-- > 0 && depth < classContext.length);
            return c;
        }
        catch (Throwable t) {
            return null;
        }
    }

    private static boolean superClassShouldBeIgnored(Class sc) {
        return sc != null && sc.getPackage() != null && "org.codehaus.groovy.runtime.callsite".equals(sc.getPackage().getName());
    }

    private static boolean classShouldBeIgnored(Class c, Collection<String> extraIgnoredPackages) {
        return c != null && (c.isSynthetic() || c.getPackage() != null && (IGNORED_PACKAGES.contains(c.getPackage().getName()) || extraIgnoredPackages.contains(c.getPackage().getName())));
    }

    static {
        IGNORED_PACKAGES.add("groovy.lang");
        IGNORED_PACKAGES.add("org.codehaus.groovy.reflection");
        IGNORED_PACKAGES.add("org.codehaus.groovy.runtime.callsite");
        IGNORED_PACKAGES.add("org.codehaus.groovy.runtime.metaclass");
        IGNORED_PACKAGES.add("org.codehaus.groovy.runtime");
        IGNORED_PACKAGES.add("sun.reflect");
        IGNORED_PACKAGES.add("java.lang.invoke");
        IGNORED_PACKAGES.add("org.codehaus.groovy.vmplugin.v7");
        HELPER = new ClassContextHelper();
    }

    private static class ClassContextHelper
    extends SecurityManager {
        private ClassContextHelper() {
        }

        public Class[] getClassContext() {
            return super.getClassContext();
        }
    }
}

