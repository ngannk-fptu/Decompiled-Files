/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.discovery.resource.names;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.discovery.ResourceNameDiscover;
import org.apache.commons.discovery.ResourceNameIterator;
import org.apache.commons.discovery.resource.names.ResourceNameDiscoverImpl;
import org.apache.commons.discovery.tools.ManagedProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DiscoverNamesInAlternateManagedProperties
extends ResourceNameDiscoverImpl
implements ResourceNameDiscover {
    private static Log log = LogFactory.getLog(DiscoverNamesInAlternateManagedProperties.class);
    private final Map<String, String> mapping = new HashMap<String, String>();

    @Deprecated
    public static void setLog(Log _log) {
        log = _log;
    }

    public void addClassToPropertyNameMapping(String className, String propertyName) {
        this.mapping.put(className, propertyName);
    }

    public ResourceNameIterator findResourceNames(String resourceName) {
        final String mappedName = this.mapping.get(resourceName);
        if (log.isDebugEnabled()) {
            if (mappedName == null) {
                log.debug((Object)("find: resourceName='" + resourceName + "', no mapping"));
            } else {
                log.debug((Object)("find: resourceName='" + resourceName + "', lookup property '" + mappedName + "'"));
            }
        }
        return new ResourceNameIterator(){
            private String resource;
            {
                this.resource = mappedName == null ? null : ManagedProperties.getProperty(mappedName);
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

