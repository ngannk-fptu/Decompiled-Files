/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.name;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.name.Named;
import com.google.inject.name.NamedImpl;
import java.lang.annotation.Annotation;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Names {
    private Names() {
    }

    public static Named named(String name) {
        return new NamedImpl(name);
    }

    public static void bindProperties(Binder binder, Map<String, String> properties) {
        binder = binder.skipSources(Names.class);
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            binder.bind(Key.get(String.class, (Annotation)new NamedImpl(key))).toInstance(value);
        }
    }

    public static void bindProperties(Binder binder, Properties properties) {
        binder = binder.skipSources(Names.class);
        Enumeration<?> e = properties.propertyNames();
        while (e.hasMoreElements()) {
            String propertyName = (String)e.nextElement();
            String value = properties.getProperty(propertyName);
            binder.bind(Key.get(String.class, (Annotation)new NamedImpl(propertyName))).toInstance(value);
        }
    }
}

