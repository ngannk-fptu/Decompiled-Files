/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery.jdk;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import org.apache.commons.discovery.jdk.JDKHooks;
import org.apache.commons.discovery.jdk.PsuedoSystemClassLoader;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class JDK11Hooks
extends JDKHooks {
    private static final ClassLoader systemClassLoader = new PsuedoSystemClassLoader();

    @Override
    public String getSystemProperty(String propName) {
        return System.getProperty(propName);
    }

    @Override
    public ClassLoader getThreadContextClassLoader() {
        return null;
    }

    @Override
    public ClassLoader getSystemClassLoader() {
        return systemClassLoader;
    }

    @Override
    public Enumeration<URL> getResources(ClassLoader loader, String resourceName) throws IOException {
        final URL first = loader.getResource(resourceName);
        final Enumeration<URL> rest = loader.getResources(resourceName);
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
}

