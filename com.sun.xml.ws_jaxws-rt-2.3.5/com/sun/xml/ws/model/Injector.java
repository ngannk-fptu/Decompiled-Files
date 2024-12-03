/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.model;

import com.sun.xml.ws.model.InjectorHelper;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.WebServiceException;

final class Injector {
    private static final Logger LOGGER = Logger.getLogger(Injector.class.getName());
    private static Method defineClass;

    Injector() {
    }

    static synchronized Class inject(ClassLoader cl, String className, byte[] image) {
        try {
            return cl.loadClass(className);
        }
        catch (ClassNotFoundException classNotFoundException) {
            try {
                return (Class)defineClass.invoke((Object)cl, className.replace('/', '.'), image, 0, image.length, Injector.class.getProtectionDomain());
            }
            catch (ReflectiveOperationException e) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "Unable to inject " + className, e);
                }
                throw new WebServiceException((Throwable)e);
            }
        }
    }

    static {
        try {
            defineClass = AccessController.doPrivileged(new PrivilegedAction<Method>(){

                @Override
                public Method run() {
                    return InjectorHelper.getMethod(ClassLoader.class, "defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE, ProtectionDomain.class);
                }
            });
        }
        catch (Throwable t) {
            Logger.getLogger(Injector.class.getName()).log(Level.SEVERE, null, t);
            WebServiceException we = new WebServiceException(t);
            throw we;
        }
    }
}

