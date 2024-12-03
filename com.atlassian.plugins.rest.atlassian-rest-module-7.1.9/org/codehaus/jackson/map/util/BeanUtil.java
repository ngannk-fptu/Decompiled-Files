/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.util;

import org.codehaus.jackson.map.introspect.AnnotatedMethod;

public class BeanUtil {
    public static String okNameForGetter(AnnotatedMethod am) {
        String name = am.getName();
        String str = BeanUtil.okNameForIsGetter(am, name);
        if (str == null) {
            str = BeanUtil.okNameForRegularGetter(am, name);
        }
        return str;
    }

    public static String okNameForRegularGetter(AnnotatedMethod am, String name) {
        if (name.startsWith("get")) {
            if ("getCallbacks".equals(name) ? BeanUtil.isCglibGetCallbacks(am) : "getMetaClass".equals(name) && BeanUtil.isGroovyMetaClassGetter(am)) {
                return null;
            }
            return BeanUtil.manglePropertyName(name.substring(3));
        }
        return null;
    }

    public static String okNameForIsGetter(AnnotatedMethod am, String name) {
        if (name.startsWith("is")) {
            Class<?> rt = am.getRawType();
            if (rt != Boolean.class && rt != Boolean.TYPE) {
                return null;
            }
            return BeanUtil.manglePropertyName(name.substring(2));
        }
        return null;
    }

    public static String okNameForSetter(AnnotatedMethod am) {
        String name = am.getName();
        if (name.startsWith("set")) {
            if ((name = BeanUtil.manglePropertyName(name.substring(3))) == null) {
                return null;
            }
            if ("metaClass".equals(name) && BeanUtil.isGroovyMetaClassSetter(am)) {
                return null;
            }
            return name;
        }
        return null;
    }

    protected static boolean isCglibGetCallbacks(AnnotatedMethod am) {
        String pname;
        Class<?> rt = am.getRawType();
        if (rt == null || !rt.isArray()) {
            return false;
        }
        Class<?> compType = rt.getComponentType();
        Package pkg = compType.getPackage();
        return pkg != null && ((pname = pkg.getName()).startsWith("net.sf.cglib") || pname.startsWith("org.hibernate.repackage.cglib"));
    }

    protected static boolean isGroovyMetaClassSetter(AnnotatedMethod am) {
        Class<?> argType = am.getParameterClass(0);
        Package pkg = argType.getPackage();
        return pkg != null && pkg.getName().startsWith("groovy.lang");
    }

    protected static boolean isGroovyMetaClassGetter(AnnotatedMethod am) {
        Class<?> rt = am.getRawType();
        if (rt == null || rt.isArray()) {
            return false;
        }
        Package pkg = rt.getPackage();
        return pkg != null && pkg.getName().startsWith("groovy.lang");
    }

    protected static String manglePropertyName(String basename) {
        char lower;
        char upper;
        int len = basename.length();
        if (len == 0) {
            return null;
        }
        StringBuilder sb = null;
        for (int i = 0; i < len && (upper = basename.charAt(i)) != (lower = Character.toLowerCase(upper)); ++i) {
            if (sb == null) {
                sb = new StringBuilder(basename);
            }
            sb.setCharAt(i, lower);
        }
        return sb == null ? basename : sb.toString();
    }
}

