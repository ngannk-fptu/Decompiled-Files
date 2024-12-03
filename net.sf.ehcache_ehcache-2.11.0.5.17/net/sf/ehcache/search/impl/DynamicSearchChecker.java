/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.search.attribute.DynamicAttributesExtractor;

public class DynamicSearchChecker {
    public static Map<String, ? extends Object> getSearchAttributes(Element e, Set<String> reservedAttrs, DynamicAttributesExtractor extractor) throws SearchException {
        if (extractor == null) {
            return Collections.emptyMap();
        }
        Map dynamic = extractor.attributesFor(e);
        HashSet<String> copy = new HashSet<String>(reservedAttrs);
        copy.add(Query.KEY.getAttributeName());
        copy.add(Query.VALUE.getAttributeName());
        if (copy.removeAll(dynamic.keySet())) {
            throw new SearchException("Dynamic extractor produced attributes already used in static search config");
        }
        return dynamic;
    }
}

