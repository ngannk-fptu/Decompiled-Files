/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import net.sf.ehcache.Element;

public interface Policy {
    public String getName();

    public Element selectedBasedOnPolicy(Element[] var1, Element var2);

    public boolean compare(Element var1, Element var2);
}

