/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import net.sf.ehcache.Element;
import net.sf.ehcache.store.AbstractPolicy;

public class LruPolicy
extends AbstractPolicy {
    public static final String NAME = "LRU";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean compare(Element element1, Element element2) {
        return element2.getLastAccessTime() < element1.getLastAccessTime();
    }
}

