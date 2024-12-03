/*
 * Decompiled with CFR 0.152.
 */
package org.xml.sax.helpers;

import org.xml.sax.helpers.SecuritySupport;

class NewInstance {
    private static final boolean DO_FALLBACK = true;
    static /* synthetic */ Class class$org$xml$sax$helpers$NewInstance;

    NewInstance() {
    }

    static Object newInstance(ClassLoader classLoader, String string) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> clazz;
        if (classLoader == null) {
            clazz = Class.forName(string);
        } else {
            try {
                clazz = classLoader.loadClass(string);
            }
            catch (ClassNotFoundException classNotFoundException) {
                classLoader = (class$org$xml$sax$helpers$NewInstance == null ? (class$org$xml$sax$helpers$NewInstance = NewInstance.class$("org.xml.sax.helpers.NewInstance")) : class$org$xml$sax$helpers$NewInstance).getClassLoader();
                clazz = classLoader != null ? classLoader.loadClass(string) : Class.forName(string);
            }
        }
        Object obj = clazz.newInstance();
        return obj;
    }

    static ClassLoader getClassLoader() {
        ClassLoader classLoader = SecuritySupport.getContextClassLoader();
        if (classLoader == null) {
            classLoader = (class$org$xml$sax$helpers$NewInstance == null ? (class$org$xml$sax$helpers$NewInstance = NewInstance.class$("org.xml.sax.helpers.NewInstance")) : class$org$xml$sax$helpers$NewInstance).getClassLoader();
        }
        return classLoader;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

