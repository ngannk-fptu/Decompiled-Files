/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.discovery.resource;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import org.apache.commons.discovery.Resource;
import org.apache.commons.discovery.ResourceDiscover;
import org.apache.commons.discovery.ResourceIterator;
import org.apache.commons.discovery.jdk.JDKHooks;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.resource.ResourceDiscoverImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DiscoverResources
extends ResourceDiscoverImpl
implements ResourceDiscover {
    private static Log log = LogFactory.getLog(DiscoverResources.class);

    @Deprecated
    public static void setLog(Log _log) {
        log = _log;
    }

    public DiscoverResources() {
    }

    public DiscoverResources(ClassLoaders classLoaders) {
        super(classLoaders);
    }

    public ResourceIterator findResources(final String resourceName) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("find: resourceName='" + resourceName + "'"));
        }
        return new ResourceIterator(){
            private int idx = 0;
            private ClassLoader loader = null;
            private Enumeration<URL> resources = null;
            private Resource resource = null;

            @Override
            public boolean hasNext() {
                if (this.resource == null) {
                    this.resource = this.getNextResource();
                }
                return this.resource != null;
            }

            @Override
            public Resource nextResource() {
                Resource element = this.resource;
                this.resource = null;
                return element;
            }

            private Resource getNextResource() {
                Resource resourceInfo;
                if (this.resources == null || !this.resources.hasMoreElements()) {
                    this.resources = this.getNextResources();
                }
                if (this.resources != null) {
                    URL url = this.resources.nextElement();
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("getNextResource: next URL='" + url + "'"));
                    }
                    resourceInfo = new Resource(resourceName, url, this.loader);
                } else {
                    resourceInfo = null;
                }
                return resourceInfo;
            }

            private Enumeration<URL> getNextResources() {
                while (this.idx < DiscoverResources.this.getClassLoaders().size()) {
                    this.loader = DiscoverResources.this.getClassLoaders().get(this.idx++);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("getNextResources: search using ClassLoader '" + this.loader + "'"));
                    }
                    try {
                        Enumeration<URL> e = JDKHooks.getJDKHooks().getResources(this.loader, resourceName);
                        if (e == null || !e.hasMoreElements()) continue;
                        return e;
                    }
                    catch (IOException ex) {
                        log.warn((Object)"getNextResources: Ignoring Exception", (Throwable)ex);
                    }
                }
                return null;
            }
        };
    }
}

