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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DiscoverNamesInSystemProperties
extends ResourceNameDiscoverImpl
implements ResourceNameDiscover {
    private static Log log = LogFactory.getLog(DiscoverNamesInSystemProperties.class);

    @Deprecated
    public static void setLog(Log _log) {
        log = _log;
    }

    public ResourceNameIterator findResourceNames(final String resourceName) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("find: resourceName='" + resourceName + "'"));
        }
        return new ResourceNameIterator(){
            private String resource;
            {
                this.resource = System.getProperty(resourceName);
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

