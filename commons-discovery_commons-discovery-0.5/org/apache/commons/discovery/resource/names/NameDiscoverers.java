/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.discovery.resource.names;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.discovery.ResourceNameDiscover;
import org.apache.commons.discovery.ResourceNameIterator;
import org.apache.commons.discovery.resource.names.ResourceNameDiscoverImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NameDiscoverers
extends ResourceNameDiscoverImpl
implements ResourceNameDiscover {
    private static Log log = LogFactory.getLog(NameDiscoverers.class);
    private final List<ResourceNameDiscover> discoverers = new ArrayList<ResourceNameDiscover>();

    @Deprecated
    public static void setLog(Log _log) {
        log = _log;
    }

    public void addResourceNameDiscover(ResourceNameDiscover discover) {
        if (discover != null) {
            this.discoverers.add(discover);
        }
    }

    protected ResourceNameDiscover getResourceNameDiscover(int idx) {
        return this.discoverers.get(idx);
    }

    protected int size() {
        return this.discoverers.size();
    }

    public ResourceNameIterator findResourceNames(final String resourceName) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("find: resourceName='" + resourceName + "'"));
        }
        return new ResourceNameIterator(){
            private int idx = 0;
            private ResourceNameIterator iterator = null;

            public boolean hasNext() {
                if (this.iterator == null || !this.iterator.hasNext()) {
                    this.iterator = this.getNextIterator();
                    if (this.iterator == null) {
                        return false;
                    }
                }
                return this.iterator.hasNext();
            }

            public String nextResourceName() {
                return this.iterator.nextResourceName();
            }

            private ResourceNameIterator getNextIterator() {
                while (this.idx < NameDiscoverers.this.size()) {
                    ResourceNameIterator iter;
                    if (!(iter = NameDiscoverers.this.getResourceNameDiscover(this.idx++).findResourceNames(resourceName)).hasNext()) continue;
                    return iter;
                }
                return null;
            }
        };
    }
}

