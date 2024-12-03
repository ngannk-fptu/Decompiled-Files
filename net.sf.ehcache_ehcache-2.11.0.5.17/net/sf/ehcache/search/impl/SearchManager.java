/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.impl;

import java.util.Map;
import java.util.Set;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.search.attribute.DynamicAttributesExtractor;
import net.sf.ehcache.store.StoreQuery;

public interface SearchManager {
    public Results executeQuery(StoreQuery var1, Map<String, AttributeExtractor> var2, DynamicAttributesExtractor var3);

    public void put(String var1, int var2, Element var3, byte[] var4, Map<String, AttributeExtractor> var5, DynamicAttributesExtractor var6);

    public void remove(String var1, Object var2, int var3, boolean var4);

    public void clear(String var1, int var2);

    public Set<Attribute> getSearchAttributes(String var1);
}

