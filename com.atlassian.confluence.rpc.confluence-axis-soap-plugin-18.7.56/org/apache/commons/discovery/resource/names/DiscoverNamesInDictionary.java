/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.discovery.resource.names;

import java.util.Dictionary;
import java.util.Hashtable;
import org.apache.commons.discovery.ResourceNameDiscover;
import org.apache.commons.discovery.ResourceNameIterator;
import org.apache.commons.discovery.resource.names.ResourceNameDiscoverImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DiscoverNamesInDictionary
extends ResourceNameDiscoverImpl
implements ResourceNameDiscover {
    private static Log log = LogFactory.getLog(DiscoverNamesInDictionary.class);
    private Dictionary<String, String[]> dictionary;

    @Deprecated
    public static void setLog(Log _log) {
        log = _log;
    }

    public DiscoverNamesInDictionary() {
        this.setDictionary(new Hashtable<String, String[]>());
    }

    public DiscoverNamesInDictionary(Dictionary<String, String[]> dictionary) {
        this.setDictionary(dictionary);
    }

    protected Dictionary<String, String[]> getDictionary() {
        return this.dictionary;
    }

    public void setDictionary(Dictionary<String, String[]> table) {
        this.dictionary = table;
    }

    public void addResource(String resourceName, String resource) {
        this.addResource(resourceName, new String[]{resource});
    }

    public void addResource(String resourceName, String[] resources) {
        this.dictionary.put(resourceName, resources);
    }

    @Override
    public ResourceNameIterator findResourceNames(String resourceName) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("find: resourceName='" + resourceName + "'"));
        }
        final String[] resources = this.dictionary.get(resourceName);
        return new ResourceNameIterator(){
            private int idx = 0;

            public boolean hasNext() {
                if (resources != null) {
                    while (this.idx < resources.length && resources[this.idx] == null) {
                        ++this.idx;
                    }
                    return this.idx < resources.length;
                }
                return false;
            }

            public String nextResourceName() {
                return this.hasNext() ? resources[this.idx++] : null;
            }
        };
    }
}

