/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.discovery.resource.classes;

import java.net.URL;
import java.security.CodeSource;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.discovery.ResourceClass;
import org.apache.commons.discovery.ResourceClassDiscover;
import org.apache.commons.discovery.ResourceClassIterator;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.resource.classes.ResourceClassDiscoverImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DiscoverClasses<T>
extends ResourceClassDiscoverImpl<T>
implements ResourceClassDiscover<T> {
    private static Log log = LogFactory.getLog(DiscoverClasses.class);

    @Deprecated
    public static void setLog(Log _log) {
        log = _log;
    }

    public DiscoverClasses() {
    }

    public DiscoverClasses(ClassLoaders classLoaders) {
        super(classLoaders);
    }

    @Override
    public ResourceClassIterator<T> findResourceClasses(final String className) {
        final String resourceName = className.replace('.', '/') + ".class";
        if (log.isDebugEnabled()) {
            log.debug((Object)("find: className='" + className + "'"));
        }
        return new ResourceClassIterator<T>(){
            private final Set<URL> history = new HashSet<URL>();
            private int idx = 0;
            private ResourceClass<T> resource = null;

            @Override
            public boolean hasNext() {
                if (this.resource == null) {
                    this.resource = this.getNextClass();
                }
                return this.resource != null;
            }

            @Override
            public ResourceClass<T> nextResourceClass() {
                ResourceClass element = this.resource;
                this.resource = null;
                return element;
            }

            private ResourceClass<T> getNextClass() {
                while (this.idx < DiscoverClasses.this.getClassLoaders().size()) {
                    ClassLoader loader = DiscoverClasses.this.getClassLoaders().get(this.idx++);
                    URL url = null;
                    try {
                        url = loader.getResource(resourceName);
                    }
                    catch (UnsupportedOperationException e) {
                        // empty catch block
                    }
                    if (url == null) {
                        try {
                            CodeSource codeSource = loader.loadClass(className).getProtectionDomain().getCodeSource();
                            if (codeSource != null) {
                                url = new URL(codeSource.getLocation(), resourceName);
                            }
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }
                    if (url != null) {
                        if (this.history.add(url)) {
                            if (log.isDebugEnabled()) {
                                log.debug((Object)("getNextClass: next URL='" + url + "'"));
                            }
                            return new ResourceClass(className, url, loader);
                        }
                        if (!log.isDebugEnabled()) continue;
                        log.debug((Object)("getNextClass: duplicate URL='" + url + "'"));
                        continue;
                    }
                    if (!log.isDebugEnabled()) continue;
                    log.debug((Object)("getNextClass: loader " + loader + ": '" + resourceName + "' not found"));
                }
                return null;
            }
        };
    }
}

