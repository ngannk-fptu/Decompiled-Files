/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.discovery.resource.names;

import org.apache.commons.discovery.ResourceNameDiscover;
import org.apache.commons.discovery.ResourceNameIterator;
import org.apache.commons.discovery.resource.names.ResourceNameDiscoverImpl;
import org.apache.commons.discovery.tools.ManagedProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DiscoverNamesInManagedProperties
extends ResourceNameDiscoverImpl
implements ResourceNameDiscover {
    private static Log log = LogFactory.getLog(DiscoverNamesInManagedProperties.class);
    private final String _prefix;
    private final String _suffix;

    @Deprecated
    public static void setLog(Log _log) {
        log = _log;
    }

    public DiscoverNamesInManagedProperties() {
        this(null, null);
    }

    public DiscoverNamesInManagedProperties(String prefix, String suffix) {
        this._prefix = prefix;
        this._suffix = suffix;
    }

    public ResourceNameIterator findResourceNames(String resourceName) {
        String name = this._prefix != null && this._prefix.length() > 0 ? this._prefix + resourceName : resourceName;
        if (this._suffix != null && this._suffix.length() > 0) {
            name = name + this._suffix;
        }
        if (log.isDebugEnabled()) {
            if (this._prefix != null && this._suffix != null) {
                log.debug((Object)("find: resourceName='" + resourceName + "' as '" + name + "'"));
            } else {
                log.debug((Object)("find: resourceName = '" + name + "'"));
            }
        }
        final String newResourcName = name;
        return new ResourceNameIterator(){
            private String resource;
            {
                this.resource = ManagedProperties.getProperty(newResourcName);
            }

            public boolean hasNext() {
                return this.resource != null;
            }

            public String nextResourceName() {
                String element = this.resource;
                this.resource = null;
                return element;
            }
        };
    }
}

