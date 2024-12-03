/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache;

import net.sf.ehcache.Element;

public class CacheEntry {
    private final Object key;
    private final Element element;

    public CacheEntry(Object key, Element element) {
        this.key = key;
        this.element = element;
    }

    public Object getKey() {
        return this.key;
    }

    public Element getElement() {
        return this.element;
    }
}

