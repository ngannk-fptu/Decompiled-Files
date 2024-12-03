/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery.jdk;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import org.apache.commons.discovery.jdk.JDK12Hooks;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class JDKHooks {
    private static final JDKHooks jdkHooks = new JDK12Hooks();

    protected JDKHooks() {
    }

    public static final JDKHooks getJDKHooks() {
        return jdkHooks;
    }

    public abstract String getSystemProperty(String var1);

    public abstract ClassLoader getThreadContextClassLoader();

    public abstract ClassLoader getSystemClassLoader();

    public abstract Enumeration<URL> getResources(ClassLoader var1, String var2) throws IOException;
}

