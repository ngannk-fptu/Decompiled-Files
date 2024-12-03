/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.discovery.jdk;

import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import org.apache.commons.discovery.jdk.JDKHooks;
import org.apache.commons.discovery.jdk.PsuedoSystemClassLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class JDK12Hooks
extends JDKHooks {
    private static Log log = LogFactory.getLog(JDK12Hooks.class);
    private static final ClassLoader systemClassLoader = JDK12Hooks.findSystemClassLoader();

    @Deprecated
    public static void setLog(Log _log) {
        log = _log;
    }

    @Override
    public String getSystemProperty(final String propName) {
        return AccessController.doPrivileged(new PrivilegedAction<String>(){

            @Override
            public String run() {
                try {
                    return System.getProperty(propName);
                }
                catch (SecurityException se) {
                    return null;
                }
            }
        });
    }

    @Override
    public ClassLoader getThreadContextClassLoader() {
        ClassLoader classLoader;
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        catch (SecurityException e) {
            classLoader = null;
        }
        return classLoader;
    }

    @Override
    public ClassLoader getSystemClassLoader() {
        return systemClassLoader;
    }

    @Override
    public Enumeration<URL> getResources(ClassLoader loader, String resourceName) throws IOException {
        Enumeration<URL> resources;
        URL first = loader.getResource(resourceName);
        if (first == null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Could not find resource: " + resourceName));
            }
            List emptyURL = Collections.emptyList();
            resources = Collections.enumeration(emptyURL);
        } else {
            try {
                resources = loader.getResources(resourceName);
            }
            catch (RuntimeException ex) {
                log.error((Object)("Exception occured during attept to get " + resourceName + " from " + first), (Throwable)ex);
                List emptyURL = Collections.emptyList();
                resources = Collections.enumeration(emptyURL);
            }
            resources = JDK12Hooks.getResourcesFromUrl(first, resources);
        }
        return resources;
    }

    private static Enumeration<URL> getResourcesFromUrl(final URL first, final Enumeration<URL> rest) {
        return new Enumeration<URL>(){
            private boolean firstDone;
            private URL next;
            {
                this.firstDone = first == null;
                this.next = this.getNext();
            }

            @Override
            public URL nextElement() {
                URL o = this.next;
                this.next = this.getNext();
                return o;
            }

            @Override
            public boolean hasMoreElements() {
                return this.next != null;
            }

            private URL getNext() {
                URL n;
                if (!this.firstDone) {
                    this.firstDone = true;
                    n = first;
                } else {
                    n = null;
                    while (rest.hasMoreElements() && n == null) {
                        n = (URL)rest.nextElement();
                        if (first == null || n == null || !n.equals(first)) continue;
                        n = null;
                    }
                }
                return n;
            }
        };
    }

    private static ClassLoader findSystemClassLoader() {
        SecurityManager security;
        ClassLoader classLoader;
        try {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        catch (SecurityException e) {
            classLoader = null;
        }
        if (classLoader == null && (security = System.getSecurityManager()) != null) {
            try {
                security.checkCreateClassLoader();
                classLoader = new PsuedoSystemClassLoader();
            }
            catch (SecurityException securityException) {
                // empty catch block
            }
        }
        return classLoader;
    }
}

