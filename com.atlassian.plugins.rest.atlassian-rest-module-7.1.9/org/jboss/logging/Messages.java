/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.logging;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import org.jboss.logging.LoggingLocale;

public final class Messages {
    private Messages() {
    }

    public static <T> T getBundle(Class<T> type) {
        return Messages.getBundle(type, LoggingLocale.getLocale());
    }

    public static <T> T getBundle(final Class<T> type, final Locale locale) {
        return AccessController.doPrivileged(new PrivilegedAction<T>(){

            @Override
            public T run() {
                Field field;
                String language = locale.getLanguage();
                String country = locale.getCountry();
                String variant = locale.getVariant();
                Class bundleClass = null;
                if (variant != null && variant.length() > 0) {
                    try {
                        bundleClass = Class.forName(Messages.join(type.getName(), "$bundle", language, country, variant), true, type.getClassLoader()).asSubclass(type);
                    }
                    catch (ClassNotFoundException e) {
                        // empty catch block
                    }
                }
                if (bundleClass == null && country != null && country.length() > 0) {
                    try {
                        bundleClass = Class.forName(Messages.join(type.getName(), "$bundle", language, country, null), true, type.getClassLoader()).asSubclass(type);
                    }
                    catch (ClassNotFoundException e) {
                        // empty catch block
                    }
                }
                if (bundleClass == null && language != null && language.length() > 0) {
                    try {
                        bundleClass = Class.forName(Messages.join(type.getName(), "$bundle", language, null, null), true, type.getClassLoader()).asSubclass(type);
                    }
                    catch (ClassNotFoundException e) {
                        // empty catch block
                    }
                }
                if (bundleClass == null) {
                    try {
                        bundleClass = Class.forName(Messages.join(type.getName(), "$bundle", null, null, null), true, type.getClassLoader()).asSubclass(type);
                    }
                    catch (ClassNotFoundException e) {
                        throw new IllegalArgumentException("Invalid bundle " + type + " (implementation not found)");
                    }
                }
                try {
                    field = bundleClass.getField("INSTANCE");
                }
                catch (NoSuchFieldException e) {
                    throw new IllegalArgumentException("Bundle implementation " + bundleClass + " has no instance field");
                }
                try {
                    return type.cast(field.get(null));
                }
                catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("Bundle implementation " + bundleClass + " could not be instantiated", e);
                }
            }
        });
    }

    private static String join(String interfaceName, String a, String b, String c, String d) {
        StringBuilder build = new StringBuilder();
        build.append(interfaceName).append('_').append(a);
        if (b != null && b.length() > 0) {
            build.append('_');
            build.append(b);
        }
        if (c != null && c.length() > 0) {
            build.append('_');
            build.append(c);
        }
        if (d != null && d.length() > 0) {
            build.append('_');
            build.append(d);
        }
        return build.toString();
    }
}

