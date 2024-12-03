/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.terracotta;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public interface InternalEhcache
extends Ehcache {
    public Element removeAndReturnElement(Object var1) throws IllegalStateException;

    public void recalculateSize(Object var1);
}

