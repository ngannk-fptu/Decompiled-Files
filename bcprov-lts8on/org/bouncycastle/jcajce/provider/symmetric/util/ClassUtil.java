/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.AccessController;
import java.security.PrivilegedAction;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class ClassUtil {
    public static Class loadClass(Class sourceClass, final String className) {
        try {
            ClassLoader loader = sourceClass.getClassLoader();
            if (loader != null) {
                return loader.loadClass(className);
            }
            return (Class)AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    try {
                        return Class.forName(className);
                    }
                    catch (Exception exception) {
                        return null;
                    }
                }
            });
        }
        catch (ClassNotFoundException classNotFoundException) {
            return null;
        }
    }
}

