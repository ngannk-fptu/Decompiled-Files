/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import net.sf.ehcache.Element;
import net.sf.ehcache.store.Store;

public interface AuthoritativeTier
extends Store {
    public Element fault(Object var1, boolean var2);

    @Deprecated
    public boolean putFaulted(Element var1);

    public void flush(Element var1);
}

