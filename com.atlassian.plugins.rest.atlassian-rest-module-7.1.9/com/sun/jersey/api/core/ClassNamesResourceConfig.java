/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.core;

import com.sun.jersey.api.core.DefaultResourceConfig;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ClassNamesResourceConfig
extends DefaultResourceConfig {
    public static final String PROPERTY_CLASSNAMES = "com.sun.jersey.config.property.classnames";

    public ClassNamesResourceConfig(Class ... classes) {
        for (Class c : classes) {
            this.getClasses().add(c);
        }
    }

    public ClassNamesResourceConfig(String ... classNames) {
        super(ClassNamesResourceConfig.getClasses(classNames));
    }

    public ClassNamesResourceConfig(Map<String, Object> props) {
        super(ClassNamesResourceConfig.getClasses(props));
        this.setPropertiesAndFeatures(props);
    }

    private static Set<Class<?>> getClasses(Map<String, Object> props) {
        Object v = props.get(PROPERTY_CLASSNAMES);
        if (v == null) {
            throw new IllegalArgumentException("com.sun.jersey.config.property.classnames property is missing");
        }
        Set<Class<?>> s = ClassNamesResourceConfig.getClasses(v);
        if (s.isEmpty()) {
            throw new IllegalArgumentException("com.sun.jersey.config.property.classnames contains no classes");
        }
        return s;
    }

    private static Set<Class<?>> getClasses(Object param) {
        return ClassNamesResourceConfig.convertToSet(ClassNamesResourceConfig._getClasses(param));
    }

    private static Set<Class<?>> getClasses(String[] elements) {
        return ClassNamesResourceConfig.convertToSet(ClassNamesResourceConfig.getElements(elements, " ,;\n"));
    }

    private static Set<Class<?>> convertToSet(String[] classes) {
        LinkedHashSet s = new LinkedHashSet();
        for (String c : classes) {
            try {
                s.add(ClassNamesResourceConfig.getClassLoader().loadClass(c));
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return s;
    }

    private static String[] _getClasses(Object param) {
        if (param instanceof String) {
            return ClassNamesResourceConfig.getElements(new String[]{(String)param}, " ,;\n");
        }
        if (param instanceof String[]) {
            return ClassNamesResourceConfig.getElements((String[])param, " ,;\n");
        }
        throw new IllegalArgumentException("com.sun.jersey.config.property.classnames must have a property value of type String or String[]");
    }

    private static ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader == null ? ClassNamesResourceConfig.class.getClassLoader() : classLoader;
    }
}

