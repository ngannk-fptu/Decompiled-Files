/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.ehcache.Element
 */
package com.atlassian.cache.ehcache.wrapper;

import com.atlassian.cache.ehcache.wrapper.ValueProcessor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.sf.ehcache.Element;

public class WrapperUtils {
    public static List<Object> unwrapAllKeys(Collection<Object> objects, ValueProcessor valueProcessor) {
        ArrayList<Object> result = new ArrayList<Object>(objects.size());
        for (Object object : objects) {
            result.add(valueProcessor.unwrap(object));
        }
        return result;
    }

    public static Element unwrapElement(Element element, ValueProcessor valueProcessor) {
        return element == null ? null : new Element(valueProcessor.unwrap(element.getObjectKey()), valueProcessor.unwrap(element.getObjectValue()), element.getVersion(), element.getCreationTime(), element.getLastAccessTime(), element.getHitCount(), element.usesCacheDefaultLifespan(), element.getTimeToLive(), element.getTimeToIdle(), element.getLastUpdateTime());
    }
}

