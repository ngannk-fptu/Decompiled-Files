/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.werken.xpath.XPath
 */
package org.apache.velocity.anakia;

import com.werken.xpath.XPath;
import java.util.Map;
import java.util.WeakHashMap;

class XPathCache {
    private static final Map XPATH_CACHE = new WeakHashMap();

    private XPathCache() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static XPath getXPath(String xpathString) {
        XPath xpath = null;
        Map map = XPATH_CACHE;
        synchronized (map) {
            xpath = (XPath)XPATH_CACHE.get(xpathString);
            if (xpath == null) {
                xpath = new XPath(xpathString);
                XPATH_CACHE.put(xpathString, xpath);
            }
        }
        return xpath;
    }
}

