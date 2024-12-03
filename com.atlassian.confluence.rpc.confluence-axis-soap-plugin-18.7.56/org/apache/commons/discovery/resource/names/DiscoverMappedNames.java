/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.discovery.resource.names;

import java.util.Hashtable;
import java.util.Map;
import org.apache.commons.discovery.ResourceNameDiscover;
import org.apache.commons.discovery.ResourceNameIterator;
import org.apache.commons.discovery.resource.names.ResourceNameDiscoverImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DiscoverMappedNames
extends ResourceNameDiscoverImpl
implements ResourceNameDiscover {
    private static Log log = LogFactory.getLog(DiscoverMappedNames.class);
    private final Map<String, String[]> mapping = new Hashtable<String, String[]>();

    @Deprecated
    public static void setLog(Log _log) {
        log = _log;
    }

    public void map(String fromName, String toName) {
        this.map(fromName, new String[]{toName});
    }

    public void map(String fromName, String[] toNames) {
        this.mapping.put(fromName, toNames);
    }

    public ResourceNameIterator findResourceNames(String resourceName) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("find: resourceName='" + resourceName + "', mapping to constants"));
        }
        final String[] names = this.mapping.get(resourceName);
        return new ResourceNameIterator(){
            private int idx = 0;

            public boolean hasNext() {
                if (names != null) {
                    while (this.idx < names.length && names[this.idx] == null) {
                        ++this.idx;
                    }
                    return this.idx < names.length;
                }
                return false;
            }

            public String nextResourceName() {
                return this.hasNext() ? names[this.idx++] : null;
            }
        };
    }
}

