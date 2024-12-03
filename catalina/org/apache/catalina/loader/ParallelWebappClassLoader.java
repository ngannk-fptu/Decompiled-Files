/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.compat.JreCompat
 */
package org.apache.catalina.loader;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.compat.JreCompat;

public class ParallelWebappClassLoader
extends WebappClassLoaderBase {
    private static final Log log = LogFactory.getLog(ParallelWebappClassLoader.class);

    public ParallelWebappClassLoader() {
    }

    public ParallelWebappClassLoader(ClassLoader parent) {
        super(parent);
    }

    public ParallelWebappClassLoader copyWithoutTransformers() {
        ParallelWebappClassLoader result = new ParallelWebappClassLoader(this.getParent());
        super.copyStateWithoutTransformers(result);
        try {
            result.start();
        }
        catch (LifecycleException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }

    static {
        if (!JreCompat.isGraalAvailable() && !ParallelWebappClassLoader.registerAsParallelCapable()) {
            log.warn((Object)sm.getString("webappClassLoaderParallel.registrationFailed"));
        }
    }
}

